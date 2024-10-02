package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.service.TourService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tour")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class TourAPI {

    @Autowired
    TourService tourService;

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity createNewTour(@Valid @RequestBody TourRequest tour) {
        TourResponse newTour = tourService.createNewTour(tour);
        return ResponseEntity.ok(newTour);
    }

    @GetMapping
    public ResponseEntity getAllTours() {
        List<TourResponse> tours = tourService.getAllTours();
        return ResponseEntity.ok(tours);
    }

    @PutMapping("{tourID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity updateTour(@Valid @RequestBody TourRequest tour, @PathVariable String tourID) {
        TourResponse updatedTour = tourService.updateTour(tour, tourID);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("{tourID}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteTour(@PathVariable String tourID) {
        Tour deletedTour = tourService.deleteTour(tourID);
        return ResponseEntity.ok(deletedTour);
    }
}
