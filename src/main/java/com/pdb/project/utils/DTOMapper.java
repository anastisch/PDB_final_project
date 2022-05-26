package com.pdb.project.utils;

import com.pdb.project.model.Cafe;
import com.pdb.project.model.Hours;
import com.pdb.project.model.Point;
import com.pdb.project.model.User;
import com.pdb.project.payload.request.CafeRequest;
import com.pdb.project.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DTOMapper {
    private final UserService userService;

    public DTOMapper(UserService userService) {
        this.userService = userService;
    }
    public Cafe fillCafeFromDTO(Cafe cafe, CafeRequest dto) {
        cafe.setName(dto.getName());
        cafe.setDescription(dto.getDescription());
        Point point = Optional.ofNullable(cafe.getLocation()).orElseGet(Point::new);
        String[] newLocationString = dto.getLocation().split(",");
        point.setLat(Double.parseDouble(newLocationString[0]));
        point.setLng(Double.parseDouble(newLocationString[1]));
        cafe.setLocation(point);
        cafe.setAddress(dto.getAddress());
        cafe.setPhone(dto.getPhone());
        cafe.setUrl(dto.getUrl());
        if (dto.getManagerId() != 0) {
            User manager = userService.getById(dto.getManagerId());
            cafe.setManager(manager);
        }
        List<Hours> workingHours = new ArrayList<>();
        dto.getWorkingHours().forEach(e -> {
            if (e.getStart_time() != null && e.getEnd_time() != null)
                workingHours.add(e);
        });
        cafe.setWorkingHours(workingHours);

        return cafe;
    }
    public Cafe toCafe(CafeRequest dto) {
        return fillCafeFromDTO(new Cafe(), dto);
    }
}