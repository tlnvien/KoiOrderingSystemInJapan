import React from "react";
import "./KoiFishDetail.css"; // Import CSS riêng cho trang này
import koiVarietyImage1 from "./assets/koi-fish.jpg";
import koiVarietyImage2 from "./assets/koi-fish1.jpg";
import koiVarietyImage3 from "./assets/koi-fish2.jpg";
import koiVarietyImage4 from "./assets/koi-fish3.jpg";
import koiVarietyImage5 from "./assets/koi-fish4.jpg";
import koiVarietyImage6 from "./assets/koi-fish5.jpg";
import koiVarietyImage7 from "./assets/koi-fish6.jpg";
import koiVarietyImage8 from "./assets/koi-fish7.jpg";
import koiVarietyImage9 from "./assets/koi-fish8.jpg";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";

const KoiDetails = () => {
  const koiVarieties = [
    {
      id: 1,
      name: "Kohaku Koi",
      description:
        "Kohaku là một trong những giống cá Koi đầu tiên được phát triển với thân màu trắng và các vệt đỏ. Đây là biểu tượng của sự sinh sôi và trường thọ, thường được dùng làm quà tặng mang ý nghĩa phúc đức.",
      image: koiVarietyImage1,
    },
    {
      id: 2,
      name: "Showa Koi",
      description:
        "Showa Koi có màu đen chủ đạo với các vệt đỏ và trắng. Đặc điểm nổi bật của Showa là vệt đen (sumi) trên vây và đầu, tạo nên sự hài hòa trong phân bố màu sắc.",
      image: koiVarietyImage2,
    },
    {
      id: 3,
      name: "Sanke Koi",
      description:
        "Sanke Koi có thân trắng, với các vệt đỏ và đốm đen mềm mại trên lưng. Không có đốm đen trên đầu, giúp phân biệt dễ dàng với Showa Koi.",
      image: koiVarietyImage3,
    },
    {
      id: 4,
      name: "Doitsu Koi",
      description:
        "Cá Koi Doitsu nổi bật với sự đột biến về vẩy so với các giống cá khác. Doitsu có đặc điểm là không có vảy toàn thân, chỉ có hai đường vẩy nhỏ dọc theo vây lưng.",
      image: koiVarietyImage4,
    },
    {
      id: 5,
      name: "Tancho Koi",
      description:
        "Koi Tancho nổi bật với chấm tròn màu đỏ nằm chính giữa đầu, được coi là biểu tượng của lá cờ Nhật Bản.",
      image: koiVarietyImage5,
    },
    {
      id: 6,
      name: "Kumonryu Koi",
      description:
        "Cá Koi Kumonryu có thân màu trắng và các đốm đen rải rác toàn thân, tượng trưng cho sự thay đổi và vượt qua nghịch cảnh.",
      image: koiVarietyImage6,
    },
    {
      id: 7,
      name: "Gin Matsuba Koi",
      description:
        "Gin Matsuba nổi bật với thân màu bạc và lớp vảy có hiệu ứng lưới đen trên lưng, mang lại vẻ đẹp đơn giản nhưng tinh tế.",
      image: koiVarietyImage7,
    },
    {
      id: 8,
      name: "Karashigoi Koi",
      description:
        "Karashigoi là giống Koi màu vàng óng, phát triển nhanh và có kích thước lớn, tạo nên sự độc đáo trong cộng đồng yêu cá Koi.",
      image: koiVarietyImage8,
    },
    {
      id: 9,
      name: "Ki Utsuri Koi",
      description:
        "Ki Utsuri rất hiếm với màu đỏ tươi và các mảng đen rải rác, tạo nên sự tương phản màu sắc nổi bật.",
      image: koiVarietyImage9,
    },
  ];

  return (
    <div className="koi-details-container">
      <Header />
      <h1>Thông tin các giống Cá Koi</h1>

      {koiVarieties.map((koi) => (
        <div key={koi.id} className="koi-item">
          <div className="koi-left">
            <img src={koi.image} alt={koi.name} className="koi-image" />
          </div>
          <div className="koi-right">
            <h2>{koi.name}</h2>
            <p>{koi.description}</p>
          </div>
        </div>
      ))}

      <Footer />
    </div>
  );
};

export default KoiDetails;
