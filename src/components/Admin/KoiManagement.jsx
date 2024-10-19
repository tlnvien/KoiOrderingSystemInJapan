import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification } from "antd";
import Sidebar from "./Admin.jsx";

const KoiManagement = () => {
  const [koiList, setKoiList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentKoi, setCurrentKoi] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8082/api/koi"; // API URL của bạn
  const getApi = "http://localhost:8082/api/koi/list"; // API URL của bạn
  const token = localStorage.getItem("token");
  const [selectedFiles, setSelectedFiles] = useState([]); // Trạng thái cho các tệp đã chọn

  useEffect(() => {
    fetchKoiList();
  }, []);

  const fetchKoiList = async () => {
    try {
      const response = await axios.get(getApi, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setKoiList(response.data);
    } catch (error) {
      notification.error({ message: "Không thể lấy danh sách koi" });
    }
  };

  const handleEdit = (koi) => {
    setCurrentKoi(koi);
    setIsModalVisible(true);
    form.setFieldsValue(koi);
    setSelectedFiles([]); // Đặt lại các tệp đã chọn
  };

  const handleDelete = (koiId) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa koi này?",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${koiId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          fetchKoiList();
          notification.success({ message: "Xóa koi thành công" });
        } catch (error) {
          notification.error({ message: "Không thể xóa koi" });
        }
      },
    });
  };

  const handleSubmit = async (values) => {
    try {
      const uploadedImages = await uploadImagesToCloudinary(selectedFiles);

      const koiData = {
        ...values,
        imageLinks: uploadedImages.map((image) => ({
          imageLink: image.url,
        })),
      };

      if (currentKoi) {
        await axios.put(`${apiUrl}/${currentKoi.koiId}`, koiData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        await axios.post(apiUrl, koiData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      fetchKoiList();
      setIsModalVisible(false);
      form.resetFields();
      setSelectedFiles([]); // Đặt lại các tệp đã chọn sau khi gửi
      notification.success({ message: "Lưu koi thành công" });
    } catch (error) {
      console.error(
        "Lỗi trong handleSubmit:",
        error.response ? error.response.data : error
      );
      notification.error({ message: "Không thể lưu koi" });
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
    return results.filter((result) => result !== null); // Lọc bỏ các tải lên thất bại
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h1>Quản lý cá Koi</h1>
        <Button
          type="primary"
          onClick={() => {
            setIsModalVisible(true);
            setCurrentKoi(null);
            form.resetFields();
            setSelectedFiles([]); // Đặt lại các tệp đã chọn khi thêm một Koi mới
          }}
        >
          Thêm Koi
        </Button>
        <Table
          dataSource={koiList}
          rowKey="koiId"
          pagination={{ pageSize: 5 }}
          columns={[
            { title: "Koi ID", dataIndex: "koiId" },
            { title: "Loại Koi", dataIndex: "species" },
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
                          alt="Koi"
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
              render: (text, koi) => (
                <>
                  <Button onClick={() => handleEdit(koi)}>Sửa</Button>
                  <Button danger onClick={() => handleDelete(koi.koiId)}>
                    Xóa
                  </Button>
                </>
              ),
            },
          ]}
        />
        <Modal
          title={currentKoi ? "Sửa Koi" : "Thêm Koi"}
          visible={isModalVisible}
          onCancel={() => setIsModalVisible(false)}
          footer={null}
        >
          <Form form={form} onFinish={handleSubmit}>
            <Form.Item name="koiId" label="Koi ID" rules={[{ required: true }]}>
              <Input disabled={!!currentKoi} />
            </Form.Item>
            <Form.Item
              name="species"
              label="Loại Koi"
              rules={[{ required: true, message: "Vui lòng nhập loại koi" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="Mô Tả"
              rules={[{ required: true, message: "Vui lòng nhập mô tả koi" }]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item label="Tải Ảnh" required>
              <input
                type="file"
                accept="image/jpeg, image/png"
                onChange={(e) => {
                  const files = Array.from(e.target.files);
                  setSelectedFiles(files); // Đặt các tệp đã chọn vào trạng thái
                }}
                multiple // Cho phép chọn nhiều tệp
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

export default KoiManagement;
