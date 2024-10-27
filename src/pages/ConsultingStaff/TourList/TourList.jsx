import React, { useEffect, useState } from "react";
import { Outlet, useNavigate } from "react-router-dom"; // Import useNavigate for navigation
import "./TourList.css";
import api from "../../../config/axios";

function TourList() {
  const [tours, setTours] = useState([]);
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate(); // Initialize navigate

  useEffect(() => {
    const fetchTours = async () => {
      const token = localStorage.getItem("token");
      try {
        const response = await api.get(
          `http://localhost:8082/api/tour/list/${userId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setTours(response.data);
      } catch (error) {
        console.error("Error fetching tours:", error);
      }
    };

    fetchTours();
  }, [userId]);

  const handleCardClick = (tourId) => {
    navigate(`/dashboard/consulting/list-passenger/${tourId}`); // Navigate to CustomerInTour component with tourId
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
        </div>
      ))}
    </div>
  );
}

export default TourList;
