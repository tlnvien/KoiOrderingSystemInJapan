package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Farm;
import com.project.KoiBookingSystem.entity.Koi;
import com.project.KoiBookingSystem.entity.KoiFarm;
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
    public ResponseEntity addKoiToFarm(@RequestParam String koiID, @RequestParam String farmID) {
        KoiFarm koiFarm = koiFarmService.addKoiToFarm(koiID, farmID);
        return ResponseEntity.ok(koiFarm);
    }

    @GetMapping("/listKoi/{farmID}")
    public ResponseEntity getAllKoiByFarmID(@PathVariable String farmID) {
        List<Koi> kois = koiFarmService.getAllKoiFromFarmID(farmID);
        return ResponseEntity.ok(kois);
    }

    @GetMapping("/listFarm/{koiID}")
    public ResponseEntity getAllFarmByKoiID(@PathVariable String koiID) {
        List<Farm> farms = koiFarmService.getAllFarmByKoiID(koiID);
        return ResponseEntity.ok(farms);
    }

    @DeleteMapping("/farm/{farmID}/koi/{koiID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteKoiFromFarm(@PathVariable String farmID, String koiID) {
        KoiFarm deletedKoiFarm = koiFarmService.deleteKoiFromFarm(farmID, koiID);
        return ResponseEntity.ok(deletedKoiFarm);
    }

    @PutMapping("/farm/{farmID}/koi/{koiID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity updateKoiFromFarm(@PathVariable String farmID, String koiID) {
        KoiFarm updatedKoiFarm = koiFarmService.updateKoiFromFarm(farmID, koiID);
        return ResponseEntity.ok(updatedKoiFarm);
    }
}
