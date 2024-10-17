package com.project.KoiBookingSystem.model.response;

import com.project.KoiBookingSystem.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {
    private long id;         // ID của phản hồi
    private String content;  // Nội dung phản hồi
    private int rating;      // Điểm đánh giá
    private String userName; // Tên người dùng (nhân viên)
}
