package celtab.swge.controller;

import celtab.swge.dto.EditionRequestDTO;
import celtab.swge.dto.VoucherRequestDTO;
import celtab.swge.dto.VoucherResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Edition;
import celtab.swge.model.Voucher;
import celtab.swge.model.user.User;
import celtab.swge.security.WebSecurity;
import celtab.swge.service.GenericService;
import celtab.swge.service.VoucherService;
import celtab.swge.util.ClassPathUtils;
import celtab.swge.util.UUIDUtils;
import celtab.swge.util.email.EmailOptions;
import celtab.swge.util.email.EmailSender;
import celtab.swge.util.template_processor.EmailTemplateProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/vouchers")
public class VoucherController extends GenericController<Voucher, Long, VoucherRequestDTO, VoucherResponseDTO> implements ClassPathUtils, UUIDUtils {

    private final VoucherService voucherService;

    private final EmailSender emailSender;

    private final EmailTemplateProcessor emailTemplateProcessor;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public VoucherController(GenericService<Voucher, Long> service, ModelMapper modelMapper, ObjectMapper objectMapper, VoucherService voucherService, EmailSender emailSender, EmailTemplateProcessor emailTemplateProcessor) {
        super(service, modelMapper, objectMapper);
        this.voucherService = voucherService;
        this.emailSender = emailSender;
        this.emailTemplateProcessor = emailTemplateProcessor;
    }

    private void sendAssignedVoucher(String userEmail, EditionRequestDTO edition) throws ControllerException {
        try {
            var options = new EmailOptions();
            options.setFrom(emailFrom);
            options.setTo(userEmail);
            options.setSubject("Voucher Atribuído");
            options.setIsTextHTML(true);

            var user = new User();
            user.setEmail(userEmail);

            var html = getContentFromClassPath("mail/voucherAddToUser.html");
            emailTemplateProcessor.setEdition(mapTo(edition, Edition.class));
            emailTemplateProcessor.setTo(user);
            options.setText(emailTemplateProcessor.processTemplate(html));
            emailSender.sendEmail(options);
        } catch (Exception e) {
            throw new ControllerException(INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    @PostMapping
    @WebSecurity.AdministratorFilter
    @Transactional
    public VoucherResponseDTO create(@RequestBody VoucherRequestDTO voucher) {
        try {
            voucher.setVoucherHash(getRandomUUIDString());
            if (voucherService.findOneByEditionAndUser(voucher.getEdition().getId(), voucher.getUserEmail()) == null) {
                sendAssignedVoucher(voucher.getUserEmail(), voucher.getEdition());
                return super.create(voucher);
            }
            throw new ControllerException(CONFLICT, "There is already a voucher in this edition assigned to this user");
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @PutMapping
    @WebSecurity.AdministratorFilter
    @Transactional
    public VoucherResponseDTO update(@RequestBody VoucherRequestDTO voucher) {
        try {
            return super.update(voucher);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @WebSecurity.AdministratorFilter
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }

    @Override
    @DeleteMapping
    @WebSecurity.AdministratorFilter
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        super.deleteAll(ids);
    }

    @Override
    @GetMapping
    public Page<VoucherResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    @GetMapping("/list")
    public List<VoucherResponseDTO> findAll() {
        return super.findAll();
    }

    @Override
    @GetMapping("/filter")
    public Page<VoucherResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public VoucherResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }
}
