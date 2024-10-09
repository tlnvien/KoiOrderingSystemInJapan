package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.KoiImage;
import com.project.KoiBookingSystem.model.request.KoiRequest;
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

    @GetMapping("/images")
    public ResponseEntity getAllKoiImages() {
        List<KoiImage> koiImages = koiService.getAllKoiImages();
        return ResponseEntity.ok(koiImages);
    }

<<<<<<< HEAD
    @GetMapping("/images/{koiId}")
    public ResponseEntity getKoiImagesByKoiId(@PathVariable String koiId) {
        List<KoiImage> koiImages = koiService.getKoiImagesByKoiId(koiId);
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
=======
    @PutMapping("{koiID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity updateKoi(@Valid @RequestBody KoiRequest koi, @PathVariable String koiID) {
        KoiResponse updatedKoi = koiService.updateKoi(koi, koiID);
        return ResponseEntity.ok(updatedKoi);
    }

    @DeleteMapping("{koiID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteKoi(@PathVariable String koiID) {
        KoiResponse deletedKoi = koiService.deleteKoi(koiID);
>>>>>>> c32ecad3e7b477f322ad177700c02f3ed07bb1ec
        return ResponseEntity.ok(deletedKoi);
    }

}
