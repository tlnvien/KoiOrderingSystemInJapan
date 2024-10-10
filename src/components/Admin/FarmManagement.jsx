import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification } from "antd";
import Sidebar from "./Admin.jsx";

const FarmManagement = () => {
  const [farmList, setFarmList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentFarm, setCurrentFarm] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8080/api/farm"; // URL API cho trang trại
  const getApi = "http://localhost:8080/api/farm/list"; // URL API để lấy danh sách trang trại
  const token = localStorage.getItem("token");

  // Fetch farm data on component load
  useEffect(() => {
    fetchFarmList();
  }, []);

  const fetchFarmList = async () => {
    try {
      const response = await axios.get(getApi, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setFarmList(response.data); // Giả định API trả về danh sách trang trại với tất cả thông tin
    } catch (error) {
      notification.error({ message: "Failed to fetch farm list" });
    }
  };

  const handleEdit = (farm) => {
    setCurrentFarm(farm);
    setIsModalVisible(true);
    form.setFieldsValue(farm);
  };

  const handleDelete = (farmID) => {
    Modal.confirm({
      title: "Are you sure you want to delete this Farm?",
      okText: "Yes",
      okType: "danger",
      cancelText: "No",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${farmID}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          fetchFarmList();
          notification.success({ message: "Farm deleted successfully" });
        } catch (error) {
          notification.error({ message: "Failed to delete farm" });
        }
      },
    });
  };

  const handleSubmit = async (values) => {
    try {
      const { imageLink, ...farmData } = values; // Lấy hình ảnh và các thông tin khác
      const imagesArray = imageLink.split(",").map((link) => link.trim()); // Tách đường dẫn nếu có nhiều

      const farmDataWithImages = {
        ...farmData,
        imageLinks: imagesArray, // Thay thế bằng mảng hình ảnh
      };

      if (currentFarm) {
        await axios.put(`${apiUrl}/${currentFarm.farmID}`, farmDataWithImages, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
      } else {
        await axios.post(apiUrl, farmDataWithImages, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
      }

      fetchFarmList(); // Cập nhật lại danh sách
      setIsModalVisible(false);
      form.resetFields();
      notification.success({ message: "Farm saved successfully" });
    } catch (error) {
      notification.error({ message: "Failed to save farm" });
    }
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h1>Farm Management</h1>
        <Button
          type="primary"
          onClick={() => {
            setIsModalVisible(true);
            setCurrentFarm(null);
            form.resetFields(); // Reset fields for new entry
          }}
        >
          Add Farm
        </Button>
        <Table
          dataSource={farmList}
          rowKey="farmID"
          pagination={{ pageSize: 5 }}
          columns={[
            { title: "ID", dataIndex: "farmID" },
            { title: "Farm Name", dataIndex: "farmName" },
            { title: "Description", dataIndex: "description" },
            {
              title: "Image Links",
              dataIndex: "imageLink",
              render: (imageLinks) => (
                <div>
                  {imageLinks && imageLinks.length > 0
                    ? imageLinks.map((link, index) => (
                        <img
                          key={index}
                          src={link}
                          alt={`Farm ${index + 1}`}
                          style={{
                            width: "50px",
                            height: "auto",
                            marginRight: "5px",
                          }}
                        />
                      ))
                    : "No Images"}
                </div>
              ),
            },
            {
              title: "Actions",
              render: (text, farm) => (
                <>
                  <Button onClick={() => handleEdit(farm)}>Edit</Button>
                  <Button danger onClick={() => handleDelete(farm.farmID)}>
                    Delete
                  </Button>
                </>
              ),
            },
          ]}
        />
        <Modal
          title={currentFarm ? "Edit Farm" : "Add Farm"}
          visible={isModalVisible}
          onCancel={() => setIsModalVisible(false)}
          footer={null}
        >
          <Form form={form} onFinish={handleSubmit}>
            <Form.Item name="farmID" label="ID" rules={[{ required: true }]}>
              <Input disabled={!!currentFarm} />
            </Form.Item>
            <Form.Item
              name="farmName"
              label="Farm Name"
              rules={[{ required: true, message: "Please enter farm name" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="Description"
              rules={[
                { required: true, message: "Please enter farm description" },
              ]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item
              name="imageLink"
              label="Image Link"
              rules={[{ required: true, message: "Please enter image links" }]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Save
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default FarmManagement;
