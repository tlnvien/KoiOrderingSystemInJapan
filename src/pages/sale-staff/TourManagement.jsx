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
} from "antd";
import { useForm } from "antd/es/form/Form";
import { PlusOutlined } from "@ant-design/icons";
import axios from "axios";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import upLoadFile from "../../utils/file";
import dayjs from "dayjs";

function TourManagement() {
  // Quản lí sinh viên (CRUD)
  // Tạo, sửa, xóa sinh viên, lưu trữ và hiển thị danh sách sinh viên
  const [tours, setTours] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form] = useForm();

  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [fileList, setFileList] = useState([]);

  const api = "https://66e79651b17821a9d9d95a2b.mockapi.io/Tour"; // biến lưu trữ api

  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }
    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
  };
  const handleChange = ({ fileList: newFileList }) => setFileList(newFileList);
  
  const uploadButton = (
    <button
      style={{
        border: 0,
        background: "none",
      }}
      type="button"
    >
      <PlusOutlined />
      <div
        style={{
          marginTop: 8,
        }}
      >
        Upload
      </div>
    </button>
  );

  const fetchStudent = async () => {
    // function lấy data từ BE => fetch...
    // lấy thông tin từ api => axios(thư viện giúp call api)

    const response = await axios.get(api);
    // js gọi là promise => function bất đồng bộ => cần thời gian để thực hiện
    // await: đợi tới khi mà api trả về kết quả

    setTours(response.data);
    // GET => dùng để lấy dữ liệu
  };

  useEffect(() => {
    // truyền vào 2 tham số: function và []: dependency array
    // đây là một function hành động
    // giúp chạy một hành động gì đó
    // khi chạy cần 1 even
    // [] => có nghĩa chạy khi load trang lần đầu
    // [number] => chạy mỗi khi number thay đổi
    fetchStudent(); // gọi hàm fetchStudent khi load trang lần đầu
  }, []);

  const columns = [
    {
      title: "tour_ID",
      dataIndex: "tour_ID", // đặt theo tên biến từ BE
      key: "tour_ID",
    },
    {
      title: "tourName",
      dataIndex: "tourName", // đặt theo tên biến từ BE
      key: "tourName",
    },
    {
      title: "price",
      dataIndex: "price", // đặt theo tên biến từ BE
      key: "price",
    },
    {
      title: "startDate",
      dataIndex: "startDate", // đặt theo tên biến từ BE
      key: "startDate",
    },
    {
      title: "quantity",
      dataIndex: "quantity", // đặt theo tên biến từ BE
      key: "quantity",
    },
    {
      title: "type",
      dataIndex: "type", // đặt theo tên biến từ BE
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
      render: (id) => (
        <div>
          <Button
            type="primary"
            danger
            onClick={() => {
              const isConfirmed = window.confirm(
                "Bạn có chắc chắn muốn xóa không?"
              ); // Hiển thị hộp thoại xác nhận
              if (isConfirmed) {
                handleDelete(id); // Nếu người dùng nhấn "OK", thực hiện việc xóa
              }
            }}
          >
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
  };

  async function handleSubmit(values) {
    console.log(values);
    // quăng data cho BE

    // upload ảnh lên trước
    if (fileList.length > 0) {
      const file = fileList[0];
      console.log(file);
      const url = await upLoadFile(file.originFileObj); //up file ảnh gốc lên firebase
      values.image_path = url;
    }
    try {
      setSubmitting(true);
      await axios.post(api, values);
      toast.success("Successfully create a new tour");
      setOpenModal(false);
      form.resetFields();
      //fetchStudent(); // lấy dữ liệu từ BE và hiển thị lại
      setTours([...tours, values]);
    } catch {
      toast.error("Fail to create tour");
    } finally {
      setSubmitting(false);
      setOpenModal(false);
      form.resetFields();
    }
  }

  const handleDelete = async (tour_ID) => {
    try {
      setSubmitting(true);
      const response = await axios.delete(`${api}/${tour_ID}`);
      console.log(response);
      toast.success("Successfully delete a tour");
      //fetchStudent(); // lấy dữ liệu từ BE và hiển thị lại
      fetchStudent();
    } catch {
      toast.error("Failed to delete a tour");
    } finally {
      setSubmitting(false);
    }
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

  return (
    <div>
      <h1>Student Management</h1>
      <Button onClick={handleOpenModal}>Create a tour</Button>

      <Table columns={columns} dataSource={tours}></Table>

      <Modal open={openModal} onCancel={handleCloseModal} onOk={handleOk}>
        {/* biến form đại diện cho Form */}
        <Form form={form} onFinish={handleSubmit}>
          <Form.Item
            label="Mã tour"
            name="tour_ID"
            rules={[
              {
                required: true,
                message: "Please input tour_ID",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Tên tour"
            name="tourName"
            rules={[
              {
                required: true,
                message: "Please input tên tour",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Giá"
            name="price"
            rules={[
              {
                required: true,
                message: "Please input price!",
              },
              {
                type: "number",
              },
            ]}
          >
            <InputNumber min={0} controls={false} style={{ width: "100%" }} />
          </Form.Item>
          <Form.Item
            label="Ngày khởi hành"
            name="startDate"
            rules={[
              {
                required: true,
                message: "Please input start date!",
              },
            ]}
            getValueProps={(value) => ({ value: value ? dayjs(value) : null })}
          >
            <DatePicker format="DD-MM-YYYY" />
          </Form.Item>
          <Form.Item
            label="Số lượng"
            name="quantity"
            rules={[
              {
                required: true,
                message: "Please input quantity!",
              },
            ]}
          >
            <InputNumber min={0} controls={false} style={{ width: "100%" }} />
          </Form.Item>
          <Form.Item
            label="Type"
            name="type"
            rules={[
              {
                required: true,
                message: "Please input type",
              },
            ]}
          >
            <Select placeholder="Select a type">
              {Array.from({ length: 2 }, (_, i) => (
                <Select.Option key={i} value={i}>
                  {i}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="Image" name={"image_path"}>
            <Upload
              action="https://660d2bd96ddfa2943b33731c.mockapi.io/api/upload"
              listType="picture-card"
              fileList={fileList}
              onPreview={handlePreview}
              onChange={handleChange}
            >
              {fileList.length >= 8 ? null : uploadButton}
            </Upload>
          </Form.Item>
        </Form>
      </Modal>

      {previewImage && (
        <Image
          wrapperStyle={{
            display: "none",
          }}
          preview={{
            visible: previewOpen,
            onVisibleChange: (visible) => setPreviewOpen(visible),
            afterOpenChange: (visible) => !visible && setPreviewImage(""),
          }}
          src={previewImage}
        />
      )}
    </div>
  );
}

export default TourManagement;
