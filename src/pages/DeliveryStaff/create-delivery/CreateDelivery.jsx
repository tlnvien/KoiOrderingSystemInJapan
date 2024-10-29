import React, { useState } from "react";
import { Form, Input, Button, message, Card, Typography, Divider } from "antd";
import api from "../../../config/axios";
import "./CreateDelivery.css"; 

const { Title, Text } = Typography;

const CreateDelivery = () => {
  const [information, setInformation] = useState("");
  const [responseData, setResponseData] = useState(null);

  const handleCreateDelivery = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      message.error("Token không tồn tại trong localStorage.");
      return;
    }

    try {
      const response = await api.post(
        "delivering",
        { information },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const { deliveringId } = response.data;
      localStorage.setItem("deliveringId", deliveringId);
      setResponseData(response.data);

      message.success("Tạo đơn giao hàng thành công!");
    } catch (error) {
      console.error("Lỗi khi tạo đơn giao hàng:", error);
      message.error("Không thể tạo đơn giao hàng.");
    }
  };

  return (
    <div className="create-delivery-container">
      <Title level={2} className="create-delivery-title">
        Tạo Đơn Giao Hàng
      </Title>

      <Card bordered className="create-delivery-form-card">
        <Form layout="vertical" onFinish={handleCreateDelivery}>
          <Form.Item label="Thông Tin" required>
            <Input.TextArea
              value={information}
              onChange={(e) => setInformation(e.target.value)}
              placeholder="Nhập thông tin giao hàng"
              rows={4}
              className="create-delivery-input"
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" className="create-delivery-submit-btn">
              Tạo Đơn Hàng
            </Button>
          </Form.Item>
        </Form>
      </Card>

      {responseData && (
        <Card title="Thông Tin Đơn Giao Hàng" bordered className="delivery-info-card">
          <div>
            <Text className="delivery-info-label">Mã Giao Hàng:</Text> 
            <Text>{responseData.deliveringId}</Text>
          </div>
          <Divider className="delivery-info-divider" />
          <div>
            <Text className="delivery-info-label">Mã Nhân Viên:</Text> 
            <Text>{responseData.deliveringStaffId}</Text>
          </div>
          <Divider className="delivery-info-divider" />
          <div>
            <Text className="delivery-info-label">Ngày Giao:</Text> 
            <Text>{responseData.deliverDate}</Text>
          </div>
          <Divider className="delivery-info-divider" />
          <div>
            <Text className="delivery-info-label">Thông Tin:</Text> 
            <Text>{responseData.information}</Text>
          </div>
          <Divider className="delivery-info-divider" />
          <div>
            <Text className="delivery-info-label">Trạng Thái:</Text> 
            <Text>{responseData.status}</Text>
          </div>
        </Card>
      )}
    </div>
  );
};

export default CreateDelivery;
