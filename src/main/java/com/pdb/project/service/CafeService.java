package com.pdb.project.service;

import com.pdb.project.model.*;
import com.pdb.project.payload.request.CafeRequest;
import com.pdb.project.payload.request.GradeRequest;
import com.pdb.project.payload.response.MessageResponse;
import com.pdb.project.repository.CafeRepository;
import com.pdb.project.repository.GradeRepository;
import com.pdb.project.repository.PerkRepository;
import com.pdb.project.utils.DTOMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class CafeService {
    @Value("${PDB.app.itemsOnPage}")
    private int itemsOnPage;
    private final CafeRepository cafeRepository;
    private final GradeRepository gradeRepository;
    private final PerkRepository perkRepository;
    private final DTOMapper mapper;

    public CafeService(CafeRepository repository,
                       GradeRepository gradeRepository,
                       PerkRepository perkRepository,
                       DTOMapper mapper) {
        this.cafeRepository = repository;
        this.gradeRepository = gradeRepository;
        this.perkRepository = perkRepository;
        this.mapper = mapper;
    }

    /** создание кофеен из API */
    public boolean create(Cafe cafe) {
        if (!(cafe.getIdApi() != null && cafeRepository.existsByIdApi(cafe.getIdApi()))) {
            cafeRepository.save(cafe);
            return true;
        }
        return false;
    }

    /** создание собственных кофеен */
    @Transactional
    public void create(CafeRequest cafeRequest) {
        Cafe cafe = mapper.toCafe(cafeRequest);
        cafeRepository.save(cafe);
    }

    public Page<Cafe> getPage(int page) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        return cafeRepository.findAll(pageable);
    }

    public int getPageCount(String location, Double dist, boolean confirmed) {
        if (confirmed) {
            Double lat = Double.parseDouble(location.split(",")[0]);
            Double lng = Double.parseDouble(location.split(",")[1]);
            int cafeCount = cafeRepository.countNearbyCoffeeShops(lat, lng, dist);
            return (int)Math.ceil((double) cafeCount/itemsOnPage);
        }
        else {
            int cafeCount = cafeRepository.countUnconfirmedCoffeeShops();
            return (int)Math.ceil((double) cafeCount/itemsOnPage);
        }
    }
    public Page<Cafe> getPage(int page, String location, Double dist, boolean confirmed) {
        Pageable pageable = PageRequest.of(page, itemsOnPage);
        if (confirmed) {
            Double lat = Double.parseDouble(location.split(",")[0]);
            Double lng = Double.parseDouble(location.split(",")[1]);
            return cafeRepository.findNearbyCoffeeShops(lat, lng, dist, pageable);
        }
        else {
            return cafeRepository.findUnconfirmedCoffeeShops(pageable);
        }
    }

    public void update(long id, CafeRequest cafeRequest) {
        Cafe cafeToBeUpdated = getById(id);
        cafeToBeUpdated = mapper.fillCafeFromDTO(cafeToBeUpdated, cafeRequest);
        cafeRepository.save(cafeToBeUpdated);
    }
    public void delete(long id) {
        cafeRepository.deleteById(id);
    }

    public Cafe getById(long id) {
        return cafeRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public ResponseEntity<MessageResponse> addReview(Long id, User user, GradeRequest gradeRequest) {
        if (gradeRepository.alreadyExistingComment(id, user.getId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: comment already left!"));
        }

        Cafe cafe = getById(id);

        Set<Perk> perks = new HashSet<>();
        gradeRequest.getPerks().forEach(perkStr -> {
            Perk perk = perkRepository.findByTitle(EPerk.valueOf(perkStr))
                    .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
            perks.add(perk);
        });

        Grade grade = new Grade(gradeRequest.getComment(),
                gradeRequest.getGrade(),
                perks,
                user
        );
        cafe.getGrades().add(grade);
        cafeRepository.save(cafe);

        return ResponseEntity
                .created(URI.create("http://localhost/cafeterias/" + id))
                .body(new MessageResponse("Grade added successfully"));
    }

    public ResponseEntity<MessageResponse> updateReview(Long id, Long userId, GradeRequest gradeRequest) {
        Grade grade = gradeRepository.findByUserAndCafeIds(id, userId);
        if (grade == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: you don't have a review!"));
        }

        Set<Perk> perks = new HashSet<>();
        gradeRequest.getPerks().forEach(perkStr -> {
            Perk perk = perkRepository.findByTitle(EPerk.valueOf(perkStr))
                    .orElseThrow(() -> new RuntimeException("Error: perk is not found."));
            perks.add(perk);
        });
        grade.setGrade(gradeRequest.getGrade());
        grade.setComment(gradeRequest.getComment());
        grade.setPerks(perks);
        grade.setDate(new Date());

        gradeRepository.save(grade);
        return ResponseEntity
                .ok()
                .body(new MessageResponse("Grade updated successfully"));
    }

    public ResponseEntity<MessageResponse> deleteReview(Long id, Long userId) {
        Grade grade = gradeRepository.findByUserAndCafeIds(id, userId);
        if (grade == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: you don't have a review!"));
        }

        gradeRepository.deleteById(grade.getId());

        return ResponseEntity
                .ok()
                .body(new MessageResponse("Grade deleted successfully"));
    }
}