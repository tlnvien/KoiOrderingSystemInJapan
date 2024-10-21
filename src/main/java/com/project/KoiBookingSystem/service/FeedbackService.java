package com.project.KoiBookingSystem.service;


import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Feedback;
import com.project.KoiBookingSystem.entity.Tour;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FeedbackRepository;
import com.project.KoiBookingSystem.repository.TourRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    FeedbackRepository feedbackRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TourRepository tourRepository;

    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {

            Account staff = accountRepository.findAccountByUserID(feedbackRequest.getStaffID());
            if (staff == null) {
                throw new NotFoundException("Staff not found");
            }
            Tour tour = tourRepository.findTourByTourID(feedbackRequest.getTourID());
            if (tour == null) {
                throw new NotFoundException("Tour not found");
            }
            Feedback feedback = new Feedback();
            feedback.setFeedbackComment(feedbackRequest.getFeedbackComment());
            feedback.setFeedbackDate(new Date());
            feedback.setRating(feedbackRequest.getRating());
            feedback.setCustomer(authenticationService.getCurrentAccount());
            feedback.setStaff(staff);
            feedback.setTourFeedback(tour);
            feedbackRepository.save(feedback);
            return modelMapper.map(feedback, FeedbackResponse.class);
    }

    // Lấy danh sách phản hồi từ FeedbackRepository
    public List<FeedbackResponse> getFeedbacksByStaff() {
        return feedbackRepository.findFeedbacksByStaff();
    }
}
