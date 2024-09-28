import { Button, Form, Input, InputNumber, Modal, Select, Table } from "antd";
import { useForm } from "antd/es/form/Form";
import axios from "axios";
import { useEffect, useState } from "react";

function StudentManagement() {
  // Quản lí sinh viên (CRUD)
  // Tạo, sửa, xóa sinh viên, lưu trữ và hiển thị danh sách sinh viên
  const [students, setStudents] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [form] = useForm();

  const api = "https://66e79651b17821a9d9d95a2b.mockapi.io/Student"; // biến lưu trữ api

  const fetchStudent = async () => {
    // function lấy data từ BE => fetch...
    // lấy thông tin từ api => axios(thư viện giúp call api)

    const response = await axios.get(api);
    // js gọi là promise => function bất đồng bộ => cần thời gian để thực hiện
    // await: đợi tới khi mà api trả về kết quả

    setStudents(response.data);
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
      title: "ID",
      dataIndex: "id", // đặt theo tên biến từ BE
      key: "id",
    },
    {
      title: "Name",
      dataIndex: "Name", // đặt theo tên biến từ BE
      key: "Name",
    },
    {
      title: "Code",
      dataIndex: "Code", // đặt theo tên biến từ BE
      key: "code",
    },
    {
      title: "Score",
      dataIndex: "Score", // đặt theo tên biến từ BE
      key: "Score",
    },
    {
      title: "Subject",
      dataIndex: "Subject", // đặt theo tên biến từ BE
      key: "Subject",
    },
  ];

  const handleOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
  };

  const handleSubmit = async (values) => {
    console.log(values);

    // quăng data cho BE
    try {
      const response = await axios.post(api, values);
      alert("Successfully create a new student");
    } catch (error) {
      console.log(error);
    }
    setOpenModal(false);

    //fetchStudent(); // lấy dữ liệu từ BE và hiển thị lại
    setStudents([...students, values]);
  };

  const handleOk = () => {
    form.submit();
  };

  return (
    <div>
      <h1>Student Management</h1>
      <Button onClick={handleOpenModal}>Create a new student</Button>

      <Table columns={columns} dataSource={students}></Table>

      <Modal
        title="Create new student"
        open={openModal}
        onCancel={handleCloseModal}
        onOk={handleOk}
      >
        {/* biến form đại diện cho Form */}
        <Form form={form} onFinish={handleSubmit}>
          <Form.Item
            label="Student name"
            name="Name"
            rules={[
              {
                required: true,
                message: "Please input student name!",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Student code"
            name="Code"
            rules={[
              {
                required: true,
                message: "Please input student code!",
              },
              {
                pattern: /^SE\d{6}$/,
                message:
                  "Student code must start with 'SE' followed by 6 digits (e.g., SE123456)!",
              },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="Student score"
            name="Score"
            rules={[
              {
                required: true,
                message: "Please input student code!",
              },
              {
                type: "number",
                min: 0,
                max: 10,
                message: "Student score must be between 0 and 10!",
              },
            ]}
          >
            <Select placeholder="Select a score">
              {Array.from({ length: 11 }, (_, i) => (
                <Select.Option key={i} value={i}>
                  {i}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="Subject"
            name="Subject"
            rules={[
              {
                required: true,
                message: "Please input subject!",
              },
            ]}
          >
            <Input />
          </Form.Item>
          
        </Form>
      </Modal>
    </div>
  );
}

export default StudentManagement;
