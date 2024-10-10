import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification } from "antd";
import Sidebar from "./Admin.jsx";

const KoiManagement = () => {
  const [koiList, setKoiList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentKoi, setCurrentKoi] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8080/api/koi";
  const getApi = "http://localhost:8080/api/koi/list";
  const token = localStorage.getItem("token");

  // Fetch koi data on component load
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
      notification.error({ message: "Failed to fetch koi list" });
    }
  };

  const handleEdit = (koi) => {
    setCurrentKoi(koi);
    setIsModalVisible(true);
    form.setFieldsValue(koi);
  };

  const handleDelete = (koiID) => {
    Modal.confirm({
      title: "Are you sure you want to delete this Koi?",
      okText: "Yes",
      okType: "danger",
      cancelText: "No",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${koiID}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          fetchKoiList();
          notification.success({ message: "Koi deleted successfully" });
        } catch (error) {
          notification.error({ message: "Failed to delete koi" });
        }
      },
    });
  };

  const handleSubmit = async (values) => {
    try {
      const { imageLink, ...koiData } = values; // Lấy hình ảnh và các thông tin khác
      // Chuyển đổi imageLink thành mảng nếu cần
      const imagesArray = imageLink.split(",").map((link) => link.trim()); // Tách đường dẫn nếu có nhiều

      const koiDataWithImages = {
        ...koiData,
        imageLinks: imagesArray, // Thay thế bằng mảng hình ảnh
      };

      if (currentKoi) {
        await axios.put(`${apiUrl}/${currentKoi.koiID}`, koiDataWithImages, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
      } else {
        await axios.post(apiUrl, koiDataWithImages, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
      }

      fetchKoiList(); // Cập nhật lại danh sách
      setIsModalVisible(false);
      form.resetFields();
      notification.success({ message: "Koi saved successfully" });
    } catch (error) {
      notification.error({ message: "Failed to save koi" });
    }
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h1>Koi Management</h1>
        <Button
          type="primary"
          onClick={() => {
            setIsModalVisible(true);
            setCurrentKoi(null);
            form.resetFields(); // Reset fields for new entry
          }}
        >
          Add Koi
        </Button>
        <Table
          dataSource={koiList}
          rowKey="koiID"
          pagination={{ pageSize: 5 }}
          columns={[
            { title: "ID", dataIndex: "koiID" },
            { title: "Species", dataIndex: "species" },
            { title: "Description", dataIndex: "description" },
            {
              title: "Image Links",
              dataIndex: "imageLinks",
              render: (imageLinks) => (
                <div>
                  {imageLinks && imageLinks.length > 0
                    ? imageLinks.map((link, index) => (
                        <img
                          key={index}
                          src={link}
                          alt={`Koi ${index + 1}`}
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
              render: (text, koi) => (
                <>
                  <Button onClick={() => handleEdit(koi)}>Edit</Button>
                  <Button danger onClick={() => handleDelete(koi.koiID)}>
                    Delete
                  </Button>
                </>
              ),
            },
          ]}
        />
        <Modal
          title={currentKoi ? "Edit Koi" : "Add Koi"}
          visible={isModalVisible}
          onCancel={() => setIsModalVisible(false)}
          footer={null}
        >
          <Form form={form} onFinish={handleSubmit}>
            <Form.Item name="koiID" label="ID" rules={[{ required: true }]}>
              <Input disabled={!!currentKoi} />
            </Form.Item>
            <Form.Item
              name="species"
              label="Species"
              rules={[{ required: true, message: "Vui lòng nhập giống cá" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="Description"
              rules={[
                { required: true, message: "Vui lòng nhập thông tin chi tiết" },
              ]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item
              name="imageLink"
              label="Image Link"
              rules={[
                { required: true, message: "Vui lòng nhập đường dẫn ảnh cá" },
              ]}
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

export default KoiManagement;
