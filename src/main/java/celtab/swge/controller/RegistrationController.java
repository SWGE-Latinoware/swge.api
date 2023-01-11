package celtab.swge.controller;

import celtab.swge.dto.RegistrationRequestDTO;
import celtab.swge.dto.RegistrationResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Activity;
import celtab.swge.model.Promotion;
import celtab.swge.model.RegistrationType;
import celtab.swge.model.Schedule;
import celtab.swge.model.enums.PaymentType;
import celtab.swge.model.registration.Registration;
import celtab.swge.model.registration.individual_registration.IndividualRegistration;
import celtab.swge.model.registration.individual_registration.IndividualRegistrationSchedule;
import celtab.swge.service.*;
import celtab.swge.util.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static celtab.swge.security.WebSecurity.AdministratorFilter;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/registrations")
public class RegistrationController extends GenericController<Registration, Long, RegistrationRequestDTO, RegistrationResponseDTO> implements DateTimeUtils {
    private final RegistrationService registrationService;

    private final ActivityService activityService;

    private final EditionService editionService;

    private final ScheduleService scheduleService;

    private final VoucherService voucherService;

    private final PromotionService promotionService;

    public RegistrationController(
        RegistrationService registrationService,
        ModelMapper modelMapper,
        ObjectMapper objectMapper,
        ActivityService activityService,
        EditionService editionService,
        ScheduleService scheduleService, VoucherService voucherService, PromotionService promotionService) {
        super(registrationService, modelMapper, objectMapper);
        this.registrationService = registrationService;
        this.activityService = activityService;
        this.editionService = editionService;
        this.scheduleService = scheduleService;
        this.voucherService = voucherService;
        this.promotionService = promotionService;
    }

    private PriceEntry getRegistrationPrice(
        List<Activity> activities,
        Long userId,
        RegistrationType registrationType) throws ControllerException {
        if (registrationType == null) {
            throw new ControllerException(BAD_REQUEST, "No Registration Type Found!");
        }
        if (!isCurrentDateTimeInInterval(registrationType.getInitialDateTime(), registrationType.getFinalDateTime())) {
            throw new ControllerException(BAD_REQUEST, "Registration type interval not valid!");
        }
        var activitiesPrice = activities.stream()
            .mapToDouble(Activity::getPrice)
            .reduce(Double::sum)
            .orElse(0.0);
        var prices = new ArrayList<PriceEntry>();
        var edition = activities.get(0).getTrack().getEdition();
        prices.add(new PriceEntry(edition.getRegistrationType().getPrice(), edition.getRegistrationType().getPrice() + activitiesPrice));
        var promotions = edition.getRegistrationType().getPromotions();
        if (!promotions.isEmpty()) {
            promotions.stream().filter(promotion -> !promotion.getIsVoucher()).forEach(promotion -> {
                if ((promotion.getVacancies() == null || promotion.getRemainingVacancies() > 0) &&
                    (isCurrentDateTimeInInterval(promotion.getInitialDateTime(), promotion.getFinalDateTime()))) {
                    prices.add(new PriceEntry(promotion, edition.getRegistrationType().getPrice() - edition.getRegistrationType().getPrice() * (promotion.getPercentage() / 100.0), edition.getRegistrationType().getPrice()));
                }
            });
        }
        if (edition.hasUserWithCustomCaravanEnrollment(userId, true, true, true)
        ) {
            prices.add(new PriceEntry(0.0, 0.0));
        }
        var registrationPriceEntry = prices
            .stream()
            .min(Comparator
                .comparingDouble(PriceEntry::getFinalPrice)
                .thenComparing(priceEntry -> priceEntry.getPromotion() == null)
            )
            .orElseThrow();
        return new PriceEntry(
            registrationPriceEntry.getPromotion(),
            registrationPriceEntry.getFinalPrice() + activitiesPrice,
            registrationPriceEntry.getOriginalPrice() + activitiesPrice
        );
    }

    @Override
    @PostMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCompletedUser(authentication, #registration.user.id)")
    @Transactional
    public RegistrationResponseDTO create(@RequestBody RegistrationRequestDTO registration) {
        try {
            if (registrationService.findOneByEditionAndUser(registration.getEdition().getId(), registration.getUser().getId()) != null) {
                throw new ControllerException(CONFLICT, "User already registered!");
            }
            var edition = editionService.findOne(registration.getEdition().getId());
            if (edition == null) {
                throw new ControllerException(NOT_FOUND, "Edition not found!");
            }
            if (edition.hasUserWithCustomCaravanEnrollment(registration.getUser().getId(), true, true, false)) {
                throw new ControllerException(CONFLICT, "User is a member of a caravan but it has not been paid yet.");
            }
            registration.getIndividualRegistrations().forEach(
                individualRegistration -> individualRegistration.setRegistration(registration)
            );
            var voucher = voucherService.findOneByEditionAndUser(edition.getId(), registration.getUser().getEmail());
            var realRequest = mapTo(registration, Registration.class);
            realRequest.setId(null);
            realRequest.setPayed(voucher != null);
            var defaultActivities = activityService.findAllLecturesByEdition(registration.getEdition().getId());
            var realActivities = activityService
                .findAllById(
                    registration.getIndividualRegistrations().stream().map(
                        individualRegistration -> individualRegistration.getActivity().getId()
                    ).collect(Collectors.toList())
                );
            var allActivities = new HashSet<>(defaultActivities);
            allActivities.addAll(realActivities);
            if (allActivities.isEmpty()) {
                throw new ControllerException(BAD_REQUEST, "No Activities Selected!");
            }
            var finalPrice = getRegistrationPrice(
                List.copyOf(allActivities),
                registration.getUser().getId(),
                allActivities.stream().findAny().orElseThrow().getTrack().getEdition().getRegistrationType()
            );
            if (voucher == null) {

                realRequest.setFinalPrice(finalPrice.getFinalPrice());
                realRequest.setPromotion(finalPrice.getPromotion());
            } else {
                realRequest.setFinalPrice(0.0);
                var voucherPromotion = edition.getRegistrationType().getPromotions().stream().filter(Promotion::getIsVoucher).collect(Collectors.toList());
                if (voucherPromotion.isEmpty()) {
                    var newVoucher = new Promotion();
                    var registrationType = edition.getRegistrationType();
                    newVoucher.setVacancies(999999);
                    newVoucher.setPercentage(100.0);
                    newVoucher.setIsVoucher(true);
                    newVoucher.setInitialDateTime(registrationType.getInitialDateTime());
                    newVoucher.setFinalDateTime(registrationType.getFinalDateTime());
                    newVoucher.setRegistrationType(registrationType);
                    promotionService.save(newVoucher);
                    realRequest.setPromotion(newVoucher);
                } else {
                    realRequest.setPromotion(voucherPromotion.get(0));
                }
            }
            realRequest.setOriginalPrice(finalPrice.getOriginalPrice());

            realRequest.setPaymentType(PaymentType.NONE);

            realRequest.setIndividualRegistrations(
                allActivities
                    .stream()
                    .map(activity -> new IndividualRegistration(activity, realRequest))
                    .collect(Collectors.toList())
            );
            if (!allActivities.stream().allMatch(Activity::hasVacancies)) {
                throw new ControllerException(CONFLICT, "Activity full!");
            }
            return mapTo(service.save(realRequest), RegistrationResponseDTO.class);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @GetMapping("/{id}")
    public RegistrationResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

    @GetMapping("/edition/{editionId}/registration/{userId}")
    public RegistrationResponseDTO findOne(
        @PathVariable Long editionId,
        @PathVariable Long userId
    ) {
        var registration = registrationService.findOneByEditionAndUser(editionId, userId);
        if (registration == null) {
            throw new ControllerException(NOT_FOUND, "It was not possible to fetch the registration!");
        }
        return mapTo(registration, RegistrationResponseDTO.class);
    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCompletedUserRegistration(authentication, #id)")
    @Transactional
    public void cancelRegistration(@PathVariable Long id) {
        findOne(id);
        delete(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @AdministratorFilter
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @AdministratorFilter
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping("/filter")
    @AdministratorFilter
    public Page<RegistrationResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @PutMapping("/schedule-list/{registrationId}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUserRegistration(authentication, #registrationId)")
    @Transactional
    public RegistrationResponseDTO updateUserScheduleList(@PathVariable Long registrationId, @RequestParam List<Long> schedulesId) {
        try {
            var registration = registrationService.findOne(registrationId);

            if (registration == null) {
                throw new ControllerException(NOT_FOUND, "Registration not found!");
            }

            var activSchedules = schedulesId.isEmpty() ? new ArrayList<Schedule>() : scheduleService.findAllById(schedulesId);

            if (activSchedules.size() != schedulesId.size()) {
                throw new ControllerException(NOT_FOUND, "Some of the schedules were not found!");
            }

            registration
                .getIndividualRegistrations()
                .forEach(individualRegistration -> {
                    individualRegistration.getIndividualRegistrationScheduleList().clear();

                    activSchedules.forEach(selectedSche -> {
                        if (Objects.equals(individualRegistration.getActivity().getId(), selectedSche.getActivity().getId())) {
                            var indivSched = new IndividualRegistrationSchedule();
                            indivSched.setSchedule(selectedSche);
                            indivSched.setIndividualRegistration(individualRegistration);
                            individualRegistration.getIndividualRegistrationScheduleList().add(indivSched);
                        }
                    });

                    individualRegistration.setCustomSchedule(true);
                });

            return mapTo(registrationService.save(registration), RegistrationResponseDTO.class);
        } catch (ControllerException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/complete-registration")
    @PreAuthorize("@webSecurity.isAdministratorOrSecretary(authentication)")
    @Transactional
    public void completeRegistration(@PathVariable Long id, @RequestParam Boolean exempt) {
        var registration = registrationService.findOne(id);
        if (registration == null) throw new ControllerException(NOT_FOUND, "Registration not Found!");
        if (Boolean.TRUE.equals(exempt)) {
            registration.setFinalPrice(0d);
            registration.setPromotion(null);
        }
        registration.setPayed(true);
        registration.setPaymentType(PaymentType.LOCAL);
    }

    @Getter
    @RequiredArgsConstructor
    static class PriceEntry {

        private final Promotion promotion;

        private final Double finalPrice;

        private final Double originalPrice;

        public PriceEntry(Double finalPrice, Double originalPrice) {
            this.finalPrice = finalPrice;
            this.originalPrice = originalPrice;
            promotion = null;
        }

    }
}
