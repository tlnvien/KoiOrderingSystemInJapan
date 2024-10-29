package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.FarmImage;
import com.project.KoiBookingSystem.model.request.FarmRequest;
import com.project.KoiBookingSystem.model.response.FarmImageResponse;
import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.service.FarmService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @GetMapping("details/{farmId}")
    public ResponseEntity getFarmByFarmId(@PathVariable String farmId) {
        FarmResponse farmResponse = farmService.getFarmDetail(farmId);
        return ResponseEntity.ok(farmResponse);

    }

    @GetMapping("/images")
    public ResponseEntity getAllFarmImages() {
        List<FarmImageResponse> farmImages = farmService.getAllFarmImages();
        return ResponseEntity.ok(farmImages);
    }

    @GetMapping("/images/{farmId}")
    public ResponseEntity getImagesByFarmId(@PathVariable String farmId) {
        List<FarmImageResponse> farmImages = farmService.getImagesByFarmId(farmId);
        return ResponseEntity.ok(farmImages);
    }

    @PutMapping("{farmId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity updateFarm(@Valid @RequestBody FarmRequest farm, @PathVariable String farmId) {
        FarmResponse updatedFarm = farmService.updateFarm(farm, farmId);
        return ResponseEntity.ok(updatedFarm);
    }

    @DeleteMapping("{farmId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteFarm(@PathVariable String farmId) {
        FarmResponse deletedFarm = farmService.deleteFarm(farmId);
        return ResponseEntity.ok(deletedFarm);
    }

    @GetMapping("/search")
    public ResponseEntity searchFarm(@RequestParam(required = false) String farmName, @RequestParam(required = false) String species) {
        List<FarmResponse> farmResponses = farmService.searchFarms(farmName, species);
        return ResponseEntity.ok(farmResponses);
    }

    @DeleteMapping("/images/remove/{farmId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteFarmImage(@PathVariable String farmId, @RequestParam String imageLink) {
        FarmResponse farmResponse = farmService.deleteFarmImage(farmId, imageLink);
        return ResponseEntity.ok(farmResponse);
    }
}

