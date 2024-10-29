package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.KoiImage;
import com.project.KoiBookingSystem.model.request.KoiRequest;
import com.project.KoiBookingSystem.model.response.KoiImageResponse;
import com.project.KoiBookingSystem.model.response.KoiResponse;
import com.project.KoiBookingSystem.service.KoiService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/koi")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class KoiAPI {

    @Autowired
    KoiService koiService;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity createNewKoi(@Valid @RequestBody KoiRequest koiRequest) {
        KoiResponse newKoi = koiService.createNewKoi(koiRequest);
        return ResponseEntity.ok(newKoi);
    }

    @GetMapping("/list")
    public ResponseEntity getAllKois() {
        List<KoiResponse> kois = koiService.getKoiList();
        return ResponseEntity.ok(kois);
    }

    @GetMapping("/details/{koiId}")
    public ResponseEntity getKoiDetails(@PathVariable String koiId) {
        KoiResponse koiResponse = koiService.getKoiDetails(koiId);
        return ResponseEntity.ok(koiResponse);
    }

    @GetMapping("/images")
    public ResponseEntity getAllKoiImages() {
        List<KoiImageResponse> koiImages = koiService.getAllKoiImages();
        return ResponseEntity.ok(koiImages);
    }

    @GetMapping("/images/{koiId}")
    public ResponseEntity getKoiImagesByKoiId(@PathVariable String koiId) {
        List<KoiImageResponse> koiImages = koiService.getKoiImagesByKoiId(koiId);
        return ResponseEntity.ok(koiImages);
    }

    @PutMapping("{koiId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity updateKoi(@Valid @RequestBody KoiRequest koi, @PathVariable String koiId) {
        KoiResponse updatedKoi = koiService.updateKoi(koi, koiId);
        return ResponseEntity.ok(updatedKoi);
    }

    @DeleteMapping("{koiId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteKoi(@PathVariable String koiId) {
        KoiResponse deletedKoi = koiService.deleteKoi(koiId);
        return ResponseEntity.ok(deletedKoi);
    }

    @GetMapping("/search")
    public ResponseEntity searchKoi(@RequestParam(required = false) String species, @RequestParam(required = false) String farmName) {
        List<KoiResponse> koiResponses = koiService.searchKoi(species, farmName);
        return ResponseEntity.ok(koiResponses);
    }

    @DeleteMapping("/image/remove/{koiId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteKoiImage(@PathVariable String koiId, @RequestParam String imageLink) {
        KoiResponse koiResponse = koiService.deleteKoiImage(koiId, imageLink);
        return ResponseEntity.ok(koiResponse);
    }
}
