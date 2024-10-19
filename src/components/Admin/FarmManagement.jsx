import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification } from "antd";
import Sidebar from "./Admin.jsx";

const FarmManagement = () => {
  const [farmList, setFarmList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentFarm, setCurrentFarm] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8082/api/farm"; // API URL của bạn
  const getApi = "http://localhost:8082/api/farm/list"; // API URL của bạn
  const token = localStorage.getItem("token");
  const [selectedFiles, setSelectedFiles] = useState([]);

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
      setFarmList(response.data);
    } catch (error) {
      notification.error({ message: "Không thể lấy danh sách trang trại" });
    }
  };

  const handleEdit = (farm) => {
    setCurrentFarm(farm);
    setIsModalVisible(true);
    form.setFieldsValue(farm);
    setSelectedFiles([]);
  };

  const handleDelete = (farmId) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa trang trại này?",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${farmId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          fetchFarmList();
          notification.success({ message: "Xóa trang trại thành công" });
        } catch (error) {
          notification.error({ message: "Không thể xóa trang trại" });
        }
      },
    });
  };

  const handleSubmit = async (values) => {
    try {
      const uploadedImages = await uploadImagesToCloudinary(selectedFiles);

      const farmData = {
        ...values,
        imageLinks: uploadedImages.map((image) => ({
          imageLink: image.url,
        })),
      };

      if (currentFarm) {
        await axios.put(`${apiUrl}/${currentFarm.farmId}`, farmData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        await axios.post(apiUrl, farmData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      fetchFarmList();
      setIsModalVisible(false);
      form.resetFields();
      setSelectedFiles([]);
      notification.success({ message: "Lưu trang trại thành công" });
    } catch (error) {
      console.error(
        "Error in handleSubmit:",
        error.response ? error.response.data : error
      );
      notification.error({ message: "Không thể lưu trang trại" });
    }
  };

  const uploadImagesToCloudinary = async (files) => {
    const cloudinaryUrl = `https://api.cloudinary.com/v1_1/dx6ldhzdj/image/upload`;
    const uploadPreset = "cxwt7hpl"; // Upload preset của Cloudinary

    if (files.length === 0) return [];

    const uploadPromises = files.map((file) => {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("upload_preset", uploadPreset);

      return axios
        .post(cloudinaryUrl, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => response.data)
        .catch((error) => {
          console.error("Lỗi khi tải ảnh:", error);
          notification.error({
            message: `Lỗi khi tải ảnh: ${error.message}`,
          });
          return null;
        });
    });

    const results = await Promise.all(uploadPromises);
    return results.filter((result) => result !== null);
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h1>Quản lý trang trại</h1>
        <Button
          type="primary"
          onClick={() => {
            setIsModalVisible(true);
            setCurrentFarm(null);
            form.resetFields();
            setSelectedFiles([]);
          }}
        >
          Thêm Trang Trại
        </Button>
        <Table
          dataSource={farmList}
          rowKey="farmId"
          pagination={{ pageSize: 5 }}
          columns={[
            { title: "Mã Trang Trại", dataIndex: "farmId" },
            { title: "Tên Trang Trại", dataIndex: "farmName" },
            { title: "Mô Tả", dataIndex: "description" },
            {
              title: "Ảnh",
              dataIndex: "imageLinks",
              render: (imageLinks) => (
                <div>
                  {imageLinks && imageLinks.length > 0
                    ? imageLinks.map((image, index) => (
                        <img
                          key={index}
                          src={image.imageLink}
                          alt="Farm"
                          style={{
                            width: "70px",
                            height: "70px",
                            marginRight: "8px",
                          }}
                        />
                      ))
                    : "Không có ảnh"}
                </div>
              ),
            },
            {
              title: "Hành Động",
              render: (text, farm) => (
                <>
                  <Button onClick={() => handleEdit(farm)}>Sửa</Button>
                  <Button danger onClick={() => handleDelete(farm.farmId)}>
                    Xóa
                  </Button>
                </>
              ),
            },
          ]}
        />
        <Modal
          title={currentFarm ? "Sửa Trang Trại" : "Thêm Trang Trại"}
          visible={isModalVisible}
          onCancel={() => setIsModalVisible(false)}
          footer={null}
        >
          <Form form={form} onFinish={handleSubmit}>
            <Form.Item
              name="farmId"
              label="Mã Trang Trại"
              rules={[{ required: true }]}
            >
              <Input disabled={!!currentFarm} />
            </Form.Item>
            <Form.Item
              name="farmName"
              label="Tên Trang Trại"
              rules={[
                { required: true, message: "Vui lòng nhập tên trang trại" },
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="Mô Tả"
              rules={[{ required: true, message: "Vui lòng nhập mô tả" }]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item label="Tải Ảnh" required>
              <input
                type="file"
                accept="image/jpeg, image/png"
                onChange={(e) => {
                  const files = Array.from(e.target.files);
                  setSelectedFiles(files);
                }}
                multiple
                required
              />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit">
                Lưu
              </Button>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default FarmManagement;
