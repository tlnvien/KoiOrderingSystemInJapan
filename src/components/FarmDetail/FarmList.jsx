import React, { useState, useEffect } from "react";
import "./FarmList.css"; // Import CSS for farms
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import { Link } from "react-router-dom";
import { notification } from "antd";
import api from "../../config/axios";

const FarmList = () => {
  const [farms, setFarms] = useState([]);
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchFarmList();
  }, []);

  const fetchFarmList = async () => {
    try {
      const response = await api.get("farm/list", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setFarms(response.data);
    } catch (error) {
      notification.error({ message: "Không thể lấy danh sách trang trại" });
      console.error("Error fetching farm list:", error);
    }
  };

  return (
    <div className="farm-list-container">
      <Header />
      <h1>Danh sách các trại cá Koi</h1>

      {/* Render farm cards */}
      {farms.map((farm) => (
        <div key={farm.farmId} className="farm-container">
          <div className="farm-left">
            <img
              src={farm.imageLinks && farm.imageLinks[0]?.imageLink}
              alt={farm.farmName}
              className="farm-image"
            />
          </div>
          <div className="farm-right">
            <Link to={`/farms/${farm.farmId}`} className="farm-link">
              <h2>{farm.farmName}</h2>
            </Link>
            <p>{farm.description}</p>
          </div>
        </div>
      ))}
      <Footer />
    </div>
  );
};

export default FarmList;
