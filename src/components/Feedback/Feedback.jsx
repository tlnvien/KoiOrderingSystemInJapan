import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Feedback.css";

const Feedback = () => {
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState("");
  const [feedbackType, setFeedbackType] = useState("KOI"); // Loại phản hồi mặc định
  const [koiSpecies, setKoiSpecies] = useState("");
  const [farmName, setFarmName] = useState("");
  const [staffId, setStaffId] = useState("");
  const [reviews, setReviews] = useState([]);
  const apiUrl = "http://localhost:8082/api/feedback"; // Cập nhật với URL API của bạn
  const userRole = localStorage.getItem("role"); // Lấy vai trò từ localStorage

  useEffect(() => {
    // Lấy đánh giá hiện có khi component được gắn
    const fetchReviews = async () => {
      try {
        const response = await axios.get(apiUrl, { params: { type: "ALL" } });
        setReviews(response.data);
      } catch (error) {
        console.error("Lỗi khi lấy đánh giá:", error);
      }
    };

    fetchReviews();
  }, []);

  const handleStarClick = (ratingValue) => {
    setRating(ratingValue);
  };

  const handleStarHover = (ratingValue) => {
    setHoverRating(ratingValue);
  };

  const handleSubmit = async () => {
    if (rating > 0 && comment !== "") {
      const newFeedback = {
        rating,
        comment,
        // Bao gồm các trường dựa trên loại phản hồi đã chọn
        ...(feedbackType === "KOI" && { koiSpecies }),
        ...(feedbackType === "FARM" && { farmName }),
        ...(feedbackType === "STAFF" && { staffId }),
      };

      try {
        // Gửi phản hồi đến API backend
        await axios.post(apiUrl, newFeedback, {
          params: { type: feedbackType },
        }); // Chỉ định loại phản hồi nếu cần
        // Lấy lại các đánh giá đã cập nhật sau khi gửi
        const response = await axios.get(apiUrl, { params: { type: "ALL" } });
        setReviews(response.data);
        // Đặt lại các trường nhập
        setRating(0);
        setComment("");
        setKoiSpecies("");
        setFarmName("");
        setStaffId("");
      } catch (error) {
        alert("Gửi phản hồi không thành công. Vui lòng thử lại!");
        console.error("Lỗi khi gửi phản hồi:", error);
      }
    } else {
      alert("Vui lòng cung cấp đánh giá và bình luận!");
    }
  };

  return (
    <div className="feedback-page">
      <div className="review-container">
        <h2>Đánh Giá và Nhận Xét</h2>

        {/* Chọn loại phản hồi */}
        <select
          value={feedbackType}
          onChange={(e) => setFeedbackType(e.target.value)}
        >
          <option value="KOI">Phản Hồi cá Koi</option>
          <option value="FARM">Phản Hồi Trang Trại</option>
          <option value="STAFF">Phản Hồi Nhân Viên</option>
        </select>

        {/* {feedbackType === "KOI" && (
          <input
            type="text"
            value={koiSpecies}
            onChange={(e) => setKoiSpecies(e.target.value)}
            placeholder="Loài Koi"
          />
        )}
        {feedbackType === "FARM" && (
          <input
            type="text"
            value={farmName}
            onChange={(e) => setFarmName(e.target.value)}
            placeholder="Tên Trang Trại"
          />
        )}
        {feedbackType === "STAFF" && (
          <input
            type="text"
            value={staffId}
            onChange={(e) => setStaffId(e.target.value)}
            placeholder="ID Nhân Viên"
          />
        )} */}

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
              Gửi Đánh Giá
            </button>
          </>
        ) : (
          <p>Bạn phải là khách hàng để để lại phản hồi.</p>
        )}

        {/* <div className="review-section">
          <h3>Đánh Giá Của Người Dùng</h3>
          <ul>
            {reviews.map((review, index) => (
              <li key={index} className="review-item">
                <div className="stars">
                  {Array(review.rating)
                    .fill()
                    .map((_, i) => (
                      <span key={i} className="active">
                        &#9733;
                      </span>
                    ))}
                </div>
                <p className="comment">{review.comment}</p>
                {review.type === "KOI" && <p>Loài: {review.koiSpecies}</p>}
                {review.type === "FARM" && (
                  <p>Tên Trang Trại: {review.farmName}</p>
                )}
                {review.type === "STAFF" && (
                  <p>ID Nhân Viên: {review.staffId}</p>
                )}
              </li>
            ))}
          </ul>
        </div> */}
      </div>
    </div>
  );
};

export default Feedback;
