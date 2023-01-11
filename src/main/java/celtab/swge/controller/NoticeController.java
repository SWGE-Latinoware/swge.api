package celtab.swge.controller;

import celtab.swge.dto.NoticeRequestDTO;
import celtab.swge.dto.NoticeResponseDTO;
import celtab.swge.model.Notice;
import celtab.swge.service.NoticeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notices")
public class NoticeController extends GenericController<Notice, Long, NoticeRequestDTO, NoticeResponseDTO> {

    public NoticeController(NoticeService noticeService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(noticeService, modelMapper, objectMapper);
    }

    @Override
    @PostMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #notice.caravan.id)")
    @Transactional
    public NoticeResponseDTO create(@RequestBody NoticeRequestDTO notice) {
        return super.create(notice);
    }

    @Override
    @PutMapping
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #notice.caravan.id)")
    @Transactional
    public NoticeResponseDTO update(@RequestBody NoticeRequestDTO notice) {
        return super.update(notice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@webSecurity.isAdministratorOrOwnCaravanCoordinator(authentication, #caravanId)")
    @Transactional
    public void delete(@PathVariable Long id, @RequestParam(required = false) Long caravanId) {
        super.delete(id);
    }
}
