package com.pdb.project.controller;

import com.pdb.project.payload.request.CafeRequest;
import com.pdb.project.payload.response.MessageResponse;
import com.pdb.project.service.CafeService;
import com.pdb.project.service.UserService;
import com.pdb.project.service.YandexMapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}