import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input } from "antd";
import axios from "axios";
import { Link } from "react-router-dom";
import "./Admin.css"; // Import CSS cho layout
import Sidebar from "./Admin.jsx";

const ReviewManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [nextId, setNextId] = useState(1); // Quản lý id tự động

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const response = await axios.get(
      "https://66f97d4dafc569e13a98ee5e.mockapi.io/Management"
    );
    const reviews = response.data.filter((item) => item.type === "review");

    const maxId = Math.max(...reviews.map((review) => parseInt(review.id)), 0);
    setNextId(maxId + 1); // Tăng nextId theo id lớn nhất

    setData(reviews);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = async (id) => {
    await axios.delete(
      `https://66f97d4dafc569e13a98ee5e.mockapi.io/Management/${id}`
    );
    fetchData();
  };

  const handleOk = async (values) => {
    const dataToSend = { ...values, type: "review" };

    if (editingRecord) {
      await axios.put(
        `https://66f97d4dafc569e13a98ee5e.mockapi.io/Management/${editingRecord.id}`,
        dataToSend
      );
    } else {
      dataToSend.id = nextId; // Gán id tự động
      setNextId(nextId + 1); // Tăng nextId sau khi thêm mới
      await axios.post(
        "https://66f97d4dafc569e13a98ee5e.mockapi.io/Management",
        dataToSend
      );
    }
    setIsModalVisible(false);
    fetchData();
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const columns = [
    { title: "ID", dataIndex: "id", key: "id" },
    { title: "Reviewer", dataIndex: "reviewer", key: "reviewer" },
    { title: "Comment", dataIndex: "comment", key: "comment" },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <>
          <Button onClick={() => handleEdit(record)}>Edit</Button>
          <Button onClick={() => handleDelete(record.id)} danger>
            Delete
          </Button>
        </>
      ),
    },
  ];

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h2>Quản lý đánh giá</h2>
        <Button type="primary" onClick={handleAdd}>
          Add Review
        </Button>
        <Table dataSource={data} columns={columns} rowKey="id" />

        <Modal
          title={editingRecord ? "Edit Review" : "Add Review"}
          visible={isModalVisible}
          onCancel={handleCancel}
          footer={null}
          key={editingRecord ? editingRecord.id : "add-review"}
        >
          <Form
            initialValues={editingRecord}
            onFinish={handleOk}
            layout="vertical"
          >
            <Form.Item
              name="reviewer"
              label="Reviewer"
              rules={[
                { required: true, message: "Please input the reviewer name!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="comment"
              label="Comment"
              rules={[{ required: true, message: "Please input the comment!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Submit
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default ReviewManagement;
