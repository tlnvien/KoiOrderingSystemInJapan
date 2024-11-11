import { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification } from "antd";
import Sidebar from "./Admin.jsx";
import api from "../../config/axios.js";

const KoiManagement = () => {
  const [koiList, setKoiList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentKoi, setCurrentKoi] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "koi";
  const getApi = "koi/list"; // API URL của bạn
  const token = localStorage.getItem("token");
  const [selectedFiles, setSelectedFiles] = useState([]);

  useEffect(() => {
    fetchKoiList();
  }, []);

  const fetchKoiList = async () => {
    try {
      const response = await api.get(getApi, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const data = response.data;

      if (Array.isArray(data)) {
        setKoiList(data);
      } else {
        console.error("Dữ liệu không phải là một mảng:", data);
        setKoiList([]);
      }
    } catch (error) {
      notification.error({ message: "Không thể lấy danh sách koi" });
      console.error("Error fetching koi list:", error);
    }
  };

  const handleEdit = (koi) => {
    setCurrentKoi(koi);
    setIsModalVisible(true);
    form.setFieldsValue(koi);
    setSelectedFiles([]); // Reset selected files
  };

  const deleteImage = async (imageLink) => {
    if (!currentKoi) {
      notification.error({
        message: "Không tìm thấy cá koi hiện tại để xóa ảnh",
      });
      return;
    }

    try {
      await api.delete(
        `koi/images/remove/${currentKoi.koiId}?imageLink=${imageLink}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
          data: {
            imageLink: imageLink,
          },
        }
      );
      notification.success({ message: "Xóa ảnh thành công" });
      fetchKoiList(); // Lấy lại danh sách koi sau khi xóa ảnh
    } catch (error) {
      notification.error({ message: "Không thể xóa ảnh" });
      console.error("Error deleting image:", error);
    }
  };

  const handleDelete = (koiId, imageLinks) => {
    Modal.confirm({
      title: "Bạn có chắc chắn muốn xóa cá koi này?",
      okText: "Có",
      okType: "danger",
      cancelText: "Không",
      onOk: async () => {
        try {
          await api.delete(`${apiUrl}/${koiId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          // Lấy lại danh sách koi sau khi xóa
          fetchKoiList();
          notification.success({ message: "Xóa koi thành công" });
        } catch (error) {
          notification.error({ message: "Không thể xóa koi" });
          console.error("Error deleting koi:", error);
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
        await api.put(`${apiUrl}/${currentKoi.koiId}`, koiData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        await api.post(apiUrl, koiData, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      fetchKoiList();
      setIsModalVisible(false);
      form.resetFields();
      setSelectedFiles([]); // Reset selected files after submission
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
          return null; // Return null if upload fails
        });
    });

    const results = await Promise.all(uploadPromises);
    return results.filter((result) => result !== null); // Filter out failed uploads
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
            setSelectedFiles([]); // Reset selected files when adding a new koi
          }}
        >
          Thêm Koi
        </Button>
        <Table
          dataSource={koiList}
          rowKey="koiId"
          pagination={{ pageSize: 2 }}
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
                        <div
                          key={index}
                          style={{ display: "flex", alignItems: "center" }}
                        >
                          <img
                            src={image.imageLink}
                            alt="Koi"
                            style={{
                              width: "70px",
                              height: "70px",
                              marginRight: "8px",
                            }}
                          />
                          {/* <Button
                            type="link"
                            danger
                            onClick={() => deleteImage(image.imageLink)}
                          >
                            Xóa
                          </Button> */}
                        </div>
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
                  <Button
                    danger
                    onClick={() =>
                      handleDelete(
                        koi.koiId,
                        koi.imageLinks.map((image) => image.imageLink)
                      )
                    }
                  >
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
            {/* <Form.Item name="koiId" label="Koi ID" rules={[{ required: true }]} >
              <Input disabled={!!currentKoi} />
            </Form.Item> */}
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
              rules={[{ message: "Vui lòng nhập mô tả koi" }]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item label="Tải Ảnh" required>
              <input
                type="file"
                accept="image/jpeg, image/png"
                onChange={(e) => {
                  const files = Array.from(e.target.files);
                  setSelectedFiles(files); // Set selected files into state
                }}
                multiple // Allow multiple file selection
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
