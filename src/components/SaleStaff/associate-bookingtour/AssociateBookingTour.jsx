import React, { useEffect, useState } from "react";
import {
  Form,
  Input,
  Button,
  message,
  Modal,
  List,
  Typography,
  Descriptions,
  Card,
  Col,
  Row,
} from "antd";
import api from "../../../config/axios";
import { useForm } from "antd/es/form/Form";

const AssociateBookingTour = () => {
  const [loading, setLoading] = useState(false);
  const [form] = useForm(); // Form instance from Ant Design
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");
  const [bookings, setBookings] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentBookingId, setCurrentBookingId] = useState(null);

  const fetchData = async () => {
    try {
      const response = await api.get(`booking/list/${userId}`);
      setBookings(response.data);
    } catch (error) {
      message.error(error.response?.data);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // Function handle submit form
  const handleSubmit = async (values) => {
    setLoading(true);

    try {
      // Send request with bookingId and tourId from form values
      const response = await api.post(
        `booking/associate?bookingId=${values.bookingId}&tourId=${values.tourId}`,
        {}, // body can be empty
        {
          headers: {
            Accept: "*/*",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Liên kết thành công: " + response.data.message);
      fetchData();
    } catch (error) {
      const errorMessage =
        error.response?.data?.message || "Đã có lỗi xảy ra. Vui lòng thử lại.";
      message.error(errorMessage);
    } finally {
      setLoading(false);
      setIsModalVisible(false);
    }
  };

  const openLinkModal = (bookingId) => {
    setCurrentBookingId(bookingId);
    form.resetFields();
    setIsModalVisible(true);
  };

  return (
    <div>
      <h2>Danh sách Đơn Hàng</h2>
      <List
        dataSource={bookings}
        renderItem={(booking) => (
          <List.Item
            style={{
              border: "1px solid grey",
              borderRadius: "10px",
              marginBottom: "16px",
            }}
          >
            <Card style={{ width: "100%" }}>
              <Typography.Title level={4}>
                Tên người yêu cầu: {booking.customerName}
              </Typography.Title>
              <Typography.Text type="secondary">
                Trạng thái booking: {booking.status}
              </Typography.Text>
              <Row gutter={[16, 16]} align="top">
                {/* Left Column: Basic Information */}
                <Col span={12}>
                  <Descriptions
                    column={1}
                    size="small"
                    bordered
                    style={{ marginTop: "8px" }}
                  >
                    <Descriptions.Item label="Mã Đơn Hàng">
                      {booking.bookingId}
                    </Descriptions.Item>
                    <Descriptions.Item label="Số điện thoại">
                      {booking.phone}
                    </Descriptions.Item>
                    <Descriptions.Item label="Ngày tạo">
                      {booking.createdDate}
                    </Descriptions.Item>
                    <Descriptions.Item label="Visa">
                      {booking.hasVisa ? "Có" : "Không"}
                    </Descriptions.Item>
                  </Descriptions>
                </Col>

                {/* Right Column: Additional Details */}
                <Col span={12}>
                  <Descriptions column={1} size="small" bordered>
                    <Descriptions.Item label="Trạng thái thanh toán">
                      {booking.paymentId}
                    </Descriptions.Item>
                    <Descriptions.Item label="Giá tổng">
                      {booking.totalPrice}
                    </Descriptions.Item>

                    <Descriptions.Item label="Số người tham gia">
                      {booking.numberOfAttendances}
                    </Descriptions.Item>
                    <Descriptions.Item label="Ghi chú">
                      {booking.description}
                    </Descriptions.Item>
                  </Descriptions>
                </Col>
              </Row>

              {/* Attendee Details Section - Below the two columns */}
              {booking.bookingDetailResponses.length > 0 && (
                <div style={{ marginTop: "16px" }}>
                  <Typography.Title level={5}>
                    Chi tiết Người Tham Gia
                  </Typography.Title>
                  <List
                    size="small"
                    bordered
                    dataSource={booking.bookingDetailResponses}
                    renderItem={(detail) => (
                      <List.Item>
                        {detail.customerName} - {detail.gender} - {detail.dob} -{" "}
                        {detail.phone}
                      </List.Item>
                    )}
                  />
                </div>
              )}

              {/* Link Button */}
              <div
                style={{
                  display: "flex",
                  justifyContent: "flex-end",
                  marginTop: "16px",
                }}
              >
                <Button
                  type="primary"
                  onClick={() => openLinkModal(booking.bookingId)}
                >
                  Liên Kết Tour
                </Button>
              </div>
            </Card>
          </List.Item>
        )}
      />

      {/* Modal for linking tour */}
      <Modal
        title="Liên kết Mã Tour"
        visible={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item
            label="Mã Tour"
            name="tourId"
            rules={[{ required: true, message: "Vui lòng nhập mã tour" }]}
          >
            <Input placeholder="Nhập mã tour" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              Xác nhận liên kết
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AssociateBookingTour;
