package celtab.swge.controller;

import celtab.swge.dto.GenericDTO;
import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ControllerException;
import celtab.swge.exception.ServiceException;
import celtab.swge.model.GenericModel;
import celtab.swge.service.GenericService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.ParameterizedType;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
public abstract class GenericController<T extends GenericModel<G>, G, J extends GenericDTO<G>, K extends GenericDTO<G>> {

    protected final GenericService<T, G> service;

    protected final ModelMapper modelMapper;

    protected final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    private final Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    @SuppressWarnings("unchecked")
    private final Class<K> kClass = (Class<K>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];

    public <L> L mapTo(Object source, Class<L> destinationClass) {
        return modelMapper.map(source, destinationClass);
    }

    public <L> List<L> mapTo(List<?> source, Class<L> destinationClass) {
        return source
            .stream()
            .map(request -> mapTo(request, destinationClass))
            .collect(Collectors.toList());
    }

    public <L> Page<L> mapTo(Page<?> source, Class<L> destinationClass) {
        return source.map(request -> mapTo(request, destinationClass));
    }

    @Transactional
    public K create(@RequestBody J request) {
        try {
            var realRequest = mapTo(request, tClass);
            realRequest.setId(null);
            return mapTo(service.save(realRequest), kClass);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public K update(@RequestBody J request) {
        try {
            var realRequest = mapTo(request, tClass);
            if (service.findOne(realRequest.getId()) == null) {
                throw new ControllerException(NOT_FOUND, service.getCustomModelNameMessage() + " Not Found!");
            }
            return mapTo(service.save(realRequest), kClass);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public void delete(@PathVariable G id) {
        try {
            service.delete(id);
        } catch (ServiceException e) {
            throw new ControllerException(CONFLICT, e.getMessage());
        }
    }

    @Transactional
    public void deleteAll(@RequestParam List<G> ids) {
        try {
            service.deleteAll(ids);
        } catch (ServiceException e) {
            throw new ControllerException(CONFLICT, e.getMessage());
        }
    }

    public Page<K> findAll(Pageable pageable) {
        var requests = service.findAll(pageable);
        return mapTo(requests, kClass);
    }

    public List<K> findAll() {
        var requests = service.findAll();
        return mapTo(requests, kClass);
    }

    public List<K> findAllById(@RequestParam(value = "ids") List<G> ids) {
        var requests = service.findAllById(ids);
        return mapTo(requests, kClass);
    }

    public Page<K> filter(@RequestParam(value = "filter") String filter, Pageable pageable) {
        try {
            var realFilter = URLDecoder.decode(filter, StandardCharsets.UTF_8);
            var genericFilter = objectMapper.readValue(realFilter, GenericFilterDTO.class);
            var requests = service.filter(genericFilter, pageable);
            return mapTo(requests, kClass);
        } catch (Exception e) {
            throw new ControllerException(BAD_REQUEST, e.getMessage());
        }
    }

    public K findOne(@PathVariable G id) {
        var request = service.findOne(id);
        if (request == null) {
            throw new ControllerException(NOT_FOUND, service.getCustomModelNameMessage() + " Not Found!");
        }
        return mapTo(request, kClass);
    }
}
