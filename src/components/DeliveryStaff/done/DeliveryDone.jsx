import React, { useEffect, useState } from "react";
import { Card, Row, Col, Divider, Spin } from "antd";
import api from "../../../config/axios";
import "./DeliveryDone.css";

const DeliveryDone = () => {
  const [deliveries, setDeliveries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const status = "DONE";

  const fetchDeliveries = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await api.get(`delivering/all/status?status=${status}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setDeliveries(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching deliveries:", error);
      setError("Could not load delivery list.");
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDeliveries();
  }, []);

  if (loading) {
    return (
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
        }}
      >
        <Spin tip="Loading deliveries..." />
      </div>
    );
  }

  if (error) return <p>{error}</p>;

  return (
    <div>
      <h2>Danh Sách Đơn Đã Giao</h2>
      <div style={{ marginTop: 16 }}>
        {deliveries.length > 0 ? (
          deliveries.map(
            ({
              deliveringId,
              deliveringStaffId,
              deliverDate,
              information,
              orderResponses,
            }) => (
              <Card key={deliveringId} className="delivery-card">
                <Row gutter={16}>
                  <Col span={6} className="delivery-info">
                    <strong>Mã Giao Hàng:</strong> {deliveringId}
                  </Col>
                  <Col span={6} className="delivery-info">
                    <strong>Mã Nhân Viên Giao Hàng:</strong> {deliveringStaffId}
                  </Col>
                  <Col span={6} className="delivery-info">
                    <strong>Ngày Giao:</strong> {deliverDate}
                  </Col>
                  <Col span={6} className="delivery-info">
                    <strong>Thông Tin:</strong> {information}
                  </Col>
                </Row>
                <Divider />
                <h4>Thông Tin Đơn Hàng:</h4>
                {orderResponses.map(({ orderId, fullName, phone }) => (
                  <div key={orderId} className="order-response">
                    <Row style={{ marginBottom: 8 }}>
                      <Col span={12}>
                        <strong>Mã Đơn Hàng:</strong> {orderId}
                      </Col>
                      <Col span={12}>
                        <strong>Khách Hàng:</strong> {fullName}
                      </Col>
                      <Col span={12}>
                        <strong>Điện Thoại:</strong> {phone}
                      </Col>
                    </Row>
                  </div>
                ))}
              </Card>
            )
          )
        ) : (
          <p>Không có đơn giao hàng nào.</p>
        )}
      </div>
    </div>
  );
};

export default DeliveryDone;
