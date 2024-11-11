import React, { useState, useEffect } from "react";
import { Form, Input, Button, Card, Row, Col, message, Divider } from "antd";
import api from "../../../config/axios";
import "./ListOrder.css"; // Import file CSS

const ListOrder = () => {
  const [tourId, setTourId] = useState("");
  const [orders, setOrders] = useState([]);
  const [role, setRole] = useState(""); // User role
  const [token, setToken] = useState(""); // Token for API calls

  // Get role and token from local storage
  useEffect(() => {
    const storedRole = localStorage.getItem("role");
    const storedToken = localStorage.getItem("token");
    setRole(storedRole);
    setToken(storedToken);
  }, []);

  // Load orders based on tourId
  const loadOrders = async () => {
    if (!tourId) {
      message.error("Vui lòng nhập mã tour.");
      return;
    }

    try {
      const response = await api.get(`order/tour/${tourId}`, {
        headers: {
          Authorization: `Bearer ${token}`, // Include token in the request
        },
      });
      setOrders(response.data);
    } catch (error) {
      console.error("Lỗi khi tải danh sách đơn hàng:", error);
      message.error("Không thể tải danh sách đơn hàng.");
    }
  };

  // Handle payment
  const handlePayment = async (orderId, isFinalPayment) => {
    try {
      const response = await api.post(
        `order/paymentUrl/${orderId}?isFinalPayment=${isFinalPayment}`,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include token in the request
          },
        }
      );
      // Open the payment URL in a new tab
      window.open(response.data, "_blank");
    } catch (error) {
      console.error("Lỗi khi thanh toán:", error);
      message.error("Không thể thực hiện thanh toán.");
    }
  };

  return (
    <div>
      <h2>Xem Danh Sách Đơn Hàng</h2>
      <Form layout="inline" onFinish={loadOrders}>
        <Form.Item label="Mã Tour">
          <Input
            value={tourId}
            onChange={(e) => setTourId(e.target.value)}
            placeholder="Nhập mã tour"
            required
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            Xem Danh Sách
          </Button>
        </Form.Item>
      </Form>

      <div className="order-list" style={{ marginTop: 16 }}>
        {orders.length > 0 ? (
          orders.map((order) => (
            <Card
              key={order.orderId}
              className="order-card"
              bordered={true}
              style={{ marginBottom: 16 }}
              hoverable // Thêm hiệu ứng hover
            >
              <Row gutter={16}>
                <Col span={6}>
                  <strong>Mã Đơn Hàng:</strong> {order.orderId}
                </Col>
                <Col span={6}>
                  <strong>Mã Khách Hàng:</strong> {order.customerId}
                </Col>
                <Col span={6}>
                  <strong>Tên khách hàng:</strong> {order.customerName}
                </Col>
                <Col span={6}>
                  <strong>Địa chỉ:</strong> {order.customerAddress}
                </Col>
                <Col span={6}>
                  <strong>Mã Tour:</strong> {order.tourId}
                </Col>
                <Col span={6}>
                  <strong>Tổng Giá:</strong> {order.totalPrice.toLocaleString()}{" "}
                  VND
                </Col>
              </Row>
              <Divider />
              <Row gutter={16}>
                <Col span={6}>
                  <strong>Ngày Đặt:</strong> {order.orderDate}
                </Col>
                <Col span={6}>
                  <strong>Trạng Thái:</strong> {order.status}
                </Col>
                <Col span={12}>
                  <strong>Ghi Chú:</strong> {order.note}
                </Col>
              </Row>
              <h4 style={{ marginTop: 16 }}>Chi Tiết Đơn Hàng:</h4>
              {order.orderDetails.map((detail) => (
                <div key={detail.koiId} className="order-detail">
                  <p>
                    <strong>Mã Koi:</strong> {detail.koiId}
                  </p>
                  <p>
                    <strong>Mô Tả:</strong> {detail.description}
                  </p>
                  <p>
                    <strong>Số Lượng:</strong> {detail.quantity}
                  </p>
                  <p>
                    <strong>Giá:</strong> {detail.price.toLocaleString()} VND
                  </p>
                  <Divider />
                </div>
              ))}
              {/* Conditional Payment Button */}
              {role === "CONSULTING" && (
                <Button
                  type="primary"
                  onClick={() => handlePayment(order.orderId, "FIRST_PAYMENT")}
                  style={{ marginTop: 16 }}
                >
                  Thanh Toán
                </Button>
              )}
              {role === "DELIVERING" && (
                <Button
                  type="primary"
                  onClick={() => handlePayment(order.orderId, "FINAL_PAYMENT")}
                  style={{ marginTop: 16 }}
                >
                  Thanh Toán
                </Button>
              )}
            </Card>
          ))
        ) : (
          <p>Không có đơn hàng nào.</p>
        )}
      </div>
    </div>
  );
};

export default ListOrder;
