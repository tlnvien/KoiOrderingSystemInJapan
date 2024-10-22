package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.EditFeedbackRequest;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.BookingResponse;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    TourRepository tourRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Transactional
    public FeedbackResponse createNewFeedback(FeedbackRequest feedbackRequest, String tourId) {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)){
            throw new ActionException("Invalid activity! Only customer can create feedback!");
        }
        List<Booking> bookings = bookingRepository.findByTour_TourId(tourId);
        if (bookings.isEmpty()) {
            throw new EmptyListException("There is no bookings for this Tour!");
        }
        for (Booking booking : bookings) {
            if (booking.getCustomer().equals(customer)) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(generateFeedbackId());
                feedback.setCustomer(customer);
                feedback.setRating(feedbackRequest.getRating());
                feedback.setComments(feedbackRequest.getComment());
                feedback.setStatus(true);
                feedback.setTour(booking.getTour());

                Feedback newFeedback = feedbackRepository.save(feedback);

                return convertToFeedbackResponse(newFeedback);
            }
        }
        throw new NotFoundException("This customer is not in this Tour!");
    }

    public List<FeedbackResponse> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findByStatusTrue();

        if (feedbacks.isEmpty()) {
            throw new EmptyListException("Feedback list is empty!");
        }
        return feedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbacksOfTour(String tourId) {
        List<Feedback> feedbacks = feedbackRepository.findByTour_TourId(tourId);
        if (feedbacks.isEmpty()) {
            throw new EmptyListException("No feedback for this tour!");
        }
        return feedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse deleteFeedback(String feedbackId) {
        Feedback feedback = feedbackRepository.findByFeedbackId(feedbackId);
        if (feedback == null) {
            throw new NotFoundException("Feedback not found!");
        }
        feedback.setStatus(false);
        feedbackRepository.save(feedback);
        return convertToFeedbackResponse(feedback);
    }

    @Transactional
    public FeedbackResponse updateFeedback(String feedbackId, EditFeedbackRequest request) {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)) {
            throw new ActionException("Invalid activity! Only customer can perform this action!");
        }
        Feedback feedback = feedbackRepository.findByFeedbackId(feedbackId);
        if (feedback == null || !feedback.isStatus()) {
            throw new NotFoundException("Feedback not found!");
        }
        if (!feedback.getCustomer().equals(customer)) {
            throw new ActionException("You are not allowed to edit feedback!");
        }
        feedback.setComments(request.getComment());
        feedback.setRating(request.getRating());

        Feedback updatedFeedback = feedbackRepository.save(feedback);

        return convertToFeedbackResponse(updatedFeedback);
    }

    private FeedbackResponse convertToFeedbackResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setFeedbackId(feedback.getFeedbackId());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComments());
        response.setCustomerId(feedback.getCustomer().getUserId());
        response.setTourId(feedback.getTour().getTourId());
        return response;
    }

    private String generateFeedbackId() {
        Feedback lastFeedback = feedbackRepository.findTopByOrderByIdDesc();
        int lastId = 0;
        if (lastFeedback != null && lastFeedback.getFeedbackId() != null) {
            String lastFeedbackId = lastFeedback.getFeedbackId();
            lastId = Integer.parseInt(lastFeedbackId.substring(2));
        }
        return "FB" + (lastId + 1);
    }
}
