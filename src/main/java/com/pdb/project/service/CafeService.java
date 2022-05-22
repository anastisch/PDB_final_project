package com.pdb.project.service;

import com.pdb.project.model.Cafe;
import com.pdb.project.payload.request.CafeRequest;
import com.pdb.project.repository.CafeRepository;
import com.pdb.project.utils.DTOMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CafeService {
    @Value("${PDB.app.itemsOnPage}")
    private int itemsOnPage;
    private final CafeRepository repository;
    private final DTOMapper mapper;

    public CafeService(CafeRepository repository,
                       DTOMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    /** создание кофеен из API */
    public boolean create(Cafe cafe) {
        if (!(cafe.getIdApi() != null && repository.existsByIdApi(cafe.getIdApi()))) {
            repository.save(cafe);
            return true;
        }
        return false;
    }

    /** создание собственных кофеен */
    @Transactional
    public void create(CafeRequest cafeRequest) {
        Cafe cafe = mapper.toCafe(cafeRequest);
        repository.save(cafe);
    }

    public Page<Cafe> getPage(int page) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        return repository.findAll(pageable);
    }

    public void update(long id, CafeRequest cafeRequest) {
        Cafe cafeToBeUpdated = getById(id);
        cafeToBeUpdated = mapper.fillCafeFromDTO(cafeToBeUpdated, cafeRequest);
        repository.save(cafeToBeUpdated);
    }
    public void delete(long id) {
        repository.deleteById(id);
    }

    public Cafe getById(long id) {
        return repository.findById(id).orElseThrow(RuntimeException::new);
    }
}