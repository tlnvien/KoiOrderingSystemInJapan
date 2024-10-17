package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.entity.TourSchedule;
import com.project.KoiBookingSystem.model.request.TourScheduleRequest;
import com.project.KoiBookingSystem.model.response.TourScheduleResponse;
import com.project.KoiBookingSystem.service.TourScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tour/schedule")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class TourScheduleAPI {

    @Autowired
    TourScheduleService tourScheduleService;

    @PostMapping("{tourID}")
    @PreAuthorize("hasAnyAuthority('SALES')")
    public ResponseEntity createTourSchedule(@Valid @RequestBody TourScheduleRequest tourScheduleRequest, @PathVariable String tourID) {
        TourScheduleResponse tourScheduleResponse = tourScheduleService.createTourSchedule(tourScheduleRequest, tourID);
        return ResponseEntity.ok(tourScheduleResponse);
    }

    @GetMapping("/all")
    public ResponseEntity getAllSchedules() {
        List<TourScheduleResponse> tourSchedules = tourScheduleService.getAllSchedules();
        return ResponseEntity.ok(tourSchedules);
    }

    @GetMapping("/all/{tourID}")
    public ResponseEntity getScheduleByTour(@PathVariable String tourID) {
        List<TourScheduleResponse> tourScheduleResponses = tourScheduleService.getScheduleByTour(tourID);
        return ResponseEntity.ok(tourScheduleResponses);
    }
}
