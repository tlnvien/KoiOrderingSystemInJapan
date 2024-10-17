package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.FeedbackType;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.exception.ActionException;
import com.project.KoiBookingSystem.exception.EmptyListException;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.EditFeedbackRequest;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FarmRepository;
import com.project.KoiBookingSystem.repository.FeedbackRepository;
import com.project.KoiBookingSystem.repository.KoiRepository;
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
    FarmRepository farmRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    KoiRepository koiRepository;

    @Transactional
    public FeedbackResponse createNewFeedback(FeedbackRequest feedbackRequest, FeedbackType type) {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || !customer.getRole().equals(Role.CUSTOMER)){
            throw new ActionException("Invalid activity! Only customer can create feedback!");
        }
        Feedback feedback = new Feedback();
        feedback.setFeedbackId(generateFeedbackId());
        feedback.setCustomer(customer);
        feedback.setRating(feedbackRequest.getRating());
        feedback.setComments(feedbackRequest.getComment());
        feedback.setStatus(true);
        feedback.setType(type);
        switch (type) {
            case FARM:
                if (feedbackRequest.getFarmName() == null) {
                    throw new ActionException("Farm Name is required for feedback Farm!");
                }
                Farm farm = farmRepository.findByFarmNameAndStatusTrue(feedbackRequest.getFarmName());
                if (farm == null) {
                    throw new NotFoundException("Farm name not found!");
                }
                feedback.setFarm(farm);
                break;
            case KOI:
                if (feedbackRequest.getKoiSpecies() == null) {
                    throw new ActionException("Koi species is required for Feedback Koi!");
                }
                Koi koi = koiRepository.findBySpeciesAndStatusTrue(feedbackRequest.getKoiSpecies());
                if (koi == null) {
                    throw new NotFoundException("Koi Species not found!");
                }
                feedback.setKoi(koi);
                break;
            case STAFF:
                if (feedbackRequest.getStaffId() == null) {
                    throw new ActionException("Staff id is required for Feedback Staff!");
                }
                Account staff = accountRepository.findAccountByUserId(feedbackRequest.getStaffId());
                if (staff == null || (!staff.getRole().equals(Role.CONSULTING) && !staff.getRole().equals(Role.SALES) && !staff.getRole().equals(Role.DELIVERING))) {
                    throw new NotFoundException("Staff id not found!");
                }
                feedback.setStaff(staff);
                break;
            default:
                throw new ActionException("Invalid feedback type!");
        }
        Feedback newFeedback = feedbackRepository.save(feedback);

        return convertToFeedbackResponse(newFeedback);
    }

    public List<FeedbackResponse> getAllFeedbacks(FeedbackType type) {
        List<Feedback> feedbacks = feedbackRepository.findByTypeAndStatusTrue(type);

        if (feedbacks.isEmpty()) {
            throw new EmptyListException("Feedback list is empty!");
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

        return convertToFeedbackResponse(feedback);
    }

    private FeedbackResponse convertToFeedbackResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setFeedbackId(feedback.getFeedbackId());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComments());
        response.setType(feedback.getType());
        response.setFarmName(feedback.getFarm().getFarmName());
        response.setCustomerId(feedback.getCustomer().getUserId());
        response.setKoiSpecies(feedback.getKoi().getSpecies());
        response.setStaffId(feedback.getStaff().getUserId());

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
