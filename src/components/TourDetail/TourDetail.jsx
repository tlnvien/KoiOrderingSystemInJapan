import React from "react";
import { useParams } from "react-router-dom";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./TourDetail.css";

// Assuming you're using the same image imports as in SearchPage
import Tour1 from "./assets/tour1.jpg";
import Tour2 from "./assets/tour2.jpg";
import Tour3 from "./assets/tour3.jpg";
import Tour4 from "./assets/tour4.jpg";
import Tour5 from "./assets/tour5.jpg";
import Tour6 from "./assets/tour6.jpg";
import Tour7 from "./assets/tour7.jpg";
import Tour8 from "./assets/tour8.jpg";
import Tour9 from "./assets/tour9.jpg";
import Tour10 from "./assets/tour10.jpg";
import Koi1 from "./assets/koi-fish.jpg"; // Koi images
import Koi2 from "./assets/koi-fish1.jpg";
import Koi3 from "./assets/koi-fish2.jpg";

const tourData = [
  {
    id: 1,
    title: "Matsue Nishikigoi Center - Dainichi Koi Farm - Otsuka Koi Farm",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 20000000, // Giá dưới dạng số
    duration: "7days", // Thời gian tour
    image: Tour1,
  },
  {
    id: 2,
    title: "Tour Nhật Bản Tiêu Chuẩn 2",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 15000000, // Giá dưới dạng số
    duration: "5days", // Thời gian tour
    image: Tour2,
  },
  {
    id: 3,
    title: "Tour Khám Phá Tokyo",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 18000000,
    duration: "4days",
    image: Tour3,
  },
  {
    id: 4,
    title: "Tour Kyoto Cổ Kính",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 22000000,
    duration: "6days",
    image: Tour4,
  },
  {
    id: 5,
    title: "Tour Thưởng Thức Ẩm Thực Nhật Bản",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 16000000,
    duration: "3days",
    image: Tour5,
  },
  {
    id: 6,
    title: "Tour Tham Quan Fuji",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 12000000,
    duration: "2days",
    image: Tour6,
  },
  {
    id: 7,
    title: "Tour Châu Á Kỳ Diệu",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 30000000,
    duration: "10days",
    image: Tour7,
  },
  {
    id: 8,
    title: "Tour Mùa Hoa Anh Đào",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 25000000,
    duration: "5days",
    image: Tour8,
  },
  {
    id: 9,
    title: "Tour Đắm Chìm Trong Văn Hóa",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 23000000,
    duration: "7days",
    image: Tour9,
  },
  {
    id: 10,
    title: "Tour Biển Okinawa",
    description:
      "Mã tour: T001\nThời gian: 4N3Đ\nNgày khởi hành: 01/10/2024\nSố chỗ còn: 5",
    price: 21000000,
    duration: "5days",
    image: Tour10,
  },
];

const koiVarieties = [
  {
    name: "Kohaku",
    image: Koi1,
  },
  {
    name: "Sanke",
    image: Koi2,
  },
  {
    name: "Showa",
    image: Koi3,
  },
];

const TourDetailPage = () => {
  const { id } = useParams(); // Get the tour ID from the URL
  const tour = tourData.find((tour) => tour.id === Number(id));

  if (!tour) {
    return <div>Tour not found</div>;
  }

  return (
    <div className="tour-detail-container">
      <Header />
      <h1 className="tour-title">{tour.title}</h1>
      <div className="tour-detail-content">
        <div className="tour-left">
          <img
            src={tour.image}
            alt={tour.title}
            className="tour-detail-image"
          />
        </div>
        <div className="tour-right">
          <form className="tour-description-form">
            <div className="tour-description">
              <p>
                {tour.description.split("\n").map((line, index) => (
                  <span key={index}>
                    {line}
                    <br />
                  </span>
                ))}
              </p>
              <p>Giá: {tour.price.toLocaleString()} VND</p>
            </div>
            <button className="tour-button">Đặt Tour</button>
          </form>
        </div>
      </div>
      {/* New Section for Koi Fish Varieties */}
      <div className="koi-varieties-section">
        <h2 className="koi-title">Các Giống Cá Koi Nổi Bật</h2>
        <div className="koi-varieties">
          {koiVarieties.map((koi, index) => (
            <div key={index} className="koi-item">
              <img src={koi.image} alt={koi.name} className="koi-image" />
              <p className="koi-name">{koi.name}</p>
            </div>
          ))}
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default TourDetailPage;
