import { Table, Button, Empty, Row, Col, Card } from "antd";
import React, { useEffect, useState } from "react";
import { toast } from "react-toastify";
import api from "../../../config/axios";
import dayjs from "dayjs";

function RequestCustomer() {
  const [dataSource, setDataSource] = useState([]);
  const token = localStorage.getItem("token");

  const fetchData = async (values) => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }
      const response = await api.get("booking/requests", values, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log(response.data);
      // Ensure response.data is an array
      setDataSource(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      toast.error(error.response?.data || "Failed to fetch data.");
    }
  };

  const handleTakeRequest = async (bookingId) => {
    try {
      const response = await api.post(`booking/take/${bookingId}`, null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Nhận yêu cầu thành công!");
      fetchData(); // Refresh data after taking the request
    } catch (error) {
      toast.error(error.response?.data || "Không thể nhận yêu cầu.");
    }
  };

  useEffect(() => {
    fetchData(); // Tải dữ liệu lần đầu
  }, []);

  return (
    <div>
      <h1>Yêu cầu từ khách hàng</h1>
      {dataSource.length > 0 ? (
        <Row gutter={[16, 16]}>
          {dataSource.map((record) => (
            <Col span={24} key={record.bookingId}>
              <Card
                title={`Booking ID: ${record.bookingId}`}
                extra={<span>Trạng thái: {record.status}</span>}
                style={{ width: "100%" }}
              >
                <Row gutter={[16, 8]}>
                  <Col span={12}>
                    <p>
                      <strong>Họ và tên:</strong> {record.customerName}
                    </p>
                    <p>
                      <strong>Sđt:</strong> {record.phone}
                    </p>
                    <p>
                      <strong>Tour ID:</strong>{" "}
                      {record.tourId || "Chưa có tour"}
                    </p>
                    <p>
                      <strong>Trạng thái thanh toán:</strong>{" "}
                      {record.paymentId === "Booking chưa được thanh toán!"
                        ? "Chưa thanh toán"
                        : record.paymentId}
                    </p>
                  </Col>
                  <Col span={12}>
                    <p>
                      <strong>Yêu cầu về tour:</strong> {record.description}
                    </p>
                    <p>
                      <strong>Visa:</strong> {record.hasVisa ? "Có" : "Không"}
                    </p>
                    <p>
                      <strong>Số lượng người tham gia:</strong>{" "}
                      {record.numberOfAttendances}
                    </p>
                    <p>
                      <strong>Giá :</strong> {record.totalPrice}
                    </p>
                    <p>
                      <strong>Created Date:</strong>{" "}
                      {dayjs(record.createdDate, "DD-MM-YYYY HH:mm:ss").format(
                        "DD/MM/YYYY HH:mm:ss"
                      )}
                    </p>
                  </Col>
                </Row>
                {record.bookingDetailResponses &&
                  record.bookingDetailResponses.length > 0 && (
                    <div style={{ marginTop: "16px" }}>
                      <h3>Thông tin người tham gia:</h3>
                      {record.bookingDetailResponses.map((attendee, index) => (
                        <div
                          key={index}
                          style={{
                            padding: "8px 0",
                            borderBottom: "1px solid #f0f0f0",
                          }}
                        >
                          <p>
                            <strong>Họ và tên người tham gia:</strong>{" "}
                            {attendee.customerName}
                          </p>
                          <p>
                            <strong>Ngày sinh:</strong>{" "}
                            {dayjs(attendee.dob, "DD-MM-YYYY").format(
                              "DD/MM/YYYY"
                            )}
                          </p>
                          <p>
                            <strong>Giới tính:</strong>{" "}
                            {attendee.gender === "MALE" ? "Nam" : "Nữ"}
                          </p>
                          <p>
                            <strong>Sđt:</strong> {attendee.phone}
                          </p>
                        </div>
                      ))}
                    </div>
                  )}
                <div style={{ textAlign: "right", marginTop: "20px" }}>
                  <Button
                    type="primary"
                    onClick={() => handleTakeRequest(record.bookingId)}
                  >
                    Nhận yêu cầu
                  </Button>
                </div>
              </Card>
            </Col>
          ))}
        </Row>
      ) : (
        <Empty description="Request is empty!" />
      )}
    </div>
  );
}

export default RequestCustomer;
