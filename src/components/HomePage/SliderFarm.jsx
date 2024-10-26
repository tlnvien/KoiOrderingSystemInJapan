import React, { useState } from "react";
import "./Slider.css";
import FarmVarierties1 from "./assets/farm1.jpg";
import FarmVarierties2 from "./assets/farm2.jpg";
import FarmVarierties3 from "./assets/farm3.jpg";
import FarmVarierties4 from "./assets/farm4.jpg";
import FarmVarierties5 from "./assets/farm5.jpg";
import FarmVarierties6 from "./assets/farm2.jpg";
import FarmVarierties7 from "./assets/farm1.jpg";
import FarmVarierties8 from "./assets/farm4.jpg";
import FarmVarierties9 from "./assets/farm3.jpg";

const Slider = () => {
  const images = [
    FarmVarierties1,
    FarmVarierties2,
    FarmVarierties3,
    FarmVarierties4,
    FarmVarierties5,
    FarmVarierties6,
    FarmVarierties7,
    FarmVarierties8,
    FarmVarierties9,
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
