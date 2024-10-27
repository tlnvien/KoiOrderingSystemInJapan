import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Feedback.css";

const Feedback = () => {
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState("");
  const [feedbackId, setFeedbackId] = useState(null);
  const apiUrl = "http://localhost:8082/api/feedback"; // API URL
  const userRole = localStorage.getItem("role");
  const token = localStorage.getItem("token");
  const tourId = localStorage.getItem("tourId");

  useEffect(() => {
    const fetchFeedback = async () => {
      try {
        const response = await axios.get(`${apiUrl}/${tourId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.data) {
          setRating(response.data.rating);
          setComment(response.data.comment);
          setFeedbackId(response.data.id);
        }
      } catch (error) {
        console.error("Error fetching feedback:", error);
      }
    };

    fetchFeedback();
  }, [apiUrl, token, tourId]);

  const handleStarClick = (ratingValue) => {
    setRating(ratingValue);
  };

  const handleStarHover = (ratingValue) => {
    setHoverRating(ratingValue);
  };

  const handleSubmit = async () => {
    if (rating > 0 && comment !== "") {
      const feedbackData = { rating, comment, tourId };

      try {
        if (feedbackId) {
          // Update feedback if it already exists
          await axios.put(`${apiUrl}/${feedbackId}`, feedbackData, {
            headers: { Authorization: `Bearer ${token}` },
          });
          alert("Phản hồi của bạn đã được cập nhật thành công!");
        } else {
          // Create new feedback
          await axios.post(apiUrl, feedbackData, {
            headers: { Authorization: `Bearer ${token}` },
          });
          alert("Phản hồi của bạn đã được gửi thành công!");
        }

        // Clear the form after successful submission or update
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
