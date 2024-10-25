package com.project.KoiBookingSystem.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.KoiBookingSystem.enums.BookingStatus;
import com.project.KoiBookingSystem.enums.RequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "userId")
    @JsonBackReference
    private Account customer; // KHÁCH HÀNG ĐẶT TOUR, KHÁCH HÀNG ĐẶT YÊU CẦU BOOKING

    @ManyToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "tourId")
    private Tour tour; // ĐẶT TOUR THEO YÊU CẦU THÌ DÒNG NÀY CHƯA XUẤT HIỆN, THẰNG SALES CẦN PHẢI LIÊN KẾT THỦ CÔNG VỚI TOUR ĐÃ ĐƯỢC TẠO VỚI CÁI ĐƠN BOOKING NÀY

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private boolean hasVisa; // Ô CHECK BOX, THAM KHẢO XEM KHÁCH HÀNG ĐÃ CÓ VISA HAY CHƯA, TOUR THEO YÊU CẦU HAY CÓ SẴN CŨNG NÊN CÓ

    private String description;

    @Column(nullable = false)
    @Min(value = 0, message = "Invalid number of attendances")
    private int numberOfAttendances;

    @NotNull(message = "seatBooked can not be null")
    private int seatBooked;

    @Column(nullable = false)
    @Min(value = 0, message = "Total price can not be negative")
    private double totalPrice;

    private Date lastUpdate;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Payment payment; // KHI NÀO THANH TOÁN THÀNH CÔNG THÌ MỚI HIỆN CÁI NÀY

    @ManyToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "userId")
    @JsonBackReference
    private Account sales; // THẰNG KINH DOANH NÀO LẤY CÁI YÊU CẦU BOOKING NÀY, NẾU LÀ TOUR CÓ SẴN THÌ DÒNG NÀY LÀ NULL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus; // CÁI NÀY LÀ THÔNG TIN CHECK IN BOOKING


    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus; // TRẠNG THÁI GỬI YÊU CẦU CỦA CÁI BOOKING ĐÓ, TỨC LÀ NẾU NHƯ KHÁCH HÀNG ĐẶT TOUR THEO YÊU CẦU THÌ NÓ SẼ CÓ THÔNG TIN, CÒN NẾU LÀ BOOKING CÓ TỪ TOUR CÓ SẴN THÌ DÒNG NÀY LÀ NULL

    private boolean isExpired;

    @ManyToOne
    @JoinColumn(name = "consulting_ID", referencedColumnName = "userID")
    private Account consulting;//.
}
