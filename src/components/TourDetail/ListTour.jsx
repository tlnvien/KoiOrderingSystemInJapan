import {
  notification,
  Card,
  Row,
  Col,
  Button,
  Input,
  DatePicker,
  Select,
  InputNumber,
} from "antd";
import axios from "axios";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./TourDetail.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import api from "../../config/axios";

const { Option } = Select;

function ListTour() {
  const [tours, setTours] = useState([]);
  const [tourName, setTourName] = useState("");
  const [farmName, setFarmName] = useState("");
  const [koiSpecies, setKoiSpecies] = useState("");
  const [departureDate, setDepartureDate] = useState([]);
  const [minPrice, setMinPrice] = useState(null);
  const [maxPrice, setMaxPrice] = useState(null);
  const token = localStorage.getItem("token");

  // Hàm để gọi API lấy danh sách tour
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

      // Ensure `tours` is set to an array, even if response.data is not an array
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

  // Inside the handleSearch function
  const handleSearch = async () => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }

      const formattedDepartureDate =
        departureDate.length > 0 ? departureDate[0].format("DD-MM-YYYY") : "";

      const params = {
        tourName,
        farmName,
        koiSpecies,
        departureDate: formattedDepartureDate,
        minPrice,
        maxPrice,
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
          {/* <Input
            placeholder="Tên trang trại"
            value={farmName}
            onChange={(e) => setFarmName(e.target.value)}
            style={{ width: "100%", marginBottom: "10px" }}
          /> */}
          <Input
            placeholder="Loại cá Koi"
            value={koiSpecies}
            onChange={(e) => setKoiSpecies(e.target.value)}
            style={{ width: "100%", marginBottom: "10px" }}
          />
          <DatePicker
            placeholder="Chọn tháng khởi hành"
            onChange={(dates) => setDepartureDate(dates)}
            style={{ width: "100%", marginBottom: "10px" }}
            format="DD-MM-YYYY"
          />
          <InputNumber
            controls={false}
            placeholder="Giá tối thiểu"
            type="number"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            style={{ width: "100%", marginBottom: "10px" }}
          />
          <InputNumber
            controls={false}
            placeholder="Giá tối đa"
            type="number"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            style={{ width: "100%", marginBottom: "10px" }}
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
          {/* <h3>SẮP XẾP THEO</h3>
          <Select
            defaultValue="Mặc định"
            style={{ width: "100%", marginBottom: "20px" }}
          >
            <Option value="default">Mặc định</Option>
            <Option value="price">Giá</Option>
            <Option value="date">Ngày khởi hành</Option>
          </Select> */}
          <div>
            {tours.map((tour) => (
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
                    src={tour.tourImage} // Thay thế bằng hình ảnh thực tế của tour
                    style={{
                      width: "100%",
                      height: "auto",
                      borderRadius: "10px", // Điều chỉnh độ bo góc
                      maxWidth: "90%", // Đảm bảo ảnh không vượt quá kích thước cột
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
                      <strong>Giá:</strong> {tour.price.toLocaleString()} VND
                    </p>
                    <Button type="primary">
                      <Link
                        to={`/tour-detail/${tour.tourId}`}
                        style={{ color: "#fff" }}
                      >
                        Chi tiết
                      </Link>
                    </Button>
                  </Card>
                </Col>
              </Row>
            ))}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
}

export default ListTour;
