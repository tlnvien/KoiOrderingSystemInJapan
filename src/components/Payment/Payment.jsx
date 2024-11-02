import React, { useEffect, useState } from "react";
import "./Payment.css"; // Import the CSS file
import api from "../../config/axios";
import { Spin, Typography } from "antd";

const { Text } = Typography;

const PaymentPage = () => {
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem("token");
  const bookingId = localStorage.getItem("bookingId"); // Get the bookingId from local storage

  const initiatePayment = async () => {
    try {
      setLoading(true); // Start loading
      const response = await api.post(
        `booking/paymentUrl/${bookingId}`, // API endpoint
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include the token in headers
          },
        }
      );
      const paymentUrl = response.data;
      window.location.href = paymentUrl; // Redirect to the payment URL
    } catch (error) {
      console.error("Error initiating payment", error);
      alert("Error initiating payment. Please try again.");
    } finally {
      setLoading(false); // Stop loading
    }
  };

  useEffect(() => {
    initiatePayment();
  }, []);

  return (
    <div style={{ textAlign: "center", marginTop: "20px" }}>
      {loading ? (
        <>
          <Spin size="large" /> {/* Loading icon */}
          <Text style={{ display: "block", marginTop: "10px" }}>
            Vui lòng chờ trong giây lát...
          </Text>
        </>
      ) : null}
    </div>
  );
};

export default PaymentPage;
