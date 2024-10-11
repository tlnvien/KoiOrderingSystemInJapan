import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification, Upload } from "antd";
import { storage } from "../../config/firebase.js";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage"; // Import necessary functions
import Sidebar from "./Admin.jsx";

const KoiManagement = () => {
  const [koiList, setKoiList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentKoi, setCurrentKoi] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8082/api/koi";
  const getApi = "http://localhost:8082/api/koi/list";
  const token = localStorage.getItem("token");
  const [fileList, setFileList] = useState([]);

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
    setFileList(koi.imageLinks.map((link) => ({ url: link }))); // Set file list for editing
  };

  const handleDelete = (koiId) => {
    Modal.confirm({
      title: "Are you sure you want to delete this Koi?",
      okText: "Yes",
      okType: "danger",
      cancelText: "No",
      onOk: async () => {
        try {
          await axios.delete(`${apiUrl}/${koiId}`, {
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
      const imageLinks = await uploadImages(fileList); // Upload images and get links
      const koiDataWithImages = {
        ...values,
        imageLinks, // Include the uploaded image links
      };

      if (currentKoi) {
        await axios.put(`${apiUrl}/${currentKoi.koiId}`, koiDataWithImages, {
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

      fetchKoiList();
      setIsModalVisible(false);
      form.resetFields();
      setFileList([]); // Reset file list
      notification.success({ message: "Koi saved successfully" });
    } catch (error) {
      notification.error({ message: "Failed to save koi" });
    }
  };

  const uploadImages = async (files) => {
    const promises = files.map(async (file) => {
      const storageRef = ref(storage, `koies/${file.name}`);
      await uploadBytes(storageRef, file.originFileObj, {
        contentType: "image/jpeg",
      });
      const downloadURL = await getDownloadURL(storageRef);
      console.log(`Uploaded ${file.name} and got URL: ${downloadURL}`); // Log the URL
      return downloadURL;
    });
    return await Promise.all(promises);
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
            form.resetFields();
            setFileList([]); // Reset file list for new entry
          }}
        >
          Add Koi
        </Button>
        <Table
          dataSource={koiList}
          rowKey="koiId"
          pagination={{ pageSize: 5 }}
          columns={[
            { title: "Koi ID", dataIndex: "koiId" },
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
                  <Button danger onClick={() => handleDelete(koi.koiId)}>
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
            <Form.Item name="koiId" label="Koi ID" rules={[{ required: true }]}>
              <Input disabled={!!currentKoi} />
            </Form.Item>
            <Form.Item
              name="species"
              label="Species"
              rules={[{ required: true, message: "Please enter koi species" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="description"
              label="Description"
              rules={[
                { required: true, message: "Please enter koi description" },
              ]}
            >
              <Input.TextArea />
            </Form.Item>
            <Form.Item
              name="imageLinks"
              label="Upload Images"
              rules={[{ required: true, message: "Please upload images" }]}
            >
              <Upload
                fileList={fileList}
                beforeUpload={(file) => {
                  setFileList((prev) => [...prev, file]);
                  return false; // Prevent automatic upload
                }}
                onRemove={(file) => {
                  setFileList((prev) => prev.filter((f) => f.uid !== file.uid));
                }}
                multiple
              >
                <Button>Upload</Button>
              </Upload>
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
