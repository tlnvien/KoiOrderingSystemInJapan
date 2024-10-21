package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private long id;         // ID của phản hồi
    private String feedbackCommend;  // Nội dung phản hồi
    private Date feedbackDate; // ngày tạo feedback
    private int rating;      // Điểm đánh giá
    private String userName; // Tên người dùng (nhân viên)
    private String TourID;
}
