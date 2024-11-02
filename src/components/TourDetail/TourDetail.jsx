import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./TourDetail.css";
import api from "../../config/axios";

const TourDetailPage = () => {
  const { tourId } = useParams();

  const navigate = useNavigate();
  const [tour, setTour] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTourDetail = async () => {
      try {
        if (!tourId) {
          console.error("tourId is not available.");
          return;
        }
        const response = await api.get(`tour/search/${tourId}`);
        setTour(response.data);
      } catch (error) {
        console.error("Error fetching tour details:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchTourDetail();
  }, [tourId]);

  const handleBookingClick = () => {
    navigate(`/booking/available?tourId=${tourId}`);
  };

  if (loading) {
    return (
      <div className="loading-spinner">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="tour-detail-container">
      <Header />
      <div className="tour-main-content">
        <div className="tour-detail-header">
          <h1 className="tour-title">Thông tin chi tiết</h1>
        </div>
        <div className="tour-content-wrapper">
          <div className="tour-image-section">
            <img
              src={tour.tourImage || "default_image_path.jpg"}
              className="tour-detail-image"
            />
          </div>
          <div className="tour-info-section">
            <div className="tour-info-content">
              <h2>{tour.tourName}</h2>
              <p>
                <strong>Mã tour:</strong> {tour.tourId}
              </p>
              <p>
                <strong>Số chỗ còn:</strong> {tour.remainSeat}
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
              <div className="tour-price">
                <strong>Giá:</strong> {tour.price.toLocaleString()}
              </div>
              <button className="tour-button" onClick={handleBookingClick}>
                Đặt Tour
              </button>
            </div>
          </div>
        </div>
        <div className="tour-schedule-section">
          <h2>Lịch trình tour</h2>
          {tour.tourSchedules && tour.tourSchedules.length > 0 ? (
            tour.tourSchedules.map((schedule, index) => (
              <div key={index} className="schedule-item">
                <h3>
                  Ngày {index + 1}: {schedule.farmName}
                </h3>
                <p>
                  <strong>Chi tiết:</strong> {schedule.scheduleDescription}
                </p>
              </div>
            ))
          ) : (
            <p>Chưa có lịch trình cho tour này.</p>
          )}
          <Link to="/list-tour" className="back-button">
            Quay lại danh sách
          </Link>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default TourDetailPage;
