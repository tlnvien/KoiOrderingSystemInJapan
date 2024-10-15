import React, { useEffect, useState } from "react";
import axios from "axios";
import "./Payment.css"; // Import the CSS file

const PaymentPage = () => {
  const [paymentId, setPaymentId] = useState("");
  const [paymentMethod, setPaymentMethod] = useState(""); // e.g. "VNPAY"
  const [totalPrice, setTotalPrice] = useState(0);
  const [description, setDescription] = useState("");
  const [paymentStatus, setPaymentStatus] = useState("");
  const [paymentDetails, setPaymentDetails] = useState(null);
  const [loading, setLoading] = useState(false); // New state for loading
  const [currentTime, setCurrentTime] = useState(""); // State để lưu thời gian hiện tại

  let timer;

  // Hàm cập nhật thời gian
  const updateTime = () => {
    const now = new Date();
    setCurrentTime(now.toLocaleTimeString());
  };

  // Khởi tạo timer để cập nhật thời gian
  useEffect(() => {
    timer = setInterval(updateTime, 1000);
    return () => clearInterval(timer); // Dọn dẹp timer khi component unmount
  }, []);

  // Function to initiate payment
  const initiatePayment = async () => {
    const paymentDetails = {
      id: paymentId,
      method: paymentMethod,
      price: totalPrice,
      description: description,
      paymentType: "vnpay", // You can change this as needed
    };

    try {
      setLoading(true); // Start loading
      const response = await axios.post(
        "http://localhost:8082/api/payment/initiate",
        paymentDetails
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

  // Function to complete payment
  const completePayment = async () => {
    const queryParams = new URLSearchParams(window.location.search);

    try {
      setLoading(true); // Start loading
      await axios.post(
        `http://localhost:8082/api/payment/complete/${paymentId}`,
        queryParams
      );
      alert("Payment completed successfully!");
    } catch (error) {
      console.error("Error completing payment", error);
      alert("Error completing payment. Please try again.");
    } finally {
      setLoading(false); // Stop loading
    }
  };

  // Function to search payment status
  const searchPayments = async () => {
    const searchParams = {
      paymentId: paymentId,
      customerId: "yourDynamicCustomerId", // Set this dynamically as needed
      status: paymentStatus, // e.g. SUCCESS, FAILED
    };

    try {
      setLoading(true); // Start loading
      const response = await axios.get(
        "http://localhost:8082/api/payment/search",
        { params: searchParams }
      );
      setPaymentDetails(response.data);
    } catch (error) {
      console.error("Error searching payments", error);
      alert("Error searching payments. Please try again.");
    } finally {
      setLoading(false); // Stop loading
    }
  };

  // Call completePayment on component mount if redirected from VNPAY
  useEffect(() => {
    const isReturnUrl =
      window.location.pathname === "/api/payment/vnpay-return"; // Adjust this path as needed
    if (isReturnUrl) {
      completePayment();
    }
  }, []);

  return (
    <div className="payment-container">
      <h1 className="payment-title">Payment Page</h1>
      <h2>Current Time: {currentTime}</h2> {/* Hiển thị thời gian hiện tại */}
      <div>
        <h2>Initiate Payment</h2>
        <input
          type="text"
          className="payment-input" // Apply input field styles
          placeholder="Payment ID"
          value={paymentId}
          onChange={(e) => setPaymentId(e.target.value)}
        />
        <input
          type="text"
          className="payment-input"
          placeholder="Payment Method"
          value={paymentMethod}
          onChange={(e) => setPaymentMethod(e.target.value)}
        />
        <input
          type="number"
          className="payment-input"
          placeholder="Total Price"
          value={totalPrice}
          onChange={(e) => setTotalPrice(Number(e.target.value))}
        />
        <input
          type="text"
          className="payment-input"
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button
          className="payment-button"
          onClick={initiatePayment}
          disabled={loading} // Disable button when loading
        >
          {loading ? "Processing..." : "Pay Now"}
        </button>
      </div>
      <div>
        <h2>Search Payment</h2>
        <input
          type="text"
          className="payment-input"
          placeholder="Payment ID"
          value={paymentId}
          onChange={(e) => setPaymentId(e.target.value)}
        />
        <input
          type="text"
          className="payment-input"
          placeholder="Payment Status"
          value={paymentStatus}
          onChange={(e) => setPaymentStatus(e.target.value)}
        />
        <button
          className="payment-button"
          onClick={searchPayments}
          disabled={loading} // Disable button when loading
        >
          {loading ? "Searching..." : "Search"}
        </button>

        {paymentDetails && (
          <div className="payment-details">
            <h3>Payment Details</h3>
            <pre>{JSON.stringify(paymentDetails, null, 2)}</pre>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentPage;
