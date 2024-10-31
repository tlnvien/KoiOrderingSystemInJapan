import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  Checkbox,
  Button,
  Row,
  Col,
  message,
  Typography,
  Card,
  Select,
  DatePicker,
} from "antd";
import Footer from "../Footer/Footer";
import Header from "../Header/Header";
import "./BookingPageAvailable.css";
import useGetParams from "../../hooks/useGetParam";
import api from "../../config/axios";
import { useForm } from "antd/es/form/Form";
import dayjs from "dayjs";

const { Title } = Typography;

const BookingPageAvailable = () => {
  const [form] = useForm();
  const tourId = useGetParams()("tourId");
  const token = localStorage.getItem("token");
  const [numberOfAttendances, setNumberOfAttendances] = useState(1);
  const [customers, setCustomers] = useState([
    { fullName: "", phone: "", gender: "", dob: null },
  ]);

  useEffect(() => {
    setCustomers((prevCustomers) => {
      const newCustomers = [...prevCustomers];

      if (numberOfAttendances > prevCustomers.length) {
        for (let i = prevCustomers.length; i < numberOfAttendances; i++) {
          newCustomers.push({ fullName: "", phone: "", gender: "", dob: null });
        }
      } else if (numberOfAttendances < prevCustomers.length) {
        newCustomers.length = numberOfAttendances;
      }

      form.setFieldsValue({ customers: newCustomers });
      return newCustomers;
    });
  }, [numberOfAttendances, form]);

  const handleSubmit = async (values) => {
    const formattedCustomers = values.customers.map((customer) => ({
      ...customer,
      dob: customer.dob ? dayjs(customer.dob).format("DD-MM-YYYY") : null,
    }));

    const submissionData = {
      ...values,
      customers: formattedCustomers,
    };

    try {
      const response = await api.post(
        "booking/available/payment",
        submissionData,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      window.location.href = response.data;
    } catch (error) {
      message.error(error.response?.data);
    }
  };

  return (
    <div className="booking-page-available">
      <Header />
      <Row justify="center" style={{ padding: "20px 0" }}>
        <Col xs={22} sm={18} md={14} lg={12} xl={10}>
          <Card bordered={false} className="booking-card">
            <Title
              level={3}
              style={{ textAlign: "center", marginBottom: "20px" }}
            >
              Thanh toán đơn đặt tour
            </Title>
            <Form
              form={form}
              layout="vertical"
              onFinish={handleSubmit}
              initialValues={{ tourID: tourId, hasVisa: false }}
            >
              <Form.Item label="Mã Tour" name="tourID">
                <Input readOnly />
              </Form.Item>

              <Form.Item
                label="Số Lượng Người Tham Gia"
                name="numberOfAttendances"
              >
                <Input
                  type="number"
                  min={1}
                  value={numberOfAttendances}
                  onChange={(e) =>
                    setNumberOfAttendances(parseInt(e.target.value, 10) || 1)
                  }
                />
              </Form.Item>

              <Form.Item name="hasVisa" valuePropName="checked">
                <Checkbox>VISA</Checkbox>
              </Form.Item>

              {customers.map((customer, index) => (
                <div key={index}>
                  <h4>Thông tin Khách Hàng {index + 1}</h4>
                  <Form.Item
                    label="Họ và Tên Khách Hàng"
                    name={["customers", index, "fullName"]}
                    rules={[
                      {
                        required: true,
                        message: "Vui lòng nhập họ và tên khách hàng",
                      },
                    ]}
                  >
                    <Input
                      placeholder="Nhập họ và tên khách hàng"
                      onChange={(e) => {
                        const updatedCustomers = [...customers];
                        updatedCustomers[index].fullName = e.target.value;
                        setCustomers(updatedCustomers);
                        form.setFieldsValue({ customers: updatedCustomers });
                      }}
                    />
                  </Form.Item>
                  <Form.Item
                    label="Số Điện Thoại Khách Hàng"
                    name={["customers", index, "phone"]}
                    rules={[
                      {
                        required: true,
                        message: "Vui lòng nhập số điện thoại của khách hàng",
                      },
                      {
                        pattern: /^[0-9]{10}$/,
                        message: "Vui lòng nhập số điện thoại hợp lệ",
                      },
                    ]}
                  >
                    <Input
                      placeholder="Nhập số điện thoại của khách hàng"
                      onChange={(e) => {
                        const updatedCustomers = [...customers];
                        updatedCustomers[index].phone = e.target.value;
                        setCustomers(updatedCustomers);
                        form.setFieldsValue({ customers: updatedCustomers });
                      }}
                    />
                  </Form.Item>
                  <Form.Item
                    label="Giới Tính"
                    name={["customers", index, "gender"]}
                  >
                    <Select
                      onChange={(value) => {
                        const updatedCustomers = [...customers];
                        updatedCustomers[index].gender = value;
                        setCustomers(updatedCustomers);
                        form.setFieldsValue({ customers: updatedCustomers });
                      }}
                    >
                      <Select.Option value="MALE">Nam</Select.Option>
                      <Select.Option value="FEMALE">Nữ</Select.Option>
                      <Select.Option value="OTHER">Khác</Select.Option>
                    </Select>
                  </Form.Item>
                  <Form.Item
                    label="Ngày Sinh"
                    name={["customers", index, "dob"]}
                  >
                    <DatePicker
                      style={{ width: "100%" }}
                      format="DD-MM-YYYY" // Hiển thị định dạng này trên DatePicker
                    />
                  </Form.Item>
                </div>
              ))}

              <Form.Item>
                <Button type="primary" htmlType="submit" block>
                  Tiến Hành Thanh Toán
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

export default BookingPageAvailable;
