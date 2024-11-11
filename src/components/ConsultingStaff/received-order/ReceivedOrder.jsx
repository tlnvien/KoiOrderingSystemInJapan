import React, { useState } from "react";
import {
  Form,
  Input,
  Button,
  message,
  Card,
  List,
  Typography,
  Divider,
} from "antd";
import api from "../../../config/axios";

const { Title, Text } = Typography;

function ReceivedOrder() {
  const [loading, setLoading] = useState(false);
  const [tourId, setTourId] = useState();
  const [orderData, setOrderData] = useState([]);
  const token = localStorage.getItem("token");
  const CHECKED_STATUS = "RECEIVED";

  const fetchData = async (tourId) => {
    setLoading(true);
    try {
      const response = await api.get(`order/tour/${tourId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOrderData(response.data);
      message.success("Đã tải danh sách đơn hàng thành công!");
    } catch (error) {
      message.error(error.response?.data);
    } finally {
      setLoading(false);
    }
  };

  const handleTourIdChange = (e) => {
    setTourId(e.target.value);
  };

  const handleFetchOrders = () => {
    if (tourId) {
      fetchData(tourId);
    } else {
      message.warning("Vui lòng nhập mã tour hợp lệ.");
    }
  };

  const handleReceiveOrder = async (orderId) => {
    try {
      const response = await api.post(
        `order/consulting/${orderId}?status=${CHECKED_STATUS}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Cập nhật trạng thái đơn hàng thành công!");
      fetchData(tourId);
    } catch (error) {
      message.error(error.response?.data);
    }
  };

  return (
    <div style={{ maxWidth: "800px", margin: "0 auto", padding: "20px" }}>
      <h2>Quản lý đơn hàng</h2>
      <Form layout="inline" style={{ marginBottom: "20px" }}>
        <Form.Item label="Mã Tour">
          <Input
            placeholder="Nhập mã Tour"
            value={tourId}
            onChange={handleTourIdChange}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" onClick={handleFetchOrders} loading={loading}>
            Tải đơn hàng
          </Button>
        </Form.Item>
      </Form>

      <List
        dataSource={orderData}
        renderItem={(order) => (
          <Card style={{ marginBottom: "20px" }} bordered={false}>
            <List.Item>
              <div style={{ flex: 1 }}>
                <Title level={4}>
                  Khách hàng: {order.customerName} ({order.customerId})
                </Title>
                <Text strong>Mã Đơn Hàng:</Text> {order.orderId}
                <br />
                <Text strong>Mã Tour:</Text> {order.tourId} |{" "}
                <Text strong>Mã Trang Trại:</Text> {order.farmId}
                <br />
                <Text strong>Ngày đặt:</Text> {order.orderDate}
                <br />
                <Text strong>Ngày giao:</Text>{" "}
                {order.deliveredDate || "Chưa giao"}
                <br />
                <Text strong>Tổng tiền:</Text> {order.totalPrice}
                <br />
                <Text strong>Địa chỉ:</Text> {order.customerAddress}
                <br />
                <Text strong>Trạng thái:</Text> {order.status}
                <br />
                <Text strong>Ghi chú:</Text> {order.note}
                <Divider orientation="left">Chi tiết đơn hàng</Divider>
                <List
                  dataSource={order.orderDetails}
                  renderItem={(detail) => (
                    <List.Item>
                      <Card bordered={false} style={{ width: "100%" }}>
                        <Text>
                          <b>Mã Koi:</b> {detail.species}
                        </Text>
                        <br />
                        <Text>
                          <b>Mô tả:</b> {detail.description}
                        </Text>
                        <br />
                        <Text>
                          <b>Số lượng:</b> {detail.quantity}
                        </Text>
                        <br />
                        <Text>
                          <b>Giá:</b> {detail.price}
                        </Text>
                      </Card>
                    </List.Item>
                  )}
                />
                <Button
                  type="primary"
                  onClick={() => handleReceiveOrder(order.orderId)}
                  disabled={order.status === CHECKED_STATUS}
                  style={{ marginTop: "10px" }}
                >
                  {order.status === CHECKED_STATUS
                    ? "Đơn đã được nhận"
                    : "Nhận đơn"}
                </Button>
              </div>
            </List.Item>
          </Card>
        )}
      />
    </div>
  );
}

export default ReceivedOrder;
