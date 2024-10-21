package com.project.KoiBookingSystem.api;


import com.project.KoiBookingSystem.entity.Feedback;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public ResponseEntity create (@RequestBody FeedbackRequest feedbackRequest) {
        FeedbackResponse feedback = feedbackService.createFeedback(feedbackRequest);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity getAll () {
        List<FeedbackResponse> feedbackList = feedbackService.getFeedbacksByStaff();
        return ResponseEntity.ok(feedbackList);
    }
}
