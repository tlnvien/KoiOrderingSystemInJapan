package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.service.TourService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/tour")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class TourAPI {

    @Autowired
    TourService tourService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SALES')")
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
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity updateTour(@Valid @RequestBody TourRequest tour, @PathVariable String tourID) {
        TourResponse updatedTour = tourService.updateTour(tour, tourID);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("{tourID}")
    @PreAuthorize("hasAnyAuthority('SALES')")
    public ResponseEntity deleteTour(@PathVariable String tourID) {
        Tour deletedTour = tourService.deleteTour(tourID);
        return ResponseEntity.ok(deletedTour);
    }

    // search tour bằng filter
    @GetMapping("/search")
    public ResponseEntity searchTours(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        List<TourResponse> search = tourService.getFilteredTours(destination, minPrice, maxPrice, startDate);
        return ResponseEntity.ok(search);
    }
}
