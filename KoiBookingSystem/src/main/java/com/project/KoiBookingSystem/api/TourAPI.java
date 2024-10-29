package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.enums.TourType;
import com.project.KoiBookingSystem.model.request.TourRequest;
import com.project.KoiBookingSystem.model.response.TourResponse;
import com.project.KoiBookingSystem.model.response.UserResponse;
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
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity createNewTour(@Valid @RequestBody TourRequest tour, @RequestParam TourType tourType) {
        TourResponse newTour = tourService.createNewTour(tour, tourType);
        return ResponseEntity.ok(newTour);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'MANAGER', 'SALES')")
    public ResponseEntity getAllTours() {
        List<TourResponse> tours = tourService.getAllTours();
        return ResponseEntity.ok(tours);
    }

    @PutMapping("{tourId}")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity updateTour(@Valid @RequestBody TourRequest tour, @PathVariable String tourId) {
        TourResponse updatedTour = tourService.updateTour(tour, tourId);
        return ResponseEntity.ok(updatedTour);
    }

    @DeleteMapping("{tourId}")
    @PreAuthorize("hasAuthority('SALES')")
    public ResponseEntity deleteTour(@PathVariable String tourId) {
        TourResponse deletedTour = tourService.deleteTour(tourId);
        return ResponseEntity.ok(deletedTour);
    }

    @GetMapping("/search")
    public ResponseEntity searchTours(@RequestParam(required = false) String tourName,
                                      @RequestParam(required = false) String farmName,
                                      @RequestParam(required = false) String koiSpecies,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                      @RequestParam(required = false) Double minPrice,
                                      @RequestParam(required = false) Double maxPrice) {
        List<TourResponse> tourResponses = tourService.searchTours(tourName, farmName, koiSpecies, departureDate, minPrice, maxPrice);
        return ResponseEntity.ok(tourResponses);
    }

    @GetMapping("/validate")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getTourToValidate() {
        List<TourResponse> tourResponses = tourService.getTourToValidate();
        return ResponseEntity.ok(tourResponses);
    }

    @GetMapping("/search/{tourId}")
    public ResponseEntity getTourDetails(@PathVariable String tourId) {
        TourResponse tourResponse = tourService.getTourDetails(tourId);
        return ResponseEntity.ok(tourResponse);
    }

    @PostMapping("approve/{tourId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity approveTour(@PathVariable String tourId) {
        TourResponse tourResponse = tourService.approveTour(tourId);
        return ResponseEntity.ok(tourResponse);
    }

    @PostMapping("/deny/{tourId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity denyTour(@PathVariable String tourId) {
        TourResponse tourResponse = tourService.denyTour(tourId);
        return ResponseEntity.ok(tourResponse);
    }

    @PostMapping("/start/{tourId}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity startTour(@PathVariable String tourId) {
        TourResponse tourResponse = tourService.startTour(tourId);
        return ResponseEntity.ok(tourResponse);
    }

    @PostMapping("end/{tourId}")
    @PreAuthorize("hasAuthority('CONSULTING')")
    public ResponseEntity endTour(@PathVariable String tourId) {
        TourResponse tourResponse = tourService.endTour(tourId);
        return ResponseEntity.ok(tourResponse);
    }


    @GetMapping("/customers/{tourId}")
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'MANAGER', 'SALES', 'DELIVERING')")
    public ResponseEntity getCustomerInTour(@PathVariable String tourId) {
        List<UserResponse> userResponses = tourService.getCustomerInTour(tourId);
        return ResponseEntity.ok(userResponses);
    }


    @GetMapping("/list/requested")
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'MANAGER', 'DELIVERING', 'SALES')")
    public ResponseEntity getRequestedTours() {
        List<TourResponse> requestedTours = tourService.getRequestedTour();
        return ResponseEntity.ok(requestedTours);
    }

    @GetMapping("/list/available")
    public ResponseEntity getAvailableTours() {
        List<TourResponse> availableTours = tourService.getAvailableTour();
        return ResponseEntity.ok(availableTours);
    }


    @GetMapping("/list/{consultingId}")
    @PreAuthorize("hasAnyAuthority('CONSULTING', 'MANAGER', 'SALES')")
    public ResponseEntity getTourByConsulting(String consultingId) {
        List<TourResponse> tourResponses = tourService.getTourByConsulting(consultingId);
        return ResponseEntity.ok(tourResponses);
    }
}
