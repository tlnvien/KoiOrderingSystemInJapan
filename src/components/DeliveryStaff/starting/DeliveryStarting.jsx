import React, { useEffect, useState } from "react";
import { Card, Row, Col, message, Divider, Button } from "antd";
import api from "../../../config/axios"; // Ensure your axios config is correct
import "./DeliveryStarting.css"; // Import your CSS file

const DeliveryStarting = () => {
  const [deliveries, setDeliveries] = useState([]);
  const token = localStorage.getItem("token");
  const status = "STARTING"; // Set the status as needed
  const FINAL_PAYMENT = "FINAL_PAYMENT";

  // Function to fetch delivery data
  const fetchDeliveries = async () => {
    try {
      const response = await api.get(`delivering/all/status?status=${status}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setDeliveries(response.data);
    } catch (error) {
      console.error("Lỗi khi tải danh sách đơn giao hàng:", error);
      message.error("Không thể tải danh sách đơn giao hàng.");
    }
  };

  const handlePayment = async (orderId) => {
    try {
      const response = await api.post(
        `order/paymentUrl/${orderId}?isFinalPayment=${FINAL_PAYMENT}`,
        {},
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

  // Use effect to fetch deliveries on component mount
  useEffect(() => {
    fetchDeliveries();
  }, []);

  return (
    <div>
      <h2>Danh Sách Đơn Giao Đang Bắt Đầu</h2>
      <div style={{ marginTop: 16 }}>
        {deliveries.length > 0 ? (
          deliveries.map((delivery) => (
            <Card key={delivery.deliveringId} className="delivery-card">
              <Row gutter={16}>
                <Col span={6} className="delivery-info">
                  <strong>Mã Giao Hàng:</strong> {delivery.deliveringId}
                </Col>
                <Col span={6} className="delivery-info">
                  <strong>Mã Nhân Viên Giao Hàng:</strong>{" "}
                  {delivery.deliveringStaffId}
                </Col>
                <Col span={6} className="delivery-info">
                  <strong>Ngày Giao:</strong> {delivery.deliverDate}
                </Col>
                <Col span={6} className="delivery-info">
                  <strong>Thông Tin:</strong> {delivery.information}
                </Col>
              </Row>
              <Divider />
              <h4>Thông Tin Đơn Hàng:</h4>
              {delivery.orderResponses.map((order) => (
                <div key={order.orderId} className="order-response">
                  <Row style={{ marginBottom: 8 }}>
                    <Col span={12}>
                      <strong>Mã Đơn Hàng:</strong> {order.orderId}
                    </Col>
                    <Col span={12}>
                      <strong>Khách Hàng:</strong> {order.fullName}
                    </Col>
                    <Col span={12}>
                      <strong>Điện Thoại:</strong> {order.phone}
                    </Col>
                  </Row>
                  <Button
                    type="primary"
                    onClick={() => handlePayment(order.orderId)}
                    style={{ position: "relative", bottom: 0, right: 0 }}
                  >
                    Thanh Toán Phần Còn Lại
                  </Button>
                </div>
              ))}
            </Card>
          ))
        ) : (
          <p>Không có đơn giao hàng nào.</p>
        )}
      </div>
    </div>
  );
};

export default DeliveryStarting;
