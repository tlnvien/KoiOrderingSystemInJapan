import React, { useEffect, useState } from "react";
import { Card, Button, Row, Col, message } from "antd";
import api from "../../../config/axios";

const Checkin = () => {
  const [bookings, setBookings] = useState([]);
  const token = localStorage.getItem("token");
  const CHECKED_STATUS = "CHECKED";

  // Fetch booking data on component mount
  const fetchBookings = async () => {
    try {
      const response = await api.get("booking/consulting", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setBookings(response.data);
    } catch (error) {
      console.error("Error fetching booking data:", error);
      message.error("Could not load booking data.");
    }
  };

  // Function to handle the "Checked" status update
  const handleCheckin = async (bookingId) => {
    try {
      await api.post(
        `booking/check/${bookingId}?status=${CHECKED_STATUS}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      message.success("Booking status updated to CHECKED");
      // Refresh the list after checking in
      fetchBookings();
    } catch (error) {
      console.error("Error updating booking status:", error);
      message.error("Failed to update booking status.");
    }
  };

  // Load bookings when the component mounts
  useEffect(() => {
    fetchBookings();
  }, []);

  return (
    <div>
      <h2>Booking Check-in</h2>
      {bookings.length > 0 ? (
        bookings.map((booking) => (
          <Card key={booking.bookingId} style={{ marginBottom: 16 }}>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <strong>Customer Name:</strong> {booking.customerName}
              </Col>
              <Col span={12}>
                <strong>Phone:</strong> {booking.phone}
              </Col>
              <Col span={12}>
                <strong>Tour ID:</strong> {booking.tourId}
              </Col>
              <Col span={12}>
                <strong>Payment ID:</strong> {booking.paymentId}
              </Col>
              <Col span={12}>
                <strong>Created Date:</strong> {booking.createdDate}
              </Col>
              <Col span={12}>
                <strong>Status:</strong> {booking.status}
              </Col>
              <Col span={12}>
                <strong>Total Price:</strong> {booking.totalPrice} VND
              </Col>
              <Col span={12}>
                <strong>Visa Required:</strong> {booking.hasVisa ? "Yes" : "No"}
              </Col>
            </Row>
            <Button
              type="primary"
              onClick={() => handleCheckin(booking.bookingId)}
              disabled={booking.status === CHECKED_STATUS}
              style={{ marginTop: 16 }}
            >
              {booking.status === CHECKED_STATUS
                ? "Already Checked"
                : "Check-in"}
            </Button>
          </Card>
        ))
      ) : (
        <p>No bookings available for check-in.</p>
      )}
    </div>
  );
};

export default Checkin;
