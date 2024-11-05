import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./TourDetail.css";
import api from "../../config/axios";

const TourDetailPage = () => {
  const { tourId } = useParams();
  const navigate = useNavigate();
  const [tour, setTour] = useState(null);
  const [comboTours, setComboTours] = useState([]);
  const [loading, setLoading] = useState(true);
  const [startIndex, setStartIndex] = useState(0);

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

    const fetchComboTours = async () => {
      try {
        const response = await api.get("tour/list/available");
        // Filter out the current tour from the combo tour list
        const filteredComboTours = response.data.filter(
          (comboTour) => comboTour.tourId !== tourId
        );
        setComboTours(filteredComboTours);
      } catch (error) {
        console.error("Error fetching combo tours:", error);
      }
    };

    fetchTourDetail();
    fetchComboTours();
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

  const handleNext = () => {
    if (startIndex < comboTours.length - 3) {
      setStartIndex(startIndex + 1);
    }
  };

  const handlePrevious = () => {
    if (startIndex > 0) {
      setStartIndex(startIndex - 1);
    }
  };

  return (
    <div className="tour-detail-container" key={tourId}>
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
                <strong>Nhân viên tư vấn:</strong> {tour.consultingName}
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
                <strong>Giá:</strong> {tour.price.toLocaleString()} VND
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
        </div>
      </div>

      {/* Combo tours section */}
      <div className="Combo">
        <h2>Combo Tour</h2>
        <div className="combo-tour-list">
          <button onClick={handlePrevious} disabled={startIndex === 0}>
            {"<"}
          </button>
          {comboTours.slice(startIndex, startIndex + 3).map((tour, index) => (
            <div key={index} className="combo-tour-item">
              <img
                src={tour.tourImage || "default_image.jpg"}
                alt={tour.name}
              />
              <h3>{tour.tourName}</h3>
              <p>
                <strong>Giá:</strong> {tour.price.toLocaleString()} VND
              </p>
              <button
                onClick={() => {
                  navigate(`/tour-detail/${tour.tourId}`);
                  window.location.reload();
                }}
              >
                Xem chi tiết
              </button>
            </div>
          ))}
          <button
            onClick={handleNext}
            disabled={startIndex >= comboTours.length - 3}
          >
            {">"}
          </button>
        </div>
      </div>

      <div>
        <p className="Note">
          Lưu ý: Tùy theo tình hình thực tế tại điểm đến, thứ tự tham quan có
          thể thay đổi để phù hợp hơn, nhưng vẫn đảm bảo đủ chương trình. Trong
          trường hợp khách quan bắt buộc phải đổi điểm vì lý do điểm đến, HDV sẽ
          tìm điểm phù hợp cung đường thay thế cho Đoàn.
        </p>
      </div>
      <Footer />
    </div>
  );
};

export default TourDetailPage;
