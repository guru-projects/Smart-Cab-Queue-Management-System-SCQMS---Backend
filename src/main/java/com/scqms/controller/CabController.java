package com.scqms.controller;

import com.scqms.entity.Cab;
import com.scqms.service.CabService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabs")
@RequiredArgsConstructor
public class CabController {

    private final CabService cabService;

    @GetMapping("/all")
    public List<Cab> getAll() {
        return cabService.getAll();
    }

    @GetMapping("/{id}")
    public Cab getById(@PathVariable Long id) {
        return cabService.getById(id);
    }
}
