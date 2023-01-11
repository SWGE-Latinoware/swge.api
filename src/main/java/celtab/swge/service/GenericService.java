package celtab.swge.service;

import celtab.swge.dto.GenericFilterDTO;
import celtab.swge.exception.ServiceException;
import celtab.swge.repository.GenericRepository;
import celtab.swge.specification.GenericSpecification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Service
public abstract class GenericService<T, G> {

    protected final GenericRepository<T, G> repository;

    protected final String customModelNameMessage;

    protected GenericSpecification<T> specification;

    @Transactional
    public T save(T model) {
        try {
            return repository.saveAndFlush(model);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("It was not possible to save the " + customModelNameMessage + "!");
        }
    }

    @Transactional
    public void delete(G id) {
        try {
            repository.deleteById(id);
        } catch (RuntimeException e) {
            throw new ServiceException("It was not possible to delete the " + customModelNameMessage + "!");
        }
    }

    @Transactional
    public void deleteAll(List<G> ids) {
        try {
            repository.deleteAllById(ids);
        } catch (RuntimeException e) {
            throw new ServiceException("It was not possible to delete the " + customModelNameMessage + "!");
        }
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<T> findAllById(List<G> ids) {
        return repository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public Page<T> filter(GenericFilterDTO genericFilterDTO, Pageable pageable) {
        var spec = specification.getByQueryAndFilters(genericFilterDTO);
        return repository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public T findOne(G id) {
        if (id == null) return null;
        return repository.findById(id).orElse(null);
    }
}
