package com.project.KoiBookingSystem.service;


import com.project.KoiBookingSystem.entity.Account;
import com.project.KoiBookingSystem.entity.Feedback;
import com.project.KoiBookingSystem.exception.NotFoundException;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.repository.AccountRepository;
import com.project.KoiBookingSystem.repository.FeedbackRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {

            Account staff = accountRepository.findAccountByUserID(feedbackRequest.getUserId());
            if (staff == null) {
                throw new NotFoundException("Staff not found");
            }
            Feedback feedback = new Feedback();
            feedback.setContent(feedbackRequest.getContent());
            feedback.setRating(feedbackRequest.getRating());
            feedback.setCustomer(authenticationService.getCurrentAccount());
            feedback.setStaff(staff);
            feedbackRepository.save(feedback);
            return modelMapper.map(feedback, FeedbackResponse.class);
    }

    // Lấy danh sách phản hồi từ FeedbackRepository
    public List<FeedbackResponse> getFeedbacksByStaff() {
        return feedbackRepository.findFeedbacksByStaff();
    }
}
