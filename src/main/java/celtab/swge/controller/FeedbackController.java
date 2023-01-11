package celtab.swge.controller;

import celtab.swge.dto.FeedbackRequestDTO;
import celtab.swge.dto.FeedbackResponseDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.model.Feedback;
import celtab.swge.model.enums.FeedbackStatus;
import celtab.swge.security.WebSecurity.AdministratorFilter;
import celtab.swge.service.FeedbackService;
import celtab.swge.service.FileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController extends GenericController<Feedback, Long, FeedbackRequestDTO, FeedbackResponseDTO> {

    private final FileService fileService;

    public FeedbackController(FeedbackService feedbackService, ModelMapper modelMapper, ObjectMapper objectMapper, FileService fileService) {
        super(feedbackService, modelMapper, objectMapper);
        this.fileService = fileService;
    }

    @Override
    @PostMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUser(authentication, #feedback.user.id)")
    @Transactional
    public FeedbackResponseDTO create(@RequestBody FeedbackRequestDTO feedback) {
        try {
            feedback.setStatus(FeedbackStatus.OPEN);
            return super.create(feedback);
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    @PutMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnUser(authentication, #feedback.user.id)")
    @Transactional
    public FeedbackResponseDTO update(@RequestBody FeedbackRequestDTO feedback) {
        return super.update(feedback);
    }

    @Override
    @GetMapping
    public Page<FeedbackResponseDTO> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @Override
    @GetMapping("/list")
    public List<FeedbackResponseDTO> findAll() {
        return super.findAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @AdministratorFilter
    @Transactional
    public void delete(@PathVariable Long id) {
        try {
            var feedback = service.findOne(id);
            if (feedback == null)
                throw new ControllerException(NOT_FOUND, "Feedback Not Found!");
            service.delete(id);
            if (feedback.getFile() != null) {
                fileService.removeFile(feedback.getFile().getId());
            }
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, "It Was Not Possible To Delete the Feedback!");
        }
    }

    @Override
    @DeleteMapping
    @AdministratorFilter
    @Transactional
    public void deleteAll(@RequestParam List<Long> ids) {
        try {
            var feedbacks = service.findAllById(ids);
            feedbacks.forEach(feedback -> {
                service.delete(feedback.getId());
                if (feedback.getFile() != null) {
                    fileService.removeFile(feedback.getFile().getId());
                }
            });
        } catch (ControllerException e) {
            throw e;
        } catch (Exception e) {
            throw new ControllerException(CONFLICT, "It Was Not Possible To Delete All The Feedbacks!");
        }
    }

    @Override
    @GetMapping("/filter")
    public Page<FeedbackResponseDTO> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        return super.filter(filter, pageable);
    }

    @Override
    @GetMapping("/{id}")
    public FeedbackResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }

}
