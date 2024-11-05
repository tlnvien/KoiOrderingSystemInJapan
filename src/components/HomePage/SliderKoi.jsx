import React, { useState } from "react";
import "./Slider.css";
import FarmVarierties1 from "./assets/koi-fish.jpg";
import FarmVarierties2 from "./assets/koi-fish1.jpg";
import FarmVarierties3 from "./assets/koi-fish2.jpg";
import FarmVarierties4 from "./assets/koi-fish3.jpg";
import FarmVarierties5 from "./assets/koi-fish4.jpg";
import FarmVarierties6 from "./assets/koi-fish5.jpg";
import FarmVarierties7 from "./assets/koi-fish6.jpg";
import FarmVarierties8 from "./assets/koi-fish7.jpg";
import FarmVarierties9 from "./assets/koi-fish8.jpg";

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
