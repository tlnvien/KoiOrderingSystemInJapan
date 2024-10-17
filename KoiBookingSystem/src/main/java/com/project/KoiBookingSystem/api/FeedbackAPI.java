package com.project.KoiBookingSystem.api;

import com.project.KoiBookingSystem.enums.FeedbackType;
import com.project.KoiBookingSystem.model.request.EditFeedbackRequest;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createNewFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest, @RequestParam FeedbackType type) {
        FeedbackResponse feedbackResponse = feedbackService.createNewFeedback(feedbackRequest, type);
        return ResponseEntity.ok(feedbackResponse);
    }

    @GetMapping
    public ResponseEntity getAllFeedbacks(@RequestParam FeedbackType type) {
        List<FeedbackResponse> feedbackResponses = feedbackService.getAllFeedbacks(type);
        return ResponseEntity.ok(feedbackResponses);
    }

    @DeleteMapping("{feedbackId}")
    public ResponseEntity deleteFeedback(@PathVariable String feedbackId) {
        FeedbackResponse feedbackResponse = feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok(feedbackResponse);
    }

    @PutMapping("{feedbackId}")
    public ResponseEntity updateFeedback(@PathVariable String feedbackId, @RequestBody EditFeedbackRequest request) {
        FeedbackResponse feedbackResponse = feedbackService.updateFeedback(feedbackId, request);
        return ResponseEntity.ok(feedbackResponse);
    }
}
