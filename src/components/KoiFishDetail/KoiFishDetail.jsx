import React, { useState, useEffect } from "react";
import "./KoiFishDetail.css";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import api from "../../config/axios";

const KoiDetails = () => {
  const [koiVarieties, setKoiVarieties] = useState([]);
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchKoiList();
  }, []);

  const fetchKoiList = async () => {
    try {
      const response = await api.get("koi/list", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const data = response.data;

      if (Array.isArray(data)) {
        setKoiVarieties(data); // Set fetched koi list to state
      } else {
        console.error("Dữ liệu không phải là một mảng:", data);
        setKoiVarieties([]);
      }
    } catch (error) {
      console.error("Lỗi khi lấy danh sách Koi:", error);
    }
  };

  return (
    <div className="koi-details-container">
      <Header />
      <h1>Thông tin các giống Cá Koi</h1>

      {koiVarieties.length > 0 ? (
        koiVarieties.map((koi) => (
          <div key={koi.koiId} className="koi-item">
            <div className="koi-left">
              <img
                src={
                  koi.imageLinks && koi.imageLinks.length > 0
                    ? koi.imageLinks[0].imageLink
                    : ""
                }
                alt={koi.species}
                className="koi-image"
              />
            </div>
            <div className="koi-right">
              <h2>{koi.species}</h2>
              <p>{koi.description}</p>
            </div>
          </div>
        ))
      ) : (
        <p>Không có thông tin cá Koi</p>
      )}

      <Footer />
    </div>
  );
};

export default KoiDetails;