import React, { useState } from "react";
import "./Slider.css";
import KoiVarierties1 from "./assets/koi-fish1.jpg";
import KoiVarierties2 from "./assets/koi-fish2.jpg";
import KoiVarierties3 from "./assets/koi-fish3.jpg";
import KoiVarierties4 from "./assets/koi-fish3.jpg";
import KoiVarierties5 from "./assets/koi-fish3.jpg";
import KoiVarierties6 from "./assets/koi-fish1.jpg";
import KoiVarierties7 from "./assets/koi-fish2.jpg";
import KoiVarierties8 from "./assets/koi-fish1.jpg";
import KoiVarierties9 from "./assets/koi-fish2.jpg";

const Slider = () => {
  const images = [
    KoiVarierties1,
    KoiVarierties2,
    KoiVarierties3,
    KoiVarierties4,
    KoiVarierties5,
    KoiVarierties6,
    KoiVarierties7,
    KoiVarierties8,
    KoiVarierties9,
  ];
  const [currentIndex, setCurrentIndex] = useState(0);
  const itemsToShow = 3; // Số lượng ảnh hiển thị cùng lúc

  const handlePrevClick = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex === 0 ? images.length - itemsToShow : prevIndex - itemsToShow
    );
  };

  const handleNextClick = () => {
    setCurrentIndex((prevIndex) =>
      prevIndex >= images.length - itemsToShow ? 0 : prevIndex + itemsToShow
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
            <img src={image} alt={`Slide ${index}`} />
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

export default Slider;
