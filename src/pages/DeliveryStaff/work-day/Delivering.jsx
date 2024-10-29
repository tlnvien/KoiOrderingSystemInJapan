import React from "react";
import { Button, message } from "antd";
import api from "../../../config/axios";

const Delivering = () => {
  const token = localStorage.getItem("token");
  const deliveringId = localStorage.getItem("deliveringId");

  // Function to start the workday
  const startWorkday = async () => {
    try {
      const response = await api.put(`delivering/start/${deliveringId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      message.success(`Ngày làm việc đã bắt đầu!`, response.data);
    } catch (error) {
      console.error("Lỗi khi bắt đầu ngày làm việc:", error);
      message.error("Không thể bắt đầu ngày làm việc.");
    }
  };

  // Function to end the workday
  const endWorkday = async () => {
    try {
      const response = await api.put(`delivering/end/${deliveringId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      message.success(
        "Cảm ơn bạn đã bán mình vì tư bản, những đơn hàng nếu bạn giao chưa xong sẽ được chuyển về kho",
        response.data
      );
    } catch (error) {
      console.error("Lỗi khi kết thúc ngày làm việc:", error);
      message.error("Không thể kết thúc ngày làm việc.");
    }
  };

  return (
    <div>
      <h2>Quản Lý Ngày Làm Việc</h2>
      <div style={{ marginTop: 16 }}>
        <Button
          type="primary"
          onClick={startWorkday}
          style={{ marginRight: 8 }}
        >
          Bắt Đầu Ngày Làm Việc
        </Button>
        <Button type="danger" onClick={endWorkday}>
          Kết Thúc Ngày Làm Việc
        </Button>
      </div>
    </div>
  );
};

export default Delivering;
