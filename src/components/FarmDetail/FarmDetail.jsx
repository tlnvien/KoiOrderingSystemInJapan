import React from "react";
import "./FarmDetail.css"; // Import CSS for farms
import farmImage1 from "./assets/farm1.jpg";
import farmImage2 from "./assets/farm2.jpg";
import farmImage3 from "./assets/farm3.jpg";
import farmImage4 from "./assets/farm4.jpg";
import farmImage5 from "./assets/farm5.jpg";
import koiImage from "./assets/koi-fish.jpg";
import koiImage1 from "./assets/koi-fish1.jpg";
import koiImage2 from "./assets/koi-fish2.jpg";
import koiImage3 from "./assets/koi-fish3.jpg";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import { useParams, Link } from "react-router-dom";
import Slider from "./Slider"; // Import Slider component

const farms = [
  {
    id: 1,
    name: "Matsue Nishikigoi Center",
    description:
      "Matsue Nishikigoi Center là một trong các trại cá Koi Nhật Bản nổi tiếng với quy mô lớn. Matsue Nishikigoi được thành lập bởi ông Shoichi Iizuka vào tháng 4 năm 1996. Các dòng cá Koi tại trung tâm Matsue rất đa dạng như Kohaku, Showa, Sanke, Doitsu. Nhưng trung tâm được biết đến rộng rãi là nhờ dòng cá Koi Jumbo Kohaku.",
    image: farmImage1,
    famousVarieties: [
      { name: "Kohaku", images: [koiImage, koiImage1, koiImage2, koiImage3] },
      { name: "Showa", images: [koiImage1, koiImage2, koiImage3, koiImage] },
      { name: "Sanke", images: [koiImage2, koiImage, koiImage1, koiImage3] },
    ],
  },
  {
    id: 2,
    name: "Dainichi Koi Farm",
    description:
      "Đây là một trong các trại cá Koi Nhật Bản nổi tiếng bậc nhất với hệ thống ao rộng và trong lành. Hệ thống hồ nuôi cá Koi sạch sẽ và đạt tiêu chuẩn cao. Dainichi Koi Farm cung cấp đa dạng dòng cá Koi, từ cá Koi nhỏ đến cá Koi trưởng thành Jumbo.",
    image: farmImage2,
    famousVarieties: [
      { name: "Kohaku", image: koiImage },
      { name: "Tancho", image: koiImage3 },
    ],
  },
  {
    id: 3,
    name: "Fukasawa Koi Farm",
    description:
      "Fukasawa Koi Farm sở hữu khu vực nuôi cá Koi để bán và cá Koi giống khác nhau. Ở đây có nguồn nước sạch tự nhiên rất tốt cho nuôi dưỡng cá Koi. Thế mạnh của trại cá này là cá Koi Kohaku, Tancho, Showa, Kumonryu…Kích thước cá Koi ở đây lên tới 40cm. Các dòng cá nổi bật: Tancho, Kohaku, Kumonryu, Showa.",
    image: farmImage3,
    famousVarieties: [
      { name: "Tancho", image: koiImage3 },
      { name: "Kohaku", image: koiImage },
    ],
  },
  {
    id: 4,
    name: "Marusei Koi Farm",
    description:
      "Marusei là một trong những trại cá lớn nhất ở vùng Niigata và cũng là nơi lai tạo đa dạng các dòng cá Koi. Quy mô của Marusei Koi Farm rất lớn, hơn 200 ao cá cho ra sản lượng 200.000 con cá Koi mỗi năm. Tốc độ sản xuất cá Koi lớn, lai tạo đa dạng nhiều dòng cá Koi, giá cả hợp lý. Đây là những điểm nổi bật khiến Marusei Koi Farm trở thành một trong các “ông lớn” trong ngành lai tạo và kinh doanh cá Koi. Trại cá Marusei lai tạo rất nhiều dòng cá Koi như Matsuba, Hi Utsuri, Gosanke, Chagoi, Benigoi, Asagi, Shusui, Yamabuki. Các dòng cá nổi bật: Gosanke, Gin Matsuba, Karashigoi, Hi Utsuri, Mizuho Ogon, Aka Matsuba.",
    image: farmImage4,
    famousVarieties: [
      { name: "Gosanke", image: koiImage1 },
      { name: "Gin Matsuba", image: koiImage2 },
    ],
  },
  {
    id: 5,
    name: "Omosako Koi Farm",
    description:
      "Trại cá Koi Nhật Bản Omosako Koi Farm được thành lập vào năm 1953 bởi ông Takashi Omosako. Trại cá tọa lạc ở Hiroshima - vùng nuôi cá Koi lớn nhất Nhật Bản. Hai dòng cá nổi bật nhất ở Omosako là Shiro Utsuri và Showa. Mỗi dòng cá này được hai đội riêng lẻ của trại cá nhân giống và nuôi dưỡng.",
    image: farmImage5,
    famousVarieties: [
      { name: "Shiro Utsuri", image: koiImage1 },
      { name: "Showa", image: koiImage3 },
    ],
  },
];

const FarmDetail = () => {
  const { farmId } = useParams();
  const farm = farms.find((f) => f.id === parseInt(farmId));

  return (
    <div className="farm-detail-container">
      <Header />
      {farm ? (
        <div className="farm-detail">
          <h1>{farm.name}</h1>
          <div className="farm-detail-content">
            <img
              src={farm.image}
              alt={farm.name}
              className="farm-detail-image"
            />
            <div className="farm-description">
              <p>{farm.description}</p>
            </div>
          </div>
          <h2 className="famous-varieties-title">Giống Cá Nổi Tiếng</h2>
          {farm.famousVarieties.map((variety, index) => (
            <div key={index} className="variety-section">
              <h3>{variety.name}</h3>
              <Slider />
              {/* Sử dụng Slider component */}
            </div>
          ))}
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
