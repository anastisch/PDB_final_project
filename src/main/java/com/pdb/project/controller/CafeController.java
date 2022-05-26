package com.pdb.project.controller;

import com.pdb.project.model.Cafe;
import com.pdb.project.model.User;
import com.pdb.project.payload.request.CafeRequest;
import com.pdb.project.payload.request.GradeRequest;
import com.pdb.project.payload.response.MessageResponse;
import com.pdb.project.security.service.UserDetailsImpl;
import com.pdb.project.service.CafeService;
import com.pdb.project.service.UserService;
import com.pdb.project.service.YandexMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/cafeterias")
public class CafeController {
    private final CafeService service;
    private final UserService userService;
    private final YandexMapService mapService;

    public CafeController(CafeService service, UserService userService, YandexMapService mapService) {
        this.service = service;
        this.userService = userService;
        this.mapService = mapService;
    }

    @PostMapping
    public void create(@RequestBody CafeRequest cafe) {
        service.create(cafe);
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateCafeterias(@RequestParam(value = "res", defaultValue = "1") Integer res,
                                                            @Value("${PDB.app.areas}") String areas,
                                                            @Value("${PDB.app.updateURL}") String urlPattern,
                                                            @Value("${PDB.app.mapAPI}") String apikey) {
        return mapService.updateCafeterias(res, areas, urlPattern, apikey);
    }

    @GetMapping
    public List<Cafe> getCafePage(@RequestParam(value = "page") Integer page,
                                  @RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                                  @RequestParam(value = "dist", defaultValue = "1.0") Double dist,
                                  @RequestParam(value = "confirmed", defaultValue = "true") boolean confirmed) {
        Page<Cafe> cafeterias = service.getPage(page, location, dist, confirmed);
        return cafeterias.getContent();
    }

    @GetMapping("/pages-count")
    public int getCountPage(@RequestParam(value = "location", defaultValue = "59.965361,30.311645") String location,
                            @RequestParam(value = "dist", defaultValue = "1.0") Double dist,
                            @RequestParam(value = "confirmed", defaultValue = "true") boolean confirmed) {
        return service.getPageCount(location, dist, confirmed);
    }

    @GetMapping("/{id}")
    public Cafe getCafe(@PathVariable long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCafe(@PathVariable long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCafe(@PathVariable long id,
                           @RequestBody CafeRequest cafe) {
        service.update(id, cafe);
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_USER')")
    public ResponseEntity<MessageResponse> addReview(Authentication auth,
                                                     @PathVariable long id,
                                                     @RequestBody GradeRequest grade) {
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        User user = userService.getById(userDetails.getId());
        return service.addReview(id, user, grade);
    }

    @PatchMapping("/{id}/review/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> updateReview(@PathVariable long id,
                                                        @PathVariable long userId,
                                                        @RequestBody GradeRequest grade) {
        return service.updateReview(id, userId, grade);
    }

    @DeleteMapping("/{id}/review/{userId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable long id,
                                                        @PathVariable long userId) {
        return service.deleteReview(id, userId);
    }
}