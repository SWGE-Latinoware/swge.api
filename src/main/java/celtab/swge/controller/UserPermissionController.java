package celtab.swge.controller;

import celtab.swge.dto.UserPermissionRequestDTO;
import celtab.swge.dto.UserPermissionResponseDTO;
import celtab.swge.model.UserPermission;
import celtab.swge.service.UserPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user-permissions")
public class UserPermissionController extends GenericController<UserPermission, Long, UserPermissionRequestDTO, UserPermissionResponseDTO> {

    private final UserPermissionService userPermissionService;

    public UserPermissionController(UserPermissionService userPermissionService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        super(userPermissionService, modelMapper, objectMapper);
        this.userPermissionService = userPermissionService;
    }

    @Override
    @PostMapping
    @Transactional
    public UserPermissionResponseDTO create(@RequestBody UserPermissionRequestDTO userPermission) {
        return super.create(userPermission);
    }

    @Override
    @PutMapping
    @Transactional
    public UserPermissionResponseDTO update(@RequestBody UserPermissionRequestDTO userPermission) {
        return super.update(userPermission);
    }

    @Override
    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        super.delete(id);
    }


    @GetMapping("/edition/{editionId}/user-permissions/{userId}")
    public List<UserPermissionResponseDTO> findAll(@PathVariable Long editionId, @PathVariable Long userId) {
        return mapTo(userPermissionService.findAllByEditionAndUser(editionId, userId), UserPermissionResponseDTO.class);
    }

    @Override
    @GetMapping("/{id}")
    public UserPermissionResponseDTO findOne(@PathVariable Long id) {
        return super.findOne(id);
    }
}
