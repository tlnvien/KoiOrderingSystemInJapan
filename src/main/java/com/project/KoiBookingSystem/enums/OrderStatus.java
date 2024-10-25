package com.project.KoiBookingSystem.enums;

public enum OrderStatus {
    PROCESSING,  //tạo đơn thành công -> đang xử lí
    PREPARING,  //farm owner đã xác nhận đơn và đang đóng hàng (farm owner update)
    SHIPPING,  //farm owner dđã ship (farm owner update)
    RECEIVED,  //con staff nhận đc ở sân bay vn (con staff update)
    DELIVERING,  //deli staff đang ship (deli staff update)
    DELIVERED,  //đã giao cho customer (deli staff update)
    CANCELLED
}
