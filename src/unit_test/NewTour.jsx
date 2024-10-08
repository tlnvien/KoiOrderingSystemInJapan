import React, { useState, useEffect } from "react";
import { Button, Form, Input, Modal, Table, message } from "antd";
import axios from "axios";

const NewTour = () => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [tours, setTours] = useState([]);

  // Hàm để gọi API và lấy danh sách tour
  const fetchTours = async () => {
    try {
      const response = await axios.get(
        "https://66e79651b17821a9d9d95a2b.mockapi.io/Test_tour"
      );
      setTours(response.data);
    } catch (error) {
      message.error("Failed to fetch tours");
    }
  };

  // Gọi hàm fetchTours khi component được mount
  useEffect(() => {
    fetchTours();
  }, []);

  // Hàm để hiển thị modal
  const showModal = () => {
    setIsModalVisible(true);
  };

  // Hàm để đóng modal
  const handleCancel = () => {
    setIsModalVisible(false);
  };

  // Hàm để xử lý việc thêm tour mới
  const handleFinish = async (values) => {
    try {
      const newTour = {
        Tour_ID: `Tour_ID ${tours.length + 1}`,
        Tour_name: values.tourName,
        start_date: Math.floor(Date.now() / 1000), // Thay đổi để lấy timestamp hiện tại
        description: values.description,
        price: values.price,
      };
      const response = await axios.post(
        "https://66e79651b17821a9d9d95a2b.mockapi.io/Test_tour",
        newTour
      );
      setTours([...tours, response.data]);
      message.success("Tour added successfully");
      form.resetFields();
      setIsModalVisible(false);
    } catch (error) {
      message.error("Failed to add tour");
    }
  };

  const columns = [
    {
      title: "Tour ID",
      dataIndex: "Tour_ID",
      key: "Tour_ID",
    },
    {
      title: "Tour Name",
      dataIndex: "Tour_name",
      key: "Tour_name",
    },
    {
      title: "Start Date",
      dataIndex: "start_date",
      key: "start_date",
      render: (text) => new Date(text * 1000).toLocaleString(), // Định dạng timestamp
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
    },
    {
      title: "Price",
      dataIndex: "price",
      key: "price",
    },
  ];

  return (
    <div>
      <Button type="primary" onClick={showModal}>
        Add New Tour
      </Button>
      <Table dataSource={tours} columns={columns} rowKey="id" />

      <Modal
        title="Add New Tour"
        open={isModalVisible}
        onCancel={handleCancel}
        footer={null}
      >
        <Form form={form} onFinish={handleFinish}>
          <Form.Item
            label="Tour Name"
            name="tourName"
            rules={[{ required: true, message: "Please input tour name!" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Description"
            name="description"
            rules={[{ required: true, message: "Please input description!" }]}
          >
            <Input.TextArea />
          </Form.Item>
          <Form.Item
            label="Price"
            name="price"
            rules={[
              { required: true, message: "Please input price!" },
              { pattern: /^\d+$/, message: "Please input a valid price!" }, // Validate price must be a number
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Add Tour
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default NewTour;
