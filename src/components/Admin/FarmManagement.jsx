import React, { useState, useEffect } from "react";
import axios from "axios";
import { Table, Button, Modal, Form, Input, notification, Upload } from "antd";
import { storage } from "../../config/firebase.js";
import { ref, uploadBytes, getDownloadURL } from "firebase/storage"; // Import necessary functions
import Sidebar from "./Admin.jsx";

const FarmManagement = () => {
  const [farmList, setFarmList] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [currentFarm, setCurrentFarm] = useState(null);
  const [form] = Form.useForm();
  const apiUrl = "http://localhost:8082/api/farm";
  const getApi = "http://localhost:8082/api/farm/list";
  const apiImage = "http://localhost:8082/api/farm/images";
  const token = localStorage.getItem("token");
  const [fileList, setFileList] = useState([]);

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
      console.log(response.data);
      setFarmList(response.data);
    } catch (error) {
      notification.error({ message: "Failed to fetch farm list" });
    }
  };

  const handleEdit = (farm) => {
    try {
      setCurrentFarm(farm);
      setIsModalVisible(true);
      form.setFieldsValue(farm);

      // Ensure imageLinks is a valid array and each link is a string
      setFileList(
        Array.isArray(farm.imageLinks)
          ? farm.imageLinks
              .filter((link) => typeof link === "string") // Only keep strings
              .map((link) => ({ url: link })) // Map to file list format
          : []
      ); // Set file list for editing
    } catch (error) {
      console.error("Error in handleEdit:", error);
      notification.error({ message: "Failed to load edit form" });
    }
  };

  const handleDelete = (farmId) => {
    Modal.confirm({
      title: "Are you sure you want to delete this Farm?",
      okText: "Yes",
      okType: "danger",
      cancelText: "No",
      onOk: async () => {
        try {
          // Xóa các hình ảnh liên quan đến farm
          await axios.delete(`${apiImage}/${farmId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          // Xóa farm sau khi xóa hình ảnh
          await axios.delete(`${apiUrl}/${farmId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          fetchFarmList();
          notification.success({ message: "Farm deleted successfully" });
        } catch (error) {
          notification.error({
            message: "Failed to delete farm or related images",
          });
        }
      },
    });
  };

  const handleSubmit = async (values) => {
    try {
      // Upload images and get their URLs in the required format
      const updatedFileList = await uploadImage(fileList); // Call uploadImage with fileList

      // Format image links for database
      const formattedImageLinks = updatedFileList.map((file) => ({
        imageLink: file.url,
      }));

      const farmData = { ...values, imageLinks: formattedImageLinks }; // Ensure `imageLinks` is formatted correctly

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
      setFileList(updatedFileList); // Set updated file list with uploaded images
      notification.success({ message: "Farm saved successfully" });
    } catch (error) {
      console.error(
        "Error in handleSubmit:",
        error.response ? error.response.data : error
      );
      notification.error({ message: "Failed to save farm" });
    }
  };

  const uploadImage = async (files) => {
    const promises = files.map(async (file) => {
      const isImage = file.type.startsWith("image/");
      if (!isImage) {
        console.error(`File ${file.name} is not an image. Skipping upload.`);
        return null; // Bỏ qua nếu không phải tệp hình ảnh
      }

      try {
        const storageRef = ref(storage, `farms/${file.name}`);
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
    return (await Promise.all(promises)).filter(Boolean); // Lọc bỏ các tệp lỗi
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
            form.resetFields();
            setFileList([]); // Reset file list for new entry
          }}
        >
          Add Farm
        </Button>
        <Table
          dataSource={farmList}
          rowKey="farmId"
          pagination={{ pageSize: 5 }}
          columns={[
            { title: "Farm ID", dataIndex: "farmId" },
            { title: "Farm Name", dataIndex: "farmName" },
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
                  <Button danger onClick={() => handleDelete(farm.farmId)}>
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
            <Form.Item
              name="farmId"
              label="Farm ID"
              rules={[{ required: true }]}
            >
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
                    return Upload.LIST_IGNORE; // Hoặc return false;
                  }
                  setFileList((prev) => [...prev, file]);
                  return false; // Prevent automatic upload
                }}
                onRemove={(file) => {
                  setFileList((prev) => prev.filter((f) => f.uid !== file.uid));
                }}
                onPreview={async (file) => {
                  let src = file.url; // Sử dụng file.url đã upload
                  if (!src) {
                    src = await getDownloadURL(ref(storage, file.name)); // Nếu chưa có URL, lấy từ Firebase
                  }
                  const imgWindow = window.open(src);
                  imgWindow.document.write(
                    `<img src="${src}" alt="Image Preview" />`
                  );
                }}
                accept=".jpeg,.jpg,.png"
                listType="picture-card"
              >
                <div>
                  <div>Upload</div>
                </div>
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

export default FarmManagement;
