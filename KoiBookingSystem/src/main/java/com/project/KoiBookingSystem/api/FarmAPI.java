package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.FarmImage;
import com.project.KoiBookingSystem.model.request.FarmRequest;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.service.FarmService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farm")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class FarmAPI {

    @Autowired
    FarmService farmService;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity createFarm(@Valid @RequestBody FarmRequest farm) {
        FarmResponse newFarm = farmService.createNewFarm(farm);
        return ResponseEntity.ok(newFarm);
    }

    @GetMapping("/list")
    public ResponseEntity getAllFarms() {
        List<FarmResponse> farms = farmService.getAllFarms();
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/images")
    public ResponseEntity getAllFarmImages() {
        List<FarmImage> farmImages = farmService.getAllFarmImages();
        return ResponseEntity.ok(farmImages);
    }

    @PutMapping("{farmID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity updateFarm(@Valid @RequestBody FarmRequest farm, @PathVariable String farmID) {
        FarmResponse updatedFarm = farmService.updateFarm(farm, farmID);
        return ResponseEntity.ok(updatedFarm);
    }

    @DeleteMapping("{farmID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteFarm(@PathVariable String farmID) {
        FarmResponse deletedFarm = farmService.deleteFarm(farmID);
        return ResponseEntity.ok(deletedFarm);
    }
}

