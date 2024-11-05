import React, { useEffect, useState } from "react";
import axios from "axios";
import "./FarmDetail.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./Slider.css";
import { useParams, Link } from "react-router-dom";
import api from "../../config/axios";

const Slider = ({ images }) => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const itemsToShow = 3;

  const handlePrevClick = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === 0 ? images.length - 1 : prevIndex - 1
    );
  };

  const handleNextClick = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex >= images.length - 1 ? 0 : prevIndex + 1
    );
  };

  return (
    <div className="slider-container">
      <div
        className="slider-content"
        style={{
          transform: `translateX(-${(currentIndex / itemsToShow) * 100}%)`,
        }}
      >
        {images.map((image, index) => (
          <div className="slider-item" key={index}>
            <img src={image.imageLink} alt={`Slide ${index}`} />
          </div>
        ))}
      </div>
      <button className="arrow-button arrow-left" onClick={handlePrevClick}>
        ❮
      </button>
      <button className="arrow-button arrow-right" onClick={handleNextClick}>
        ❯
      </button>
    </div>
  );
};

const FarmDetail = () => {
  const { farmId } = useParams();
  const [farm, setFarm] = useState(null);
  const [koiVarieties, setKoiVarieties] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    const getAuthHeaders = () => {
      return token
        ? {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        : {};
    };
    api
      .get(`farm/details/${farmId}`, getAuthHeaders())
      .then((response) => {
        setFarm(response.data);
      })
      .catch((error) => {
        console.error("Error fetching farm data:", error);
        setError("Trang trại không tồn tại.");
      });

    api
      .get(`koiFarm/listKoi/${farmId}`, getAuthHeaders())
      .then((response) => {
        setKoiVarieties(response.data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Error fetching koi varieties:", error);
        setError("Không thể tải giống cá Koi.");
        setLoading(false);
      });
  }, [farmId]);

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  return (
    <div className="farm-detail-container">
      <Header />
      {farm ? (
        <div className="farm-detail">
          <h1>{farm.farmName}</h1>
          <div className="farm-detail-content">
            <img
              src={farm.imageLinks[0]?.imageLink} // Adjust to match API structure
              alt={farm.farmName}
              className="farm-detail-image"
            />
            <div className="farm-description">
              <p>{farm.description}</p>
            </div>
          </div>
          <h2 className="famous-varieties-title">Giống Cá Nổi Tiếng</h2>
          {koiVarieties.length > 0 ? (
            koiVarieties.map((koi, index) => (
              <div key={index} className="variety-section">
                <h3>{koi.species}</h3>
                <Slider images={koi.imageLinks} />
              </div>
            ))
          ) : (
            <p>Không có giống cá Koi nào nổi bật.</p>
          )}
          <Link to="/farm" className="back-button">
            Quay lại danh sách
          </Link>
        </div>
      ) : (
        <p>Trang trại không tồn tại.</p>
      )}
      <Footer />
    </div>
  );
};

export default FarmDetail;
