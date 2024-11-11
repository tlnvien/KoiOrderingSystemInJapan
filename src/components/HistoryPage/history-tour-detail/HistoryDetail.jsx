import React, { useEffect, useState } from "react";
import { Rate, Button, Input, Form, message } from "antd";
import { useParams, Link } from "react-router-dom";
import api from "../../../config/axios";
import Header from "../../Header/Header";

const { TextArea } = Input;

function HistoryDetail() {
  const [tour, setTour] = useState(null);
  const [feedbacks, setFeedbacks] = useState([]);
  const [comment, setComment] = useState("");
  const [rating, setRating] = useState(0);
  const [editingFeedbackId, setEditingFeedbackId] = useState(null);
  const { tourId } = useParams();
  const token = localStorage.getItem("token");

  // Fetch tour details
  useEffect(() => {
    const fetchTourDetails = async () => {
      try {
        const response = await api.get(`tour/search/${tourId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setTour(response.data);
      } catch (error) {
        console.error("Lỗi khi lấy thông tin tour:", error);
      }
    };

    if (tourId) fetchTourDetails();
  }, [tourId]);

  // Fetch feedback for the tour
  useEffect(() => {
    const fetchFeedbacks = async () => {
      try {
        const response = await api.get(`feedback/tour/${tourId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setFeedbacks(Array.isArray(response.data) ? response.data : []);
      } catch (error) {
        console.error("Lỗi khi lấy đánh giá:", error);
      }
    };

    if (tourId) fetchFeedbacks();
  }, [tourId]);

  const handleEditFeedback = (feedback) => {
    setComment(feedback.comment);
    setRating(feedback.rating);
    setEditingFeedbackId(feedback.feedbackId);
  };

  // Form submission for feedback
  const handleFeedbackSubmit = async () => {
    if (rating === 0 || !comment) {
      message.warning("Vui lòng cho điểm và nhập nhận xét.");
      return;
    }

    if (editingFeedbackId) {
      handleUpdateFeedback();
    } else {
      try {
        const response = await api.post(
          `feedback?tourId=${tourId}`,
          { comment, rating },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        message.success("Gửi đánh giá thành công!");
        setComment("");
        setRating(0);

        // Cập nhật danh sách feedbacks mà không cần tải lại từ server
        setFeedbacks([
          ...feedbacks,
          {
            feedbackId: response.data.feedbackId, // Ensure new feedback ID is included
            comment,
            rating,
            feedbackDate: new Date().toLocaleString(),
          },
        ]);
      } catch (error) {
        console.error("Lỗi khi gửi đánh giá:", error);
        message.error("Gửi đánh giá thất bại.");
      }
    }
  };

  // Cập nhật sau khi chỉnh sửa đánh giá
  const handleUpdateFeedback = async () => {
    if (rating === 0 || !comment) {
      message.warning("Vui lòng cho điểm và nhập nhận xét.");
      return;
    }

    try {
      await api.put(
        `feedback/${editingFeedbackId}`,
        { comment, rating },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Chỉnh sửa đánh giá thành công!");
      setComment("");
      setRating(0);
      setEditingFeedbackId(null);

      // Cập nhật lại danh sách feedbacks mà không cần tải lại từ server
      const updatedFeedbacks = feedbacks.map((fb) =>
        fb.feedbackId === editingFeedbackId
          ? {
              ...fb,
              comment,
              rating,
              feedbackDate: new Date().toLocaleString(),
            }
          : fb
      );
      setFeedbacks(updatedFeedbacks);
    } catch (error) {
      console.error("Lỗi khi chỉnh sửa đánh giá:", error);
      message.error("Chỉnh sửa đánh giá thất bại.");
    }
  };

  return (
    <div>
      <Header />
      <Link to="/history-tour" className="back-button">
        Quay lại danh sách tour đã đi
      </Link>
      <div style={{ padding: "20px" }}>
        {tour ? (
          <>
            <div className="tour-detail-container">
              <div className="tour-main-content">
                <div className="tour-detail-header">
                  <h1 className="tour-title">{tour.tourName}</h1>
                </div>
                <div
                  className="tour-content-wrapper"
                  style={{ display: "flex", gap: "20px", marginBottom: "20px" }}
                >
                  <div className="tour-image-section">
                    <img
                      src={tour.tourImage || "placeholder-image.jpg"}
                      alt={tour.tourName}
                      style={{
                        width: "300px",
                        height: "200px",
                        objectFit: "cover",
                        borderRadius: "10px",
                      }}
                    />
                  </div>
                  <div className="tour-info-section">
                    <div className="tour-info-content">
                      <p>
                        <strong>Mã tour:</strong> {tour.tourId}
                      </p>
                      <p>
                        <strong>Ngày khởi hành:</strong> {tour.departureDate}
                      </p>
                      <p>
                        <strong>Thời gian:</strong> {tour.duration}
                      </p>
                      <p>
                        <strong>Ngày kết thúc:</strong> {tour.endDate}
                      </p>
                      <p>
                        <strong>Mô tả:</strong> {tour.description}
                      </p>
                      <p
                        style={{
                          fontWeight: "bold",
                          color: "red",
                          fontSize: "30px",
                        }}
                      >
                        <strong>Giá:</strong> {tour.price} VND
                      </p>
                    </div>
                  </div>
                </div>
                <h2 style={{ marginTop: "20px" }}>Lịch trình tour</h2>
                {tour.tourSchedules &&
                  tour.tourSchedules.map((schedule) => (
                    <div key={schedule.farmId} style={{ marginBottom: "15px" }}>
                      <h3>{schedule.farmName}</h3>
                      <p>{schedule.scheduleDescription}</p>
                    </div>
                  ))}
              </div>
            </div>

            <h2 style={{ marginTop: "20px", textAlign: "center" }}>
              Để lại đánh giá
            </h2>
            <div
              style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                flexDirection: "column",
                marginBottom: "20px",
              }}
            >
              <Form
                layout="vertical"
                onFinish={handleFeedbackSubmit}
                style={{ width: "100%", maxWidth: "400px" }}
              >
                <Form.Item label="Điểm">
                  <Rate onChange={setRating} value={rating} />
                </Form.Item>
                <Form.Item label="Nhận xét">
                  <TextArea
                    rows={4}
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="Viết đánh giá của bạn..."
                  />
                </Form.Item>
                <Form.Item>
                  <Button
                    type="primary"
                    onClick={
                      editingFeedbackId
                        ? handleUpdateFeedback
                        : handleFeedbackSubmit
                    }
                    style={{ width: "100%" }}
                  >
                    {editingFeedbackId ? "Cập nhật đánh giá" : "Gửi đánh giá"}
                  </Button>
                </Form.Item>
              </Form>
            </div>

            <h2 style={{ marginTop: "20px", marginLeft: "50px" }}>Đánh giá</h2>
            <div
              style={{
                marginBottom: "20px",
                width: "500px",
                marginLeft: "100px",
              }}
            >
              {feedbacks.map((fb, index) => (
                <div
                  key={index}
                  style={{
                    borderBottom: "1px solid #e0e0e0",
                    padding: "10px 0",
                  }}
                >
                  <p>
                    <strong>Nhận xét:</strong> {fb.comment}
                  </p>
                  <p>
                    <strong>Điểm:</strong>
                    {""}
                    <Rate defaultValue={fb.rating} />
                  </p>
                  <p>
                    <strong>Ngày:</strong> {fb.feedbackDate}
                  </p>
                  <Button
                    type="link"
                    onClick={() => handleEditFeedback(fb)}
                    style={{ marginTop: "10px" }}
                  >
                    Chỉnh sửa
                  </Button>
                </div>
              ))}
            </div>
          </>
        ) : (
          <div>Loading tour details...</div>
        )}
      </div>
    </div>
  );
}

export default HistoryDetail;
