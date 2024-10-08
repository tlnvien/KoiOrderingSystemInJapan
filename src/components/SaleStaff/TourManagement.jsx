import {
  Button,
  Form,
  Input,
  Image,
  Modal,
  Select,
  Table,
  Upload,
  InputNumber,
  DatePicker,
  Spin,
} from "antd";
import { useForm } from "antd/es/form/Form";
import { PlusOutlined } from "@ant-design/icons";
import axios from "axios";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import upLoadFile from "../../utils/file";
import dayjs from "dayjs";

const TourManagement = () => {
  const [tours, setTours] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [form] = useForm();
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [fileList, setFileList] = useState([]);
  const [actionLoading, setActionLoading] = useState(false); // New loading state for actions
  const api = "https://66e79651b17821a9d9d95a2b.mockapi.io/Tour"; // API URL

  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }
    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
  };

  const handleChange = ({ fileList: newFileList }) => setFileList(newFileList);

  const uploadButton = (
    <div>
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>Upload</div>
    </div>
  );

  const fetchTour = async () => {
    setActionLoading(true); // Start loading
    const response = await axios.get(api);
    const toursWithValidDate = response.data.map((tour) => ({
      ...tour,
      startDate: dayjs(tour.startDate), // Convert to dayjs
    }));
    setTours(toursWithValidDate);
    setActionLoading(false); // Stop loading after fetching
  };

  useEffect(() => {
    fetchTour(); // Fetch tours on initial load
  }, []);

  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "Tour ID",
      dataIndex: "tour_ID",
      key: "tour_ID",
    },
    {
      title: "Tour Name",
      dataIndex: "tourName",
      key: "tourName",
    },
    {
      title: "Price",
      dataIndex: "price",
      key: "price",
    },
    {
      title: "Start Date",
      dataIndex: "startDate",
      key: "startDate",
      render: (startDate) => dayjs(startDate).format("DD-MM-YYYY"),
    },
    {
      title: "Duration",
      dataIndex: "duration",
      key: "duration",
    },
    {
      title: "End Date",
      dataIndex: "endDate",
      key: "endDate",
      render: (_, tour) => {
        const { startDate, duration } = tour;
        if (!startDate || !duration) return "-";
        const match = duration.match(/(\d+)N(\d+)Đ/);
        if (!match) return "-";
        const days = parseInt(match[1]);
        const endDate = dayjs(startDate).add(days - 1, "day");
        return endDate.format("DD-MM-YYYY");
      },
    },
    {
      title: "Quantity",
      dataIndex: "quantity",
      key: "quantity",
    },
    {
      title: "Type",
      dataIndex: "type",
      key: "type",
    },
    {
      title: "Image",
      dataIndex: "image_path",
      key: "image_path",
      render: (image_path) => <Image width={100} src={image_path} />,
    },
    {
      title: "Action",
      dataIndex: "id",
      key: "id",
      render: (id, tour) => (
        <div>
          <Button
            type="primary"
            onClick={() => {
              setOpenModal(true);
              form.setFieldsValue(tour);
            }}
          >
            Update
          </Button>
          <Button type="primary" danger onClick={() => handleDelete(id)}>
            Delete
          </Button>
        </div>
      ),
    },
  ];

  const handleOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    form.resetFields();
    setFileList([]); // Clear file list on close
  };

  async function handleSubmit(values) {
    if (fileList.length > 0) {
      const file = fileList[0];
      const url = await upLoadFile(file.originFileObj);
      values.image_path = url;
    }

    if (values.startDate && !dayjs(values.startDate).isValid()) {
      toast.error("Invalid start date!");
      return;
    }

    try {
      setLoading(true);
      if (values.id) {
        await axios.put(`${api}/${values.id}`, values);
        toast.success("Tour updated successfully");
      } else {
        await axios.post(api, values);
        toast.success("Tour created successfully");
      }
      setActionLoading(true); // Start loading before fetch
      form.resetFields();
      setTimeout(() => {
        fetchTour(); // Re-fetch tours after creating/updating
        handleCloseModal();
      }, 1000); // Simulate 1s loading
    } catch {
      toast.error("Operation failed!");
    } finally {
      setLoading(false);
    }
  }

  const handleDelete = (tour_ID) => {
    Modal.confirm({
      title: "Confirm Deletion",
      content: "Are you sure you want to delete this tour?",
      onOk: async () => {
        try {
          setActionLoading(true); // Start loading before deleting
          await axios.delete(`${api}/${tour_ID}`);
          toast.success("Tour deleted successfully");
          setTimeout(() => {
            fetchTour();
          }, 1000); // Simulate 1s loading after deletion
        } catch {
          toast.error("Deletion failed!");
        } finally {
          setActionLoading(false);
        }
      },
    });
  };

  const handleOk = () => {
    form.submit();
  };

  const getBase64 = (file) =>
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(reader.result);
      reader.onerror = (error) => reject(error);
    });

  const validateStartDate = (_, value) => {
    if (!value) {
      return Promise.reject(new Error("Please select a start date!"));
    }

    const today = dayjs();
    const oneMonthFromNow = today.add(1, "month");

    if (value.isBefore(today, "day")) {
      return Promise.reject(new Error("Start date cannot be in the past!"));
    }
    if (value.isBefore(oneMonthFromNow, "day")) {
      return Promise.reject(
        new Error("Start date cannot be less than one month from now!")
      );
    }

    return Promise.resolve();
  };

  const validateDuration = (_, value) => {
    if (!value) return Promise.resolve();

    const match = value.match(/(\d+)N(\d+)Đ/);
    if (!match) {
      return Promise.reject(
        new Error("Invalid format! Expected: XNXD (e.g., 4N3Đ)")
      );
    }

    const days = parseInt(match[1]);
    const nights = parseInt(match[2]);

    if (days <= nights) {
      return Promise.reject(new Error("Days must be greater than nights!"));
    }
    if (days - nights !== 1) {
      return Promise.reject(
        new Error("Days must be exactly one more than nights!")
      );
    }

    return Promise.resolve();
  };

  return (
    <div className="sale-staff">
      <div style={{ padding: "20px" }}>
        <h1 style={{ textAlign: "center" }}>Tour Management</h1>
        <Button
          type="primary"
          onClick={handleOpenModal}
          style={{ marginBottom: "20px" }}
        >
          Create New Tour
        </Button>
        <Spin spinning={actionLoading} tip="Loading...">
          <Table columns={columns} dataSource={tours} rowKey="id" />
        </Spin>
        <Modal
          open={openModal}
          onCancel={handleCloseModal}
          onOk={handleOk}
          footer={[
            <Button key="back" onClick={handleCloseModal}>
              Cancel
            </Button>,
            <Button
              key="submit"
              type="primary"
              loading={loading}
              onClick={handleOk}
            >
              Submit
            </Button>,
          ]}
        >
          <Form
            form={form}
            onFinish={handleSubmit}
            layout="horizontal"
            labelCol={{ span: 6 }} // Tỉ lệ nhãn
            wrapperCol={{ span: 18 }} // Tỉ lệ input
          >
            <Form.Item name="id" style={{ display: "none" }}>
              <Input readOnly />
            </Form.Item>
            <Form.Item
              label="Tour ID"
              name="tour_ID"
              rules={[{ required: true, message: "Please input Tour ID!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="Tour Name"
              name="tourName"
              rules={[{ required: true, message: "Please input tour name!" }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="Price"
              name="price"
              rules={[{ required: true, message: "Please input price!" }]}
            >
              <InputNumber
                min={0}
                style={{ width: "100%" }}
                // Formatter: Định dạng dữ liệu hiển thị
                formatter={(value) => {
                  if (!value) return "";
                  return `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, "."); // Thêm dấu chấm cho số lớn
                }}
                // Parser: Chuyển đổi dữ liệu từ dạng hiển thị về dạng số
                parser={(value) =>
                  value
                    .replace(/\.(?=\d{3})/g, "")
                    .replace(/\./g, "")
                    .trim()
                } // Xóa dấu phẩy khi nhập dữ liệu
              />
            </Form.Item>
            <Form.Item
              label="Start Date"
              name="startDate"
              rules={[{ required: true, validator: validateStartDate }]}
            >
              <DatePicker />
            </Form.Item>
            <Form.Item
              label="Duration"
              name="duration"
              rules={[{ required: true, validator: validateDuration }]}
            >
              <Input placeholder="e.g., 4N3Đ" />
            </Form.Item>
            <Form.Item
              label="Quantity"
              name="quantity"
              rules={[
                { required: true, message: "Please input quantity!" },
                { type: "number" },
              ]}
            >
              <Select placeholder="Select quantity">
                <Select.Option value={6}>6</Select.Option>
                <Select.Option value={7}>7</Select.Option>
                <Select.Option value={8}>8</Select.Option>
                <Select.Option value={9}>9</Select.Option>
                <Select.Option value={10}>10</Select.Option>
                <Select.Option value={11}>11</Select.Option>
                <Select.Option value={12}>12</Select.Option>
                <Select.Option value={13}>13</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item
              label="Type"
              name="type"
              rules={[{ required: true, message: "Please select type!" }]}
            >
              <Select>
                <Select.Option value="local">Private</Select.Option>
                <Select.Option value="international">Public</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item label="Image">
              <Upload
                listType="picture-card"
                fileList={fileList}
                onPreview={handlePreview}
                onChange={handleChange}
                maxCount={1}
              >
                {fileList.length < 1 ? uploadButton : null}
              </Upload>
              <Modal
                open={previewOpen}
                title="Preview Image"
                footer={null}
                onCancel={() => setPreviewOpen(false)}
              >
                <img
                  alt="Preview"
                  style={{ width: "100%" }}
                  src={previewImage}
                />
              </Modal>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};

export default TourManagement;
