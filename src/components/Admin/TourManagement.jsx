import React, { useEffect, useState } from "react";
import { Table, Button, Modal, Form, Input, DatePicker, Select } from "antd";
import axios from "axios";
import Sidebar from "./Admin.jsx";
import moment from "moment";

const TourManagement = () => {
  const [data, setData] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [searchText, setSearchText] = useState("");

  const apiUrl = "http://localhost:8081/api/tour";
  const token = localStorage.getItem("token");

  // Fetch tour data when component mounts
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await axios.get(apiUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "*/*",
        },
      });
      console.log("Response:", response.data);
      setData(response.data);
    } catch (error) {
      console.error("Error fetching tour data:", error);
      alert("Có lỗi xảy ra khi lấy dữ liệu tour.");
    }
  };

  const handleAdd = () => {
    setEditingRecord(null);
    setIsModalVisible(true);
  };

  const handleEdit = (record) => {
    setEditingRecord(record);
    setIsModalVisible(true);
  };

  const handleDelete = (tourID) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa tour này không?",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${tourID}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          fetchData();
        } catch (error) {
          console.error(
            "Error deleting tour:",
            error.response || error.message
          );
          alert("Có lỗi xảy ra khi xóa tour.");
        }
      },
    });
  };

  const handleOk = async (values) => {
    const dataToSend = {
      ...values,
      departureDate: values.departureDate.format("YYYY-MM-DDTHH:mm:ss"),
      endDate: values.endDate.format("YYYY-MM-DDTHH:mm:ss"),
    };

    console.log("Data to send:", dataToSend); // Log data before sending

    try {
      if (editingRecord) {
        // Edit an existing record
        await axios.put(`${apiUrl}/${editingRecord.tourID}`, dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        // Create a new record
        await axios.post(apiUrl, dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }
      setIsModalVisible(false);
      fetchData(); // Refresh data after operation
    } catch (error) {
      // Log error details
      console.error(
        "Error saving tour data:",
        error.response?.data || error.message
      );
      alert(
        "Có lỗi xảy ra khi lưu dữ liệu tour. Chi tiết: " +
          (error.response?.data.message || error.message)
      );
    }
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const handleSearch = (value) => {
    setSearchText(value);
  };

  const filteredData = data.filter((record) =>
    record.tourName.toLowerCase().includes(searchText.toLowerCase())
  );

  const columns = [
    { title: "Tour ID", dataIndex: "tourID", key: "tourID" },
    {
      title: "Tour Name",
      dataIndex: "tourName",
      key: "tourName",
      sorter: (a, b) => a.tourName.localeCompare(b.tourName),
    },
    {
      title: "Max Participants",
      dataIndex: "maxParticipants",
      key: "maxParticipants",
      sorter: (a, b) => a.maxParticipants - b.maxParticipants,
    },
    {
      title: "Remaining Seats",
      dataIndex: "remainSeat",
      key: "remainSeat",
      sorter: (a, b) => a.remainSeat - b.remainSeat,
    },
    {
      title: "Departure Date",
      dataIndex: "departureDate",
      key: "departureDate",
      sorter: (a, b) => new Date(a.departureDate) - new Date(b.departureDate),
      render: (text) => moment(text).format("YYYY-MM-DDTHH:mm:ss"),
    },
    {
      title: "End Date",
      dataIndex: "endDate",
      key: "endDate",
      sorter: (a, b) => new Date(a.endDate) - new Date(b.endDate),
      render: (text) => moment(text).format("YYYY-MM-DDTHH:mm:ss"),
    },
    {
      title: "Description",
      dataIndex: "description",
      key: "description",
    },
    {
      title: "Consulting ID",
      dataIndex: "consulting",
      key: "consulting",
    },
    {
      title: "Tour Type",
      dataIndex: "type",
      key: "type",
      filters: [
        { text: "Available Tour", value: "AVAILABLE_TOUR" },
        { text: "Unavailable Tour", value: "UNAVAILABLE_TOUR" },
      ],
      onFilter: (value, record) => record.type.includes(value),
    },
    {
      title: "Price",
      dataIndex: "price",
      key: "price",
      sorter: (a, b) => a.price - b.price,
    },
    {
      title: "Manager ID",
      dataIndex: "manager",
      key: "manager",
    },
    {
      title: "Action",
      key: "action",
      render: (_, record) => (
        <>
          <Button onClick={() => handleEdit(record)}>Edit</Button>
          <Button onClick={() => handleDelete(record.tourID)} danger>
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

        <Input.Search
          placeholder="Tìm kiếm tour theo tên"
          onSearch={handleSearch}
          style={{ marginBottom: 16, width: 300 }}
          allowClear
        />

        <Button type="primary" onClick={handleAdd} style={{ marginBottom: 16 }}>
          Thêm Tour
        </Button>

        <Table
          dataSource={filteredData}
          columns={columns}
          rowKey="tourID"
          pagination={{ pageSize: 5 }}
        />

        <Modal
          title={editingRecord ? "Chỉnh sửa Tour" : "Thêm Tour"}
          visible={isModalVisible}
          onCancel={handleCancel}
          footer={null}
          key={editingRecord ? editingRecord.tourID : "new"}
        >
          <Form
            initialValues={
              editingRecord
                ? {
                    ...editingRecord,
                    departureDate: moment(editingRecord.departureDate),
                    endDate: moment(editingRecord.endDate),
                  }
                : {}
            }
            onFinish={handleOk}
            layout="vertical"
          >
            <Form.Item
              name="tourID"
              label="Tour ID"
              rules={[{ required: true, message: "Vui lòng nhập Tour ID!" }]}
            >
              <Input disabled={!!editingRecord} />
            </Form.Item>
            <Form.Item
              name="tourName"
              label="Tour Name"
              rules={[{ required: true, message: "Vui lòng nhập tên tour!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="maxParticipants"
              label="Max Participants"
              rules={[
                { required: true, message: "Vui lòng nhập số lượng tối đa!" },
              ]}
            >
              <Input type="number" />
            </Form.Item>
            <Form.Item
              name="remainSeat"
              label="Remaining Seats"
              rules={[
                { required: true, message: "Vui lòng nhập số chỗ còn lại!" },
              ]}
            >
              <Input type="number" />
            </Form.Item>
            <Form.Item
              name="departureDate"
              label="Departure Date"
              rules={[
                { required: true, message: "Vui lòng chọn ngày khởi hành!" },
              ]}
            >
              <DatePicker showTime format="YYYY-MM-DDTHH:mm:ss" />
            </Form.Item>
            <Form.Item
              name="endDate"
              label="End Date"
              rules={[
                { required: true, message: "Vui lòng chọn ngày kết thúc!" },
              ]}
            >
              <DatePicker showTime format="YYYY-MM-DDTHH:mm:ss" />
            </Form.Item>
            <Form.Item name="description" label="Description">
              <Input.TextArea />
            </Form.Item>
            <Form.Item name="consulting" label="Consulting ID">
              <Input />
            </Form.Item>
            <Form.Item
              name="type"
              label="Tour Type"
              rules={[{ required: true, message: "Vui lòng chọn loại tour!" }]}
            >
              <Select>
                <Select.Option value="AVAILABLE_TOUR">
                  Available Tour
                </Select.Option>
                <Select.Option value="UNAVAILABLE_TOUR">
                  Unavailable Tour
                </Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              name="price"
              label="Price"
              rules={[{ required: true, message: "Vui lòng nhập giá!" }]}
            >
              <Input type="number" />
            </Form.Item>
            <Form.Item name="manager" label="Manager ID">
              <Input />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                {editingRecord ? "Cập nhật" : "Tạo mới"}
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default TourManagement;
