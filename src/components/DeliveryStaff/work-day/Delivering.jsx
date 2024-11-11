import React, { useState, useEffect } from "react";
import { Button, Card, Col, Divider, message, Row } from "antd";
import api from "../../../config/axios";

const Delivering = () => {
  const token = localStorage.getItem("token");
  const deliveringId = localStorage.getItem("deliveringId");
  const [delivering, setDelivering] = useState([]); // Khởi tạo là một mảng

  const fetchData = async () => {
    try {
      const response = await api.get(`order/list/${deliveringId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setDelivering(response.data); // Đảm bảo là một mảng
    } catch (error) {
      console.error("Lỗi khi lấy thông tin giao hàng:", error);
      message.error("Không thể lấy thông tin giao hàng.");
    }
  };

  // Hàm bắt đầu ngày làm việc
  const startWorkday = async () => {
    try {
      await api.put(`delivering/start/${deliveringId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      message.success("Ngày làm việc đã bắt đầu!");
      fetchData(); // Làm mới dữ liệu nếu cần
    } catch (error) {
      console.error("Lỗi khi bắt đầu ngày làm việc:", error);
      message.error("Không thể bắt đầu ngày làm việc.");
    }
  };

  // Hàm kết thúc ngày làm việc
  const endWorkday = async () => {
    try {
      await api.put(`delivering/end/${deliveringId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      message.success(
        "Cảm ơn bạn đã làm việc! Những đơn hàng chưa giao sẽ được chuyển về kho."
      );
      fetchData(); // Làm mới dữ liệu nếu cần
    } catch (error) {
      console.error("Lỗi khi kết thúc ngày làm việc:", error);
      message.error("Không thể kết thúc ngày làm việc.");
    }
  };

  // Lấy dữ liệu khi component được mount
  useEffect(() => {
    fetchData();
  }, []);

  return (
    <div>
      <h2>Quản Lý Ngày Làm Việc</h2>
      <div className="delivering-list" style={{ marginTop: 16 }}>
        {delivering.length > 0 ? (
          delivering.map((order) => (
            <Card
              key={order.orderId}
              className="delivering-card"
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
                  <strong>Tên khách hàng:</strong> {order.customerName}
                </Col>
                <Col span={6}>
                  <strong>Địa Chỉ Giao Hàng:</strong> {order.customerAddress}
                </Col>
                <Col span={6}>
                  <strong>Tổng Giá:</strong> {order.totalPrice}
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
              {order.orderDetails.map((detail, index) => (
                <div key={index} className="order-detail">
                  <p>
                    <strong>Loại Koi:</strong> {detail.species}
                  </p>
                  <p>
                    <strong>Mô Tả:</strong> {detail.description}
                  </p>
                  <p>
                    <strong>Số Lượng:</strong> {detail.quantity}
                  </p>
                  <p>
                    <strong>Giá:</strong> {detail.price}
                  </p>
                  <Divider />
                </div>
              ))}
            </Card>
          ))
        ) : (
          <p>Không có đơn hàng nào.</p>
        )}
      </div>
      <Button type="primary" onClick={startWorkday} style={{ marginRight: 8 }}>
        Bắt đầu làm việc
      </Button>
      <Button danger onClick={endWorkday}>
        Kết thúc làm việc
      </Button>
    </div>
  );
};

export default Delivering;
