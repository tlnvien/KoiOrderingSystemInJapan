package com.project.KoiBookingSystem.enums;

public enum OrderStatus {
    // Trạng thái này là của thằng chủ trang trại cập nhật
    PREPARING, // chuẩn bị cá để đóng gói
    SENT, // hàng đã được gửi lên chuyến bay
    // Trạng thái của thằng tư vấn cập nhật
    RECEIVED, // thằng tư vấn đã nhận được hàng ở sân bay
    // Thằng nhân viên vận chuyển cập nhật
    DELIVERING, // Đơn hàng đang vận chuyển
    DELIVERED, // Đơn hàng đã vận chuyển
    CANCELLED // Đơn hàng bị hủy
}
