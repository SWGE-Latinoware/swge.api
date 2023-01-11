package celtab.swge.controller;

import celtab.swge.dto.PromotionRequestDTO;
import celtab.swge.dto.RegistrationTypeRequestDTO;
import celtab.swge.dto.RegistrationTypeResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.RegistrationType;
import celtab.swge.service.RegistrationTypeService;
import celtab.swge.util.DateTimeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/registration-types")
public class RegistrationTypeController extends GenericController<RegistrationType, Long, RegistrationTypeRequestDTO, RegistrationTypeResponseDTO> implements DateTimeUtils {

    public RegistrationTypeController(RegistrationTypeService registrationTypeService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(registrationTypeService, modelMapper, objectMapper);
    }

    private void validatePromotion(RegistrationTypeRequestDTO registration) throws ControllerException {
        if (!registration.getPromotions().stream().allMatch(promotions -> isIntervalInInterval(
            promotions.getInitialDateTime(),
            promotions.getFinalDateTime(),
            registration.getInitialDateTime(),
            registration.getFinalDateTime()
        ))) {
            throw new ControllerException(BAD_REQUEST, "Promotion out of the Registration Type interval");
        }
    }

    @Override
    @PostMapping
    @Transactional
    public RegistrationTypeResponseDTO create(@RequestBody RegistrationTypeRequestDTO registrationType) {
        try {
            var voucherPromotion = new PromotionRequestDTO();
            voucherPromotion.setVacancies(999999);
            voucherPromotion.setPercentage(100.0);
            voucherPromotion.setIsVoucher(true);
            voucherPromotion.setInitialDateTime(registrationType.getInitialDateTime());
            voucherPromotion.setFinalDateTime(registrationType.getFinalDateTime());
            voucherPromotion.setRegistrationType(registrationType);

            registrationType.getPromotions().forEach(promotion -> {
                promotion.setRegistrationType(registrationType);
                promotion.setIsVoucher(false);
            });

            var promotions = registrationType.getPromotions();
            promotions.add(voucherPromotion);

            registrationType.setPromotions(promotions);
            validatePromotion(registrationType);
            return super.create(registrationType);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @PutMapping
    @Transactional
    public RegistrationTypeResponseDTO update(@RequestBody RegistrationTypeRequestDTO registrationType) {
        try {
            validatePromotion(registrationType);
            return super.update(registrationType);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @GetMapping("/{id}")
    public RegistrationTypeResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

}
