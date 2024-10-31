import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Payment.css"; // Import the CSS file
import api from "../../config/axios";

const PaymentPage = () => {
  const [loading, setLoading] = useState(false); // New state for loading
  const token = localStorage.getItem("token");

  const bookingId = localStorage.getItem("bookingId");

  // Function to initiate payment with bookingId
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

  return (
    <div className="payment-container">
      <h1 className="payment-title">Payment Page</h1>
      <div>
        <h2>Initiate Payment</h2>
        <input
          type="text"
          className="payment-input" // Apply input field styles
          placeholder="Booking ID"
          value={bookingId}
        />
        <button
          className="payment-button"
          onClick={initiatePayment}
          disabled={loading || !bookingId} // Disable button when loading or if bookingId is empty
        >
          {loading ? "Processing..." : "Tiến hành thanh toán"}
        </button>
      </div>
    </div>
  );
};

export default PaymentPage;
