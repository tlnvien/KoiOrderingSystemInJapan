import {
  notification,
  Card,
  Row,
  Col,
  Button,
  Input,
  DatePicker,
  InputNumber,
} from "antd";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import moment from "moment"; // Import moment for date handling
import "./TourDetail.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import api from "../../config/axios";

function ListTour() {
  const [tours, setTours] = useState([]);
  const [tourName, setTourName] = useState("");
  const [farmName, setFarmName] = useState("");
  const [koiSpecies, setKoiSpecies] = useState("");
  const [departureMonth, setDepartureMonth] = useState(null);
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);
  const token = localStorage.getItem("token");

  const fetchData = async () => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }

      const response = await api.get("tour/list/available", {
        headers: {
          Accept: "*/*",
          Authorization: `Bearer ${token}`,
        },
      });

      const tourData = Array.isArray(response.data) ? response.data : [];
      setTours(tourData);
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || "Có lỗi xảy ra khi tải dữ liệu.";
      notification.error({ message: errorMessage });
      setTours([]); // Set tours to an empty array in case of error
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleSearch = async () => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }

      const formattedDepartureDate = departureMonth
        ? departureMonth.format("MM")
        : "";

      const formattedMinPrice = minPrice !== null ? Number(minPrice) : null;
      const formattedMaxPrice = maxPrice !== null ? Number(maxPrice) : null;

      const params = {
        tourName,
        farmName,
        koiSpecies,
        departureMonth: formattedDepartureDate,
        minPrice: formattedMinPrice,
        maxPrice: formattedMaxPrice,
        tourType: "AVAILABLE_TOUR", // Chỉ lấy tour loại AVAILABLE_TOUR
      };

      const filteredParams = Object.fromEntries(
        Object.entries(params).filter(([_, v]) => v != null && v !== "")
      );

      const response = await api.get("tour/search", {
        headers: {
          Accept: "*/*",
          Authorization: `Bearer ${token}`,
        },
        params: filteredParams,
      });

      const tourData = Array.isArray(response.data) ? response.data : [];
      setTours(tourData);
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || "Có lỗi xảy ra khi tìm kiếm.";
      notification.error({ message: errorMessage });
      setTours([]); // Set tours to an empty array in case of error
    }
  };

  // Get the current date
  const currentDate = moment().startOf("day"); // Use moment for current date comparison

  return (
    <div>
      <Header />
      <div
        className="filter-section"
        style={{ margin: "20px", display: "flex" }}
      >
        <div style={{ flex: 1, marginRight: "20px" }}>
          <h3>BỘ LỌC TÌM KIẾM</h3>
          <Input
            placeholder="Tên tour"
            value={tourName}
            onChange={(e) => setTourName(e.target.value)}
            style={{ width: "100%", marginBottom: "10px" }}
          />
          <Input
            placeholder="Loại cá Koi"
            value={koiSpecies}
            onChange={(e) => setKoiSpecies(e.target.value)}
            style={{ width: "100%", marginBottom: "10px" }}
          />
          <DatePicker
            picker="month"
            placeholder="Chọn tháng khởi hành"
            onChange={(date) => setDepartureMonth(date)}
            style={{ width: "100%", marginBottom: "10px" }}
            format="MM"
          />
          <InputNumber
            controls={false}
            placeholder="Giá tối thiểu"
            value={minPrice}
            onChange={(value) => setMinPrice(value)}
            style={{ width: "100%", marginBottom: "10px" }}
            formatter={(value) =>
              value
                ? value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")
                : ""
            }
            parser={(value) => value.replace(/\./g, "")}
          />
          <InputNumber
            controls={false}
            placeholder="Giá tối đa"
            value={maxPrice}
            onChange={(value) => setMaxPrice(value)}
            style={{ width: "100%", marginBottom: "10px" }}
            formatter={(value) =>
              value
                ? value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".")
                : ""
            }
            parser={(value) => value.replace(/\./g, "")}
          />
          <Button
            type="primary"
            onClick={handleSearch}
            style={{ width: "100%" }}
          >
            Tìm kiếm
          </Button>
        </div>
        <div style={{ flex: 3 }}>
          <div>
            {tours
              .filter(
                (tour) =>
                  tour.tourType === "AVAILABLE_TOUR" &&
                  tour.status !== "IN_PROGRESS" &&
                  tour.status !== "COMPLETED"
              ) // Lọc các tour không hiển thị
              .map((tour) => {
                // Use moment to parse the departure date
                const tourDepartureDate = moment(
                  tour.departureDate,
                  "DD-MM-YYYY"
                );

                // Check if the tour's departure date is in the past
                const isDisabled = tourDepartureDate.isBefore(currentDate);

                return (
                  <Row
                    gutter={[16, 16]}
                    key={tour.tourId}
                    style={{ marginBottom: "20px" }}
                  >
                    <Col
                      span={8}
                      style={{
                        display: "flex",
                        justifyContent: "center",
                        alignItems: "center",
                      }}
                    >
                      <img
                        src={tour.tourImage}
                        alt={tour.tourName}
                        style={{
                          width: "100%",
                          height: "auto",
                          borderRadius: "10px",
                          maxWidth: "90%",
                          objectFit: "cover",
                        }}
                      />
                    </Col>
                    <Col span={16}>
                      <Card bordered={false}>
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
                          <strong>Số chỗ còn:</strong> {tour.remainSeat}
                        </p>
                        <p>
                          <strong>Giá:</strong> {tour.price.toLocaleString()}
                        </p>
                        <Button type="primary" disabled={isDisabled}>
                          <Link
                            to={`/tour-detail/${tour.tourId}`}
                            style={{ color: "black" }}
                          >
                            {isDisabled ? "Không thể đặt" : "Chi tiết"}
                          </Link>
                        </Button>
                        {isDisabled}
                      </Card>
                    </Col>
                  </Row>
                );
              })}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default ListTour;
