package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.model.request.EditFeedbackRequest;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class FeedbackAPI {

    @Autowired
    FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity createNewFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest, String tourId) {
        FeedbackResponse feedbackResponse = feedbackService.createNewFeedback(feedbackRequest, tourId);
        return ResponseEntity.ok(feedbackResponse);
    }

    @GetMapping("/all")
    public ResponseEntity getAllFeedbacks() {
        List<FeedbackResponse> feedbackResponses = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbackResponses);
    }

    @GetMapping("tour/{tourId}")
    public ResponseEntity getFeedbackOfTour(@PathVariable String tourId) {
        List<FeedbackResponse> feedbackResponses = feedbackService.getFeedbacksOfTour(tourId);
        return ResponseEntity.ok(feedbackResponses);
    }

    @DeleteMapping("{feedbackId}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity deleteFeedback(@PathVariable String feedbackId) {
        FeedbackResponse feedbackResponse = feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok(feedbackResponse);
    }

    @PutMapping("{feedbackId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity updateFeedback(@PathVariable String feedbackId, @RequestBody EditFeedbackRequest request) {
        FeedbackResponse feedbackResponse = feedbackService.updateFeedback(feedbackId, request);
        return ResponseEntity.ok(feedbackResponse);
    }

    @GetMapping("positive")
    public ResponseEntity getPositiveFeedbacks() {
        List<FeedbackResponse> feedbackResponses = feedbackService.getPositiveFeedback();
        return ResponseEntity.ok(feedbackResponses);
    }

    @GetMapping("negative")
    public ResponseEntity getNegativeFeedbacks() {
        List<FeedbackResponse> feedbackResponses = feedbackService.getNegativeFeedback();
        return ResponseEntity.ok(feedbackResponses);
    }


    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity getCustomerFeedbacks(@PathVariable String customerId) {
        List<FeedbackResponse> feedbackResponses = feedbackService.getCustomerFeedbacks(customerId);
        return ResponseEntity.ok(feedbackResponses);
    }


    @GetMapping("/manage/negative")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'CONSULTING', 'SALES', 'DELIVERING')")
    public ResponseEntity manageNegativeFeedbacks() {
        List<FeedbackResponse> feedbackResponses = feedbackService.manageNegativeFeedbacks();
        return ResponseEntity.ok(feedbackResponses);
    }


    @GetMapping("/manage/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'CONSULTING', 'SALES', 'DELIVERING')")
    public ResponseEntity manageCustomerFeedbacks(@PathVariable String customerId) {
        List<FeedbackResponse> feedbackResponses = feedbackService.manageCustomerFeedbacks(customerId);
        return ResponseEntity.ok(feedbackResponses);
    }


    @GetMapping("/manage/positive")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'CONSULTING', 'SALES', 'DELIVERING')")
    public ResponseEntity managePositiveFeedbacks() {
        List<FeedbackResponse> feedbackResponses = feedbackService.manageNegativeFeedbacks();
        return ResponseEntity.ok(feedbackResponses);
    }


    @GetMapping("/manage/tour/{tourId}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'CONSULTING', 'SALES', 'DELIVERING')")
    public ResponseEntity manageTourFeedbacks(@PathVariable String tourId) {
        List<FeedbackResponse> feedbackResponses = feedbackService.manageTourFeedbacks(tourId);
        return ResponseEntity.ok(feedbackResponses);
    }

    @GetMapping("/manage/all")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'CONSULTING', 'SALES', 'DELIVERING')")
    public ResponseEntity manageAllFeedbacks() {
        List<FeedbackResponse> feedbackResponses = feedbackService.manageAllFeedbacks();
        return ResponseEntity.ok(feedbackResponses);
    }


}
