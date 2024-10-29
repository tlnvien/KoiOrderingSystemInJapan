import React, { useState, useEffect } from "react";
import { Button, Card, Row, Col, message, Divider } from "antd";
import api from "../../../config/axios";
import "./ListOrder.css"; // Import CSS file

const ListOrderD = () => {
  const [orders, setOrders] = useState([]);
  const token = localStorage.getItem("token");
  const deliveringId = localStorage.getItem("deliveringId"); // Get deliveringId from local storage

  // Function to fetch orders
  const fetchData = async () => {
    try {
      const response = await api.get(`order/list/received`, {
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

  // Function to handle receiving the order
  const handleReceiveOrder = async (orderId) => {
    try {
      if (!deliveringId) {
        message.error("deliveringId không tồn tại trong localStorage.");
        return;
      }

      const response = await api.post(
        `delivering/order/${deliveringId}?orderId=${orderId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Include token in the request
          },
        }
      );

      message.success(`Đơn hàng ${orderId} đã được nhận!`);
      console.log("Response from receiving order:", response.data);
    } catch (error) {
      console.error("Lỗi khi nhận đơn hàng:", error);
      message.error("Không thể nhận đơn hàng.");
    }
  };

  // Get role and token from local storage
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div>
      <h2>Xem Danh Sách Đơn Hàng</h2>

      <div className="order-list" style={{ marginTop: 16 }}>
        {orders.length > 0 ? (
          orders.map((order) => (
            <Card
              key={order.orderId}
              className="order-card"
              bordered={true}
              style={{ marginBottom: 16 }}
              hoverable
            >
              <Row gutter={16}>
                <Col span={6}>
                  <strong>Mã Đơn Hàng:</strong> {order.orderId}
                </Col>
                <Col span={6}>
                  <strong>Mã Khách Hàng:</strong> {order.customerId}
                </Col>
                <Col span={6}>
                  <strong>Mã Tour:</strong> {order.tourId}
                </Col>
                <Col span={6}>
                  <strong>Địa chỉ giao hàng:</strong> {order.customerAddress}
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

              <div className="order-buttons">
                <Button
                  type="primary"
                  onClick={() => handleReceiveOrder(order.orderId)}
                  style={{ marginRight: 8 }} // Adjust spacing
                >
                  Nhận Đơn
                </Button>
              </div>
            </Card>
          ))
        ) : (
          <p>Không có đơn hàng nào.</p>
        )}
      </div>
    </div>
  );
};

export default ListOrderD;
