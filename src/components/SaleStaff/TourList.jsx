import React, { useEffect, useState } from "react";
import { List, Card, Button } from "antd";
import axios from "axios";
import dayjs from "dayjs";
import "./TourList.css"; // Tạo file CSS riêng để tùy chỉnh giao diện

const api = "https://66e79651b17821a9d9d95a2b.mockapi.io/Tour"; // Your API URL

const TourList = () => {
  const [tours, setTours] = useState([]);

  const fetchTours = async () => {
    try {
      const response = await axios.get(api);
      const toursWithFormattedDate = response.data.map((tour) => ({
        ...tour,
        startDate: dayjs(tour.startDate).format("DD-MM-YYYY"), // Format date as needed
      }));
      setTours(toursWithFormattedDate);
    } catch (error) {
      console.error("Failed to fetch tours:", error);
    }
  };

  useEffect(() => {
    fetchTours(); // Fetch tours on initial load
  }, []);

  return (
    <List
      grid={{ gutter: 16, column: 1 }}
      dataSource={tours}
      renderItem={(tour) => (
        <List.Item>
          <Card className="tour-card">
            <div className="tour-item">
              <img
                src={tour.image_path}
                alt={tour.tourName}
                className="tour-image"
              />
              <div className="tour-details">
                <h3>{tour.tourName}</h3>
                <p>Mã tour: {tour.tourCode}</p>
                <p>Thời gian: {tour.duration}</p>
                <p>Ngày khởi hành: {tour.startDate}</p>
                <p>Số chỗ còn: {tour.quantity}</p>
                <Button type="primary">Chi tiết</Button>
              </div>
              <div className="tour-price">
                <p>GIÁ: {tour.price} VNĐ/Khách</p>
              </div>
            </div>
          </Card>
        </List.Item>
      )}
    />
  );
};

export default TourList;
