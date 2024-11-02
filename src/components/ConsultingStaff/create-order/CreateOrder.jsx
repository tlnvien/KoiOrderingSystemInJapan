import { useState, useEffect } from "react";
import { Form, Input, Button, Row, Col, message, Select } from "antd";
import useGetParams from "../../../hooks/useGetParam";
import "./CreateOrder.css";
import api from "../../../config/axios";

const { Option } = Select;

function CreateOrder() {
  const params = useGetParams();
  const tourId = params("tourId");
  const customerId = params("customerId");
  const [description, setDescription] = useState();
  const [note, setNote] = useState();
  const [customerAddress, setcustomerAddress] = useState();
  const [farmId, setFarmId] = useState();
  const [koiSpeciesList, setKoiSpeciesList] = useState([]); // State for Koi species list
  const [orderDetails, setOrderDetails] = useState([
    {
      species: undefined,
      description: undefined,
      quantity: undefined,
      price: undefined,
    },
  ]);

  // Function to format number with commas
  const formatNumber = (value) => {
    if (value === undefined || value === "") return "";
    const number = Number(value);
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ".");
  };

  // Function to fetch Koi species based on farmId
  const fetchKoiSpecies = async (farmId) => {
    try {
      const response = await api.get(`koiFarm/listKoi/${farmId}`);
      if (Array.isArray(response.data)) {
        setKoiSpeciesList(response.data); // Lưu trữ danh sách giống cá
      } else {
        console.error("Dữ liệu trả về không phải là mảng:", response.data);
        message.error("Không thể lấy danh sách giống cá.");
      }
    } catch (error) {
      console.error("Lỗi khi lấy danh sách giống cá:", error);
      message.error("Không thể lấy danh sách giống cá.");
    }
  };

  // Handle farmId change and fetch Koi species
  const handleFarmIdChange = (value) => {
    setFarmId(value);
    fetchKoiSpecies(value); // Call API when farmId changes
  };

  // Handle order details change
  const handleOrderDetailsChange = (index, field, value) => {
    const newOrderDetails = [...orderDetails];
    newOrderDetails[index][field] = value;
    setOrderDetails(newOrderDetails);
  };

  // Add new order detail row
  const addOrderDetail = () => {
    const newOrderDetail = {
      species: undefined,
      description: undefined,
      quantity: undefined,
      price: undefined,
    };
    const updatedOrderDetails = [...orderDetails];
    updatedOrderDetails.push(newOrderDetail);
    setOrderDetails(updatedOrderDetails);
  };

  // Submit order data to the server
  const handleSubmit = async () => {
    const orderData = {
      farmId,
      description,
      note,
      customerAddress,
      orderDetails,
    };
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
            onChange={(e) => handleFarmIdChange(e.target.value)} // Update farmId state and fetch species
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
              <Form.Item label="Giống Cá">
                <Select
                  placeholder="Chọn giống cá"
                  value={detail.species}
                  onChange={(value) =>
                    handleOrderDetailsChange(index, "species", value)
                  }
                >
                  {/* Kiểm tra nếu koiSpeciesList là một mảng trước khi map */}
                  {Array.isArray(koiSpeciesList) &&
                    koiSpeciesList.map((species) => (
                      <Option key={species.species} value={species.species}>
                        {species.species}
                      </Option>
                    ))}
                </Select>
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
                  type="text"
                  placeholder="Nhập giá"
                  value={formatNumber(detail.price)}
                  onChange={(e) => {
                    const value = e.target.value.replace(/\./g, ""); // Remove dots
                    handleOrderDetailsChange(index, "price", value);
                  }}
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
          Thêm giống cá vào đơn hàng
        </Button>

        <Button type="primary" htmlType="submit" style={{ marginTop: 16 }}>
          Tạo Đơn Hàng
        </Button>
      </Form>
    </div>
  );
}

export default CreateOrder;
