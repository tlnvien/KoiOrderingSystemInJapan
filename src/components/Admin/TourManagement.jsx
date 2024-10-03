import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, DatePicker } from "antd"; // Import DatePicker từ Ant Design
import axios from "axios";
import Sidebar from "./Admin.jsx"; // Đảm bảo Sidebar đã được định nghĩa đúng
import moment from "moment"; // Import moment để xử lý ngày giờ

const TourManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [nextId, setNextId] = useState(1); // Biến để quản lý id tự động
  const [searchText, setSearchText] = useState("");

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const response = await axios.get(
      "https://66f97d4dafc569e13a98ee5e.mockapi.io/Management"
    );
    const tours = response.data.filter((item) => item.type === "tour");

    // Tìm id lớn nhất trong dữ liệu hiện tại
    const maxId = Math.max(...tours.map((tour) => parseInt(tour.id)), 0);
    setNextId(maxId + 1); // Cập nhật id tiếp theo dựa trên id lớn nhất

    setData(tours);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = (id) => {
    Modal.confirm({
      title: "Are you sure you want to delete this tour?",
      okText: "Yes",
      okType: "danger",
      cancelText: "No",
      onOk: async () => {
        await axios.delete(
          `https://66f97d4dafc569e13a98ee5e.mockapi.io/Management/${id}`
        );
        fetchData();
      },
    });
  };

  const handleOk = async (values) => {
    const dataToSend = {
      ...values,
      type: "tour",
      startDate: values.startDate.format("YYYY-MM-DD"), // Định dạng ngày trước khi gửi
    };

    // Nếu đang chỉnh sửa thì giữ nguyên id, nếu thêm mới thì dùng nextId
    if (editingRecord) {
      await axios.put(
        `https://66f97d4dafc569e13a98ee5e.mockapi.io/Management/${editingRecord.id}`,
        dataToSend
      );
    } else {
      dataToSend.id = nextId; // Thêm id tự động vào khi tạo mới
      setNextId(nextId + 1); // Tăng id cho lần tiếp theo
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

  const handleSearch = (value) => {
    setSearchText(value);
  };

  // Lọc dữ liệu dựa trên từ khóa tìm kiếm
  const filteredData = data.filter((record) => {
    return (
      record.name.toLowerCase().includes(searchText.toLowerCase()) ||
      record.location.toLowerCase().includes(searchText.toLowerCase())
    );
  });

  const columns = [
    { title: "ID", dataIndex: "id", key: "id" },
    {
      title: "Tour Name",
      dataIndex: "name",
      key: "name",
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: "Price",
      dataIndex: "price",
      key: "price",
      sorter: (a, b) => a.price - b.price,
    },
    {
      title: "Location",
      dataIndex: "location",
      key: "location",
      sorter: (a, b) => a.location.localeCompare(b.location),
    },
    {
      title: "Seats",
      dataIndex: "seats",
      key: "seats",
      sorter: (a, b) => a.seats - b.seats,
    },
    {
      title: "Start Date", // Cột mới cho startDate
      dataIndex: "startDate",
      key: "startDate",
      sorter: (a, b) => new Date(a.startDate) - new Date(b.startDate),
      render: (text) => moment(text).format("YYYY-MM-DD"), // Hiển thị ngày đúng định dạng
    },
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
        <h2>Quản lý tour</h2>

        {/* Thêm ô tìm kiếm */}
        <Input.Search
          placeholder="Tìm kiếm tour theo tên hoặc địa điểm"
          onSearch={handleSearch}
          style={{ marginBottom: 16, width: 300 }}
          allowClear // Cho phép xóa tìm kiếm
        />

        <Button type="primary" onClick={handleAdd}>
          Add Tour
        </Button>
        <Table
          dataSource={filteredData}
          columns={columns}
          rowKey="id"
          pagination={{ pageSize: 5 }} // Giới hạn số lượng bản ghi trên mỗi trang
        />

        <Modal
          title={editingRecord ? "Edit Tour" : "Add Tour"}
          visible={isModalVisible}
          onCancel={handleCancel}
          footer={null}
          key={editingRecord ? editingRecord.id : "add-tour"}
        >
          <Form
            initialValues={
              editingRecord
                ? {
                    ...editingRecord,
                    startDate: moment(editingRecord.startDate),
                  }
                : {}
            }
            onFinish={handleOk}
            layout="vertical"
          >
            <Form.Item
              name="name"
              label="Tour Name"
              rules={[
                { required: true, message: "Please input the tour name!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="price"
              label="Price"
              rules={[{ required: true, message: "Please input the price!" }]}
            >
              <Input type="number" />
            </Form.Item>
            <Form.Item
              name="description"
              label="Description"
              rules={[
                { required: true, message: "Please input the description!" },
              ]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item
              name="location"
              label="Location"
              rules={[
                { required: true, message: "Please input the location!" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="seats"
              label="Seats"
              rules={[
                {
                  required: true,
                  message: "Please input the number of seats!",
                },
              ]}
            >
              <Input type="number" />
            </Form.Item>
            <Form.Item
              name="startDate"
              label="Start Date"
              rules={[
                { required: true, message: "Please select the start date!" },
              ]}
            >
              <DatePicker format="YYYY-MM-DD" />
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

export default TourManagement;
