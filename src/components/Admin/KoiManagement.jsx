import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification, Upload } from "antd";
import { storage } from "../../config/firebase.js";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage";
import Sidebar from "./Admin.jsx";

const KoiManagement = () => {
  const [koiList, setKoiList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentKoi, setCurrentKoi] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8082/api/koi"; // Adjust your API URL
  const getApi = "http://localhost:8082/api/koi/list"; // Adjust your API URL
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
    try {
      setCurrentKoi(koi);
      setIsModalVisible(true);
      form.setFieldsValue(koi);

      setFileList(
        Array.isArray(koi.imageLinks)
          ? koi.imageLinks
              .filter((link) => typeof link === "string")
              .map((link) => ({ url: link }))
          : []
      );
    } catch (error) {
      console.error("Error in handleEdit:", error);
      notification.error({ message: "Failed to load edit form" });
    }
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
      const updatedFileList = await uploadImage(fileList);

      const formattedImageLinks = updatedFileList.map((file) => ({
        imageLink: file.url,
      }));

      const koiData = { ...values, imageLinks: formattedImageLinks };

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
      setFileList(updatedFileList);
      notification.success({ message: "Koi saved successfully" });
    } catch (error) {
      console.error(
        "Error in handleSubmit:",
        error.response ? error.response.data : error
      );
      notification.error({ message: "Failed to save koi" });
    }
  };

  const uploadImage = async (files) => {
    const promises = files.map(async (file) => {
      const isImage = file.type.startsWith("image/");
      if (!isImage) {
        console.error(`File ${file.name} is not an image. Skipping upload.`);
        return null;
      }

      try {
        const storageRef = ref(storage, `koi/${file.name}`); // Change the storage path
        await uploadBytes(storageRef, file.originFileObj, {
          contentType: "image/jpeg",
        });
        const downloadURL = await getDownloadURL(storageRef);
        console.log(`Uploaded ${file.name} and got URL: ${downloadURL}`);
        return {
          uid: file.uid,
          name: file.name,
          status: "done",
          url: downloadURL,
        };
      } catch (error) {
        console.error(`Error uploading ${file.name}:`, error);
        return null;
      }
    });
    return (await Promise.all(promises)).filter(Boolean);
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
            setFileList([]);
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
            { title: "Koi Specie", dataIndex: "species" },
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
                          src={link.imageLink}
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
              label="Koi Species"
              rules={[{ required: true, message: "Please enter koi specie" }]}
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
                  const isJpgOrPng =
                    file.type === "image/jpeg" || file.type === "image/png";
                  if (!isJpgOrPng) {
                    notification.error({
                      message: "You can only upload JPG/PNG files!",
                    });
                    return Upload.LIST_IGNORE;
                  }
                  setFileList((prev) => [...prev, file]);
                  return false;
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
