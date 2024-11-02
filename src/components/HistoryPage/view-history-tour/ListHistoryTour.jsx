import React, { useEffect, useState } from "react";
import { Card, Button } from "antd";
import { useNavigate } from "react-router-dom";
import api from "../../../config/axios";
import Header from "../../Header/Header";
import {
  UserOutlined,
  ShoppingCartOutlined,
  StarOutlined,
} from "@ant-design/icons";
import "./ListHistoryTour.css"; // Import custom CSS for styling

const ListHistoryTour = () => {
  const [tours, setTours] = useState([]);
  const userId = localStorage.getItem("userId");
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const fetchTours = async () => {
    try {
      const response = await api.get(
        `tour/list/customer/${userId}`,
        {}, // Empty request body
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include token in headers
          },
        }
      );

      // Filter tours with status "COMPLETED"
      const completedTours = Array.isArray(response.data)
        ? response.data.filter((tour) => tour.status === "COMPLETED")
        : [];

      setTours(completedTours);
    } catch (error) {
      console.error("Error fetching tours:", error);
      alert("Có lỗi xảy ra khi tải danh sách tour. Vui lòng thử lại.");
    }
  };

  useEffect(() => {
    fetchTours();
  }, []);

  const handleViewDetails = async (tourId) => {
    try {
      const response = await api.get(
        `tour/search/${tourId}`,
        {}, // Request body trống
        {
          headers: {
            Authorization: `Bearer ${token}`, // Đưa token vào headers
          },
        }
      );
      navigate(`/history-tour-detail/${tourId}`, {
        state: { tourDetails: response.data },
      });
    } catch (error) {
      console.error("Error fetching tour details:", error);
      alert("Có lỗi xảy ra khi tải thông tin chi tiết tour.");
    }
  };

  return (
    <div>
      <Header />
      <div className="profile-content">
        <div className="sidebar-profile">
          <ul>
            <li onClick={() => navigate("/profile")}>
              <UserOutlined style={{ marginRight: "10px" }} /> Tài khoản
            </li>
            <li onClick={() => navigate("/orders")}>
              <ShoppingCartOutlined style={{ marginRight: "10px" }} /> Đơn đặt
              hàng
            </li>
            <li onClick={() => navigate("/history-tour")}>
              <StarOutlined style={{ marginRight: "10px" }} /> Tour đã đi
            </li>
          </ul>
        </div>

        <div className="history-list-container">
          {tours.map((tour) => (
            <Card key={tour.tourId} className="history-card">
              <div className="history-content">
                <img
                  src={tour.tourImage}
                  alt={tour.tourName}
                  className="history-image"
                />
                <div className="history-details">
                  <h3>{tour.tourName}</h3> {/* Đảm bảo có tiêu đề tour ở đây */}
                  <p>Mã tour: {tour.tourId}</p>
                  <p>Ngày khởi hành: {tour.departureDate}</p>
                  <p>Thời gian: {tour.duration}</p>
                  <p>Ngày kết thúc: {tour.endDate}</p>
                  <p>Hướng dẫn viên: {tour.consultingName}</p>
                  <p>Giá: {tour.price}</p>
                  <Button
                    type="primary"
                    onClick={() => handleViewDetails(tour.tourId)}
                  >
                    Xem Chi Tiết
                  </Button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ListHistoryTour;
