import { useState } from "react";
import { Form, Input, Button, Row, Col, message } from "antd";
import useGetParams from "../../../hooks/useGetParam";
import "./CreateOrder.css";
import api from "../../../config/axios";

function CreateOrder() {
  const params = useGetParams();
  const tourId = params("tourId");
  const customerId = params("customerId");
  const [description, setDescription] = useState();
  const [note, setNote] = useState();
  const [customerAddress, setcustomerAddress] = useState();
  const [farmId, setFarmId] = useState(); // State for farmId
  const [orderDetails, setOrderDetails] = useState([
    {
      koiId: undefined,
      description: undefined,
      quantity: undefined,
      price: undefined,
    },
  ]);

  // Handle order details change
  const handleOrderDetailsChange = (index, field, value) => {
    const newOrderDetails = [...orderDetails];
    newOrderDetails[index][field] = value;
    setOrderDetails(newOrderDetails);
  };

  // Add new order detail row
  const addOrderDetail = () => {
    setOrderDetails([
      ...orderDetails,
      {
        koiId: undefined,
        description: undefined,
        quantity: undefined,
        price: undefined,
      },
    ]);
  };

  // Submit order data to the server
  const handleSubmit = async () => {
    const orderData = { farmId, description, note, customerAddress, orderDetails }; // Include farmId here
    const token = localStorage.getItem("token");

    if (!token) {
      message.error("Thiếu token xác thực. Vui lòng đăng nhập.");
      return;
    }

    try {
      const response = await api.post(
        `order/${customerId}?tourId=${tourId}`,
        orderData,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      message.success("Đơn hàng đã được tạo thành công!");
      console.log("Đơn hàng đã được tạo:", response.data);
      localStorage.setItem("orderId", response.data.orderId);
    } catch (error) {
      console.error("Lỗi khi tạo đơn hàng:", error);
      message.error(error.response?.data);
    }
  };

  return (
    <div>
      <h2>Tạo Đơn Hàng</h2>
      <Form onFinish={handleSubmit} layout="vertical">
        <Form.Item label="Mã Khách Hàng" required>
          <Input
            value={customerId}
            placeholder="Nhập mã khách hàng"
            disabled
            required
          />
        </Form.Item>

        <Form.Item label="Mã Tour" required>
          <Input value={tourId} placeholder="Nhập mã tour" disabled required />
        </Form.Item>

        <Form.Item label="Mã Trang Trại" required>
          <Input
            value={farmId}
            placeholder="Nhập mã trang trại"
            onChange={(e) => setFarmId(e.target.value)} // Update farmId state
            required
          />
        </Form.Item>

        <Form.Item label="Mô Tả" required>
          <Input.TextArea
            value={description}
            placeholder="Nhập mô tả"
            onChange={(e) => setDescription(e.target.value)}
            required
            rows={4}
          />
        </Form.Item>

        <Form.Item label="Ghi Chú">
          <Input
            value={note}
            placeholder="Nhập ghi chú"
            onChange={(e) => setNote(e.target.value)}
          />
        </Form.Item>

        <Form.Item label="Địa chỉ">
          <Input
            value={customerAddress}
            placeholder="Nhập địa chỉ"
            onChange={(e) => setcustomerAddress(e.target.value)}
          />
        </Form.Item>

        <h3>Chi Tiết Đơn Hàng</h3>
        {orderDetails.map((detail, index) => (
          <Row
            key={index}
            gutter={16}
            style={{ marginBottom: "16px", borderBottom: "1px solid #f0f0f0" }}
          >
            <Col span={4}>
              <Form.Item label="Mã Koi">
                <Input
                  value={detail.koiId}
                  placeholder="Nhập mã Koi"
                  onChange={(e) =>
                    handleOrderDetailsChange(index, "koiId", e.target.value)
                  }
                />
              </Form.Item>
            </Col>

            <Col span={4}>
              <Form.Item label="Mô Tả">
                <Input
                  value={detail.description}
                  placeholder="Nhập mô tả"
                  onChange={(e) =>
                    handleOrderDetailsChange(
                      index,
                      "description",
                      e.target.value
                    )
                  }
                />
              </Form.Item>
            </Col>

            <Col span={4}>
              <Form.Item label="Số Lượng">
                <Input
                  type="number"
                  min={1}
                  placeholder="Nhập số lượng"
                  value={detail.quantity}
                  onChange={(e) =>
                    handleOrderDetailsChange(index, "quantity", e.target.value)
                  }
                />
              </Form.Item>
            </Col>

            <Col span={4}>
              <Form.Item label="Giá">
                <Input
                  type="number"
                  min={0}
                  placeholder="Nhập giá"
                  value={detail.price}
                  onChange={(e) =>
                    handleOrderDetailsChange(index, "price", e.target.value)
                  }
                />
              </Form.Item>
            </Col>
          </Row>
        ))}

        <Button
          type="dashed"
          onClick={addOrderDetail}
          style={{ marginTop: 16 }}
        >
          Thêm Chi Tiết Đơn Hàng
        </Button>

        <Button type="primary" htmlType="submit" style={{ marginTop: 16 }}>
          Tạo Đơn Hàng
        </Button>
      </Form>
    </div>
  );
}

export default CreateOrder;
