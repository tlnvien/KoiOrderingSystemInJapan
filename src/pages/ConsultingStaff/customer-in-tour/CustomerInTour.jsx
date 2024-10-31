import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom"; // Import useParams and useNavigate
import api from "../../../config/axios";
import "./CustomerInTour.css";

function CustomerInTour() {
  const { tourId } = useParams(); // Get tourId from URL parameters
  const navigate = useNavigate(); // Initialize navigate function
  const [customers, setCustomers] = useState([]);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        const response = await api.get(`tour/customers/${tourId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setCustomers(response.data);
      } catch (error) {
        console.error("Error fetching customers:", error);
      }
    };

    if (tourId) {
      fetchCustomers();
    }
  }, [tourId]);

  const handleCreateOrder = (customerId) => {
    // Navigate to the Create Order page with tourId and customerId as parameters
    navigate(
      `/dashboard/consulting/create-order?tourId=${tourId}&customerId=${customerId}`
    );
  };

  return (
    <div className="customer-list-container">
      <h2>Danh sách hành khách đã đặt tour</h2>
      {customers.length === 0 ? (
        <p>Chưa có hành khách nào trong tour này.</p>
      ) : (
        <div className="customer-cards-container">
          {customers.map((customer) => (
            <div key={customer.userID} className="customer-card">
              <div className="customer-info">
                <p>
                  <strong>Mã khách hàng:</strong> {customer.userID}
                </p>
                <p>
                  <strong>Họ tên:</strong> {customer.fullName}
                </p>
                <p>
                  <strong>Giới tính:</strong> {customer.gender}
                </p>
                <p>
                  <strong>Điện thoại:</strong> {customer.phone}
                </p>
                <p>
                  <strong>Ngày sinh:</strong> {customer.dob}
                </p>
                <p>
                  <strong>Địa chỉ:</strong> {customer.address}
                </p>
                <p>
                  <strong>Email:</strong> {customer.email}
                </p>
              </div>
              <button
                className="create-order-button"
                onClick={() => handleCreateOrder(customer.userID)}
              >
                Tạo đơn
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default CustomerInTour;
