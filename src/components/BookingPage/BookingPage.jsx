import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  Checkbox,
  Button,
  Divider,
  Row,
  Col,
  message,
  Typography,
  Card,
  DatePicker,
  Select,
} from "antd";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import "./BookingPage.css";
import api from "../../config/axios";
import dayjs from "dayjs";

const { Title } = Typography;

const BookingPage = () => {
  const [form] = Form.useForm();
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");
  const [numberOfAttendees, setNumberOfAttendees] = useState(1);
  const [bookingDetails, setBookingDetails] = useState([
    { customerName: "", phone: "", gender: "", dob: null },
  ]);

  const fetchData = async () => {
    try {
      const response = await api.get(`info/user/${userId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const userData = response.data;

      // Populate form with user data
      form.setFieldsValue({
        fullName: userData.fullName || "",
        phone: userData.phone || "",
        gender: userData.gender || "",
        dob: userData.dob ? dayjs(userData.dob, "DD-MM-YYYY") : null,
      });
    } catch (error) {
      message.error("Không thể lấy thông tin người dùng!");
    }
  };

  useEffect(() => {
    fetchData();
  }, [form, userId, token]);

  // Update the number of attendee fields dynamically
  useEffect(() => {
    setBookingDetails((prevDetails) => {
      const newDetails = [...prevDetails];

      if (numberOfAttendees > prevDetails.length) {
        for (let i = prevDetails.length + 1; i < numberOfAttendees; i++) {
          newDetails.push({
            customerName: "",
            phone: "",
            gender: "",
            dob: null,
          });
        }
      } else if (numberOfAttendees < prevDetails.length) {
        newDetails.length = numberOfAttendees; // Reduce the length to match
      }

      form.setFieldsValue({ bookingDetailRequests: newDetails });
      return newDetails;
    });
  }, [numberOfAttendees, form]);

  // Submit form data to the API
  const handleSubmit = async (values) => {
    const formattedDetails = (values.bookingDetailRequests || []).map(
      (detail) => ({
        ...detail,
        dob: detail.dob ? dayjs(detail.dob).format("DD-MM-YYYY") : null,
      })
    );

    values.dob = dayjs(values.dob).format("DD-MM-YYYY");

    const submissionData = {
      ...values,
      bookingDetailRequests: formattedDetails,
    };

    try {
      const response = await api.post("booking/request", submissionData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      message.success("Yêu cầu đặt tour thành công!");
      form.resetFields();
      fetchData();
      setNumberOfAttendees(1);
      setBookingDetails([
        { customerName: "", phone: "", gender: "", dob: null },
      ]);
      const { bookingId } = response.data;
      localStorage.setItem("bookingId", bookingId);
    } catch (error) {
      message.error(error.response?.data || "Có lỗi xảy ra, vui lòng thử lại!");
    }
  };

  return (
    <div className="booking-page">
      <Header />
      <Row justify="center" style={{ padding: "20px 0" }}>
        <Col xs={22} sm={18} md={16} lg={12} xl={10}>
          <Card bordered={false} className="booking-card">
            <Title
              level={3}
              style={{ textAlign: "center", marginBottom: "20px" }}
            >
              Đặt Tour
            </Title>
            <Divider orientation="left">Thông tin liên hệ</Divider>
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              initialValues={{
                fullName: "",
                phone: "",
                gender: "",
                dob: "",
                numberOfAttendees: 1,
                description: "",
                hasVisa: false,
                bookingDetailRequests: bookingDetails, // Initialize here
              }}
              className="booking-form"
            >
              <Form.Item
                label="Họ và tên"
                name="fullName"
                rules={[{ required: true, message: "Vui lòng nhập họ và tên" }]}
              >
                <Input placeholder="Nhập họ và tên" />
              </Form.Item>

              <Form.Item
                label="Số điện thoại"
                name="phone"
                rules={[
                  { required: true, message: "Vui lòng nhập số điện thoại" },
                  {
                    pattern: /^[0-9]{10}$/,
                    message: "Số điện thoại không hợp lệ",
                  },
                ]}
              >
                <Input type="tel" placeholder="Nhập số điện thoại" />
              </Form.Item>

              <Form.Item
                label="Ngày Sinh"
                name="dob"
                rules={[{ required: true, message: "Vui lòng nhập ngày sinh" }]}
              >
                <DatePicker format="DD-MM-YYYY" style={{ width: "100%" }} />
              </Form.Item>

              <Form.Item
                label="Số lượng người tham gia"
                name="numberOfAttendees"
              >
                <Input
                  type="number"
                  min={1}
                  value={numberOfAttendees}
                  onChange={(e) =>
                    setNumberOfAttendees(parseInt(e.target.value, 10) || 1)
                  }
                />
              </Form.Item>

              <Form.Item name="hasVisa" valuePropName="checked">
                <Checkbox>VISA</Checkbox>
              </Form.Item>

              <Form.Item label="Yêu cầu về tour" name="description">
                <Input.TextArea placeholder="Nhập mô tả nếu có" rows={4} />
              </Form.Item>

              {numberOfAttendees > 1 && (
                <>
                  <Divider orientation="left">Thông tin người tham gia</Divider>
                  {bookingDetails.map((attendee, index) => (
                    <div key={index}>
                      <Form.Item
                        label={`Tên người tham gia thứ ${index + 2}`}
                        name={["bookingDetailRequests", index, "customerName"]}
                        rules={[
                          { required: true, message: "Vui lòng nhập tên" },
                        ]}
                      >
                        <Input placeholder="Nhập họ và tên" />
                      </Form.Item>

                      <Form.Item
                        label="Ngày Sinh"
                        name={["bookingDetailRequests", index, "dob"]}
                        rules={[
                          {
                            required: true,
                            message: "Vui lòng nhập ngày sinh",
                          },
                        ]}
                      >
                        <DatePicker
                          format="DD-MM-YYYY"
                          style={{ width: "100%" }}
                        />
                      </Form.Item>

                      <Form.Item
                        label="Giới tính"
                        name={["bookingDetailRequests", index, "gender"]}
                        rules={[
                          {
                            required: true,
                            message: "Vui lòng chọn giới tính",
                          },
                        ]}
                      >
                        <Select placeholder="Chọn giới tính">
                          <Select.Option value="MALE">Nam</Select.Option>
                          <Select.Option value="FEMALE">Nữ</Select.Option>
                          <Select.Option value="OTHER">Khác</Select.Option>
                        </Select>
                      </Form.Item>

                      <Form.Item
                        label="Số điện thoại"
                        name={["bookingDetailRequests", index, "phone"]}
                        rules={[
                          {
                            required: true,
                            message: "Vui lòng nhập số điện thoại",
                          },
                          {
                            pattern: /^[0-9]{10}$/,
                            message: "Số điện thoại không hợp lệ",
                          },
                        ]}
                      >
                        <Input type="tel" placeholder="Nhập số điện thoại" />
                      </Form.Item>
                    </div>
                  ))}
                </>
              )}

              <Form.Item>
                <Button
                  type="primary"
                  htmlType="submit"
                  className="submit-button"
                  block
                >
                  Gửi Yêu Cầu
                </Button>
              </Form.Item>
            </Form>
          </Card>
        </Col>
      </Row>
      <Footer />
    </div>
  );
};

export default BookingPage;
