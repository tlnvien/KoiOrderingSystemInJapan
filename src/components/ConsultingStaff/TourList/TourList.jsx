import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // Import useNavigate for navigation
import "./TourList.css";
import api from "../../../config/axios";
import { message } from "antd";

function TourList() {
  const [tours, setTours] = useState([]);
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate(); // Initialize navigate

  const fetchTours = async () => {
    const token = localStorage.getItem("token");
    try {
      const response = await api.get(`tour/list/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (Array.isArray(response.data)) {
        setTours(response.data); // Set tours if it is an array
      } else {
        console.error(
          "Dữ liệu nhận được không phải là một mảng:",
          response.data
        );
      }
    } catch (error) {
      console.error("Lỗi khi lấy danh sách tour:", error);
    }
  };

  // Call fetchTours in useEffect
  useEffect(() => {
    fetchTours();
  }, [userId]);

  const handleCardClick = (tourId) => {
    navigate(`/dashboard/consulting/list-passenger/${tourId}`); // Navigate to CustomerInTour component with tourId
  };

  // Function to start tour
  const handleStartTour = async (event, tourId) => {
    event.stopPropagation(); // Prevent the card click event
    const token = localStorage.getItem("token");
    try {
      await api.post(
        `tour/start/${tourId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success(`Tour ${tourId} đã bắt đầu`);
      fetchTours(); // Reload tours after starting
    } catch (error) {
      message.error(
        "Lỗi: " + error.response?.data || "Có lỗi xảy ra khi bắt đầu tour."
      );
    }
  };

  // Function to end tour
  const handleEndTour = async (event, tourId) => {
    event.stopPropagation(); // Prevent the card click event
    const token = localStorage.getItem("token");
    try {
      await api.post(
        `tour/end/${tourId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success(`Tour ${tourId} đã kết thúc`);
      fetchTours(); // Reload tours after ending
    } catch (error) {
      message.error(
        "Lỗi: " + error.response?.data || "Có lỗi xảy ra khi kết thúc tour."
      );
    }
  };

  return (
    <div className="tour-list-container">
      {tours.map((tour) => (
        <div
          key={tour.tourId}
          className="tour-card"
          onClick={() => handleCardClick(tour.tourId)}
        >
          <img
            src={tour.tourImage}
            alt={tour.tourName}
            className="tour-image"
          />
          <div className="tour-details">
            <h3>{tour.tourName}</h3>
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
              <strong>Mô tả:</strong> {tour.description}
            </p>
            <p>
              <strong>Số chỗ còn lại:</strong> {tour.remainSeat}
            </p>
            <p>
              <strong>Giá:</strong> {tour.price.toLocaleString()} VND
            </p>
          </div>
          <div className="tour-actions">
            <button
              onClick={(event) => handleStartTour(event, tour.tourId)}
              className="start-button"
            >
              Bắt đầu
            </button>
            <button
              onClick={(event) => handleEndTour(event, tour.tourId)}
              className="end-button"
            >
              Kết thúc
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}

export default TourList;
