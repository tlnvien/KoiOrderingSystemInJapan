package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.model.response.FarmResponse;
import com.project.KoiBookingSystem.model.response.KoiFarmResponse;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import com.project.KoiBookingSystem.service.KoiFarmService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/koiFarm")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class KoiFarmAPI {

    @Autowired
    KoiFarmService koiFarmService;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity addKoiToFarm(@RequestParam String koiId, @RequestParam String farmId) {
        KoiFarmResponse koiFarm = koiFarmService.addKoiToFarm(koiId, farmId);
        return ResponseEntity.ok(koiFarm);
    }

    @GetMapping("/listKoi/{farmId}")
    public ResponseEntity getAllKoiByFarmId(@PathVariable String farmId) {
        List<KoiResponse> kois = koiFarmService.getAllKoiFromFarmId(farmId);
        return ResponseEntity.ok(kois);
    }

    @GetMapping("/listFarm/{koiId}")
    public ResponseEntity getAllFarmByKoiId(@PathVariable String koiId) {
        List<FarmResponse> farms = koiFarmService.getAllFarmByKoiId(koiId);
        return ResponseEntity.ok(farms);
    }

    @DeleteMapping("/koi/{koiId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteKoiFromFarm(@RequestParam String farmId, @PathVariable String koiId) {
        koiFarmService.deleteKoiFromFarm(farmId, koiId);
        return ResponseEntity.ok("Koi deleted from this farm successfully!");
    }

}
