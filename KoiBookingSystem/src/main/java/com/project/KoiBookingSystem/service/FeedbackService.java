package com.project.KoiBookingSystem.service;

import com.project.KoiBookingSystem.entity.*;
import com.project.KoiBookingSystem.enums.Role;
import com.project.KoiBookingSystem.enums.TourStatus;
import com.project.KoiBookingSystem.exception.*;
import com.project.KoiBookingSystem.model.request.EditFeedbackRequest;
import com.project.KoiBookingSystem.model.request.FeedbackRequest;
import com.project.KoiBookingSystem.model.response.FeedbackResponse;
import com.project.KoiBookingSystem.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    TourRepository tourRepository;

    @Autowired
    FeedbackRepository feedbackRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BookingRepository bookingRepository;

    @Transactional
    public FeedbackResponse createNewFeedback(FeedbackRequest feedbackRequest, String tourId) {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || customer.getRole() != Role.CUSTOMER){
            throw new AuthorizationException("Chỉ có khách hàng mới có thể thực hiện feedback!");
        }
        checkUserIsPendingDeletion(customer);
        Tour tour = tourRepository.findTourByTourId(tourId);
        if (tour.getStatus() != TourStatus.COMPLETED) {
            throw new InvalidRequestException("Bạn không thể thực hiện feedback vì Tour chưa hoàn thành!");
        }
        List<Booking> bookings = bookingRepository.findByTour_TourId(tourId);
        if (bookings.isEmpty()) {
            throw new EmptyListException("Tour này không có booking!");
        }
        for (Booking booking : bookings) {
            if (booking.getCustomer().getUserId().equals(customer.getUserId())) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(generateFeedbackId());
                feedback.setCustomer(customer);
                feedback.setFeedbackDate(LocalDateTime.now());
                feedback.setRating(feedbackRequest.getRating());
                feedback.setComments(feedbackRequest.getComment());
                feedback.setStatus(true);
                feedback.setTour(booking.getTour());

                Feedback newFeedback = feedbackRepository.save(feedback);

                return convertToFeedbackResponse(newFeedback);
            }
        }
        throw new NotFoundException("Bạn không thể feedback cho tour này vì bạn không phải là hành khách của tour!");
    }

    public List<FeedbackResponse> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findByStatusTrue();

        if (feedbacks.isEmpty()) {
            throw new EmptyListException("Danh sách feedback đang trống!");
        }
        return feedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbacksOfTour(String tourId) {
        List<Feedback> feedbacks = feedbackRepository.findByTour_TourIdAndStatusTrue(tourId);
        if (feedbacks.isEmpty()) {
            throw new EmptyListException("Tour này không có feedback!");
        }
        return feedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse deleteFeedback(String feedbackId) {
        Feedback feedback = feedbackRepository.findByFeedbackId(feedbackId);
        if (feedback == null) {
            throw new NotFoundException("Feedback không tìm thấy!");
        }
        feedback.setStatus(false);
        feedbackRepository.save(feedback);
        return convertToFeedbackResponse(feedback);
    }

    @Transactional
    public FeedbackResponse updateFeedback(String feedbackId, EditFeedbackRequest request) {
        Account customer = authenticationService.getCurrentAccount();
        if (customer == null || customer.getRole() != Role.CUSTOMER) {
            throw new AuthorizationException("Chỉ có khách hàng mới có thể cập nhật feedback!");
        }
        checkUserIsPendingDeletion(customer);
        Feedback feedback = feedbackRepository.findByFeedbackId(feedbackId);
        if (feedback == null || !feedback.isStatus()) {
            throw new NotFoundException("Không tìm thấy feedback!");
        }
        if (!feedback.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new InvalidRequestException("Bạn không có quyền chỉnh sửa feedback này!");
        }
        feedback.setComments(request.getComment());
        feedback.setRating(request.getRating());

        Feedback updatedFeedback = feedbackRepository.save(feedback);

        return convertToFeedbackResponse(updatedFeedback);
    }

    public List<FeedbackResponse> getPositiveFeedback() {
        List<Feedback> positiveFeedbacks = feedbackRepository.findByRatingGreaterThanAndStatusTrue(2);
        if (positiveFeedbacks.isEmpty()) {
            throw new EmptyListException("Không có feedback tích cực nào!");
        }
        return positiveFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> getCustomerFeedbacks(String customerId) {
        List<Feedback> customerFeedbacks = feedbackRepository.findByCustomer_UserIdAndStatusTrue(customerId);
        if (customerFeedbacks.isEmpty()) {
            throw new EmptyListException("Không có feedback nào của khách hàng này!");
        }
        return customerFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> getNegativeFeedback() {
        List<Feedback> negativeFeedbacks = feedbackRepository.findByRatingLessThanEqualAndStatusTrue(2);
        if (negativeFeedbacks.isEmpty()) {
            throw new EmptyListException("Không có feedback tiêu cực nào!");
        }
        return negativeFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> manageCustomerFeedbacks(String customerId) {
        List<Feedback> customerFeedbacks = feedbackRepository.findByCustomer_UserId(customerId);
        if (customerFeedbacks.isEmpty()) {
            throw new EmptyListException("Khách hàng này chưa từng feedback!");
        }
        return customerFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> manageNegativeFeedbacks() {
        List<Feedback> negativeFeedbacks = feedbackRepository.findByRatingLessThanEqual(2);
        if (negativeFeedbacks.isEmpty()) {
            throw new EmptyListException("Không có feedback tiêu cực nào tồn tại trong hệ thống!");
        }
        return negativeFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> managePositiveFeedbacks() {
        List<Feedback> positiveFeedbacks = feedbackRepository.findByRatingGreaterThan(2);
        if (positiveFeedbacks.isEmpty()) {
            throw new EmptyListException("Không có feedback tích cực nào tồn tại trong hệ thống!");
        }
        return positiveFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> manageTourFeedbacks(String tourId) {
        List<Feedback> tourFeedbacks = feedbackRepository.findByTour_TourId(tourId);
        if (tourFeedbacks.isEmpty()) {
            throw new EmptyListException("Không có feedback nào cho tour này!");
        }
        return tourFeedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    public List<FeedbackResponse> manageAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAll();
        if (feedbacks.isEmpty()) {
            throw new EmptyListException("Hệ thống không tồn tại bất cứ feedback nào!");
        }
        return feedbacks.stream().map(this::convertToFeedbackResponse).collect(Collectors.toList());
    }

    private FeedbackResponse convertToFeedbackResponse(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setFeedbackId(feedback.getFeedbackId());
        response.setRating(feedback.getRating());
        response.setComment(feedback.getComments());
        response.setFeedbackDate(feedback.getFeedbackDate());
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

    private void checkUserIsPendingDeletion(Account account) {
        if (account.isPendingDeletion()) throw new AuthenticationException("Tài khoản " + account.getUserId() + " đang yêu cầu xóa và không thể thực hiện bất kỳ hành động nào trong hệ thống. Để hủy quá trình này, vui lòng đăng nhập lại!");
        if (!account.isStatus()) throw new AuthenticationException("Tài khoản này đã không còn tồn tại!");
    }
}
