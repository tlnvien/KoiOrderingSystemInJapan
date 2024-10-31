import React, { useState, useEffect } from "react";
import api from "../../config/axios";
import "./Feedback.css";

const Feedback = () => {
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState("");
  const [feedbackId, setFeedbackId] = useState(null);
  const [tourId, setTourId] = useState(null);
  const [customerTours, setCustomerTours] = useState([]);
  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("role");
  const customerID = localStorage.getItem("userId");

  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const response = await api.get(
          `booking/available/listBooking/${customerID}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        if (response.data && response.data.length > 0) {
          setCustomerTours(
            response.data.map((booking) => ({
              bookingId: booking.bookingId,
              tourId: booking.tourID.tourID,
              createdDate: booking.createdDate,
            }))
          );
        }
      } catch (error) {
        console.error("Error fetching bookings:", error);
      }
    };

    fetchBookings();
  }, [customerID, token]);

  useEffect(() => {
    const fetchFeedback = async () => {
      if (tourId) {
        try {
          const response = await api.get(`feedback/${tourId}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (response.data) {
            setRating(response.data.rating);
            setComment(response.data.comment);
            setFeedbackId(response.data.id);
          } else {
            setRating(0);
            setComment("");
            setFeedbackId(null);
          }
        } catch (error) {
          console.error("Error fetching feedback:", error);
        }
      }
    };

    fetchFeedback();
  }, [tourId, token]);

  const handleStarClick = (ratingValue) => setRating(ratingValue);
  const handleStarHover = (ratingValue) => setHoverRating(ratingValue);

  const handleSubmit = async () => {
    if (rating > 0 && comment !== "") {
      const feedbackData = { rating, comment, tourId };

      try {
        if (feedbackId) {
          await api.put(`feedback/${feedbackId}`, feedbackData, {
            headers: { Authorization: `Bearer ${token}` },
          });
          alert("Phản hồi của bạn đã được cập nhật thành công!");
        } else {
          await api.post(`feedback?tourId=${tourId}`, feedbackData, {
            headers: { Authorization: `Bearer ${token}` },
          });
          alert("Phản hồi của bạn đã được gửi thành công!");
        }

        setRating(0);
        setComment("");
        setFeedbackId(null);
      } catch (error) {
        alert("Gửi phản hồi không thành công. Vui lòng thử lại!");
        console.error("Error submitting feedback:", error);
      }
    } else {
      alert("Vui lòng cung cấp đánh giá và bình luận!");
    }
  };

  return (
    <div className="feedback-page">
      <div className="review-container">
        <h2>Đánh Giá và Nhận Xét</h2>
        {userRole === "CUSTOMER" ? (
          <>
            <label htmlFor="tourSelect">Chọn Tour của bạn:</label>
            <select
              id="tourSelect"
              value={tourId || ""}
              onChange={(e) => setTourId(e.target.value)}
            >
              <option value="">Chọn tour</option>
              {customerTours.map((tour) => (
                <option key={tour.bookingId} value={tour.tourId}>
                  {`Tour ID: ${tour.tourId} - Ngày đặt: ${tour.createdDate}`}
                </option>
              ))}
            </select>

            <div className="star-rating">
              {[1, 2, 3, 4, 5].map((starValue) => (
                <span
                  key={starValue}
                  className={`star ${
                    hoverRating >= starValue || rating >= starValue
                      ? "active"
                      : ""
                  }`}
                  onClick={() => handleStarClick(starValue)}
                  onMouseEnter={() => handleStarHover(starValue)}
                  onMouseLeave={() => setHoverRating(0)}
                >
                  &#9733;
                </span>
              ))}
            </div>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Để lại bình luận của bạn ở đây..."
            ></textarea>
            <button onClick={handleSubmit} className="buttonFeedback">
              {feedbackId ? "Cập Nhật Đánh Giá" : "Gửi Đánh Giá"}
            </button>
          </>
        ) : (
          <p>Bạn phải là khách hàng để để lại phản hồi.</p>
        )}
      </div>
    </div>
  );
};

export default Feedback;
