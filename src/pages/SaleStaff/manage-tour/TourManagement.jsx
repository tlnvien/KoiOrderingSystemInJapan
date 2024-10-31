import React, { useState, useEffect } from "react";
import api from "../../../config/axios";
import { toast } from "react-toastify";
import {
  Button,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Modal,
  Select,
  Tag,
  Card,
  Image as AntImage,
  Upload,
  Divider,
  Row,
  Col,
} from "antd";
import dayjs from "dayjs";
import { MinusCircleOutlined, PlusOutlined } from "@ant-design/icons";
import TextArea from "antd/es/input/TextArea";
import "./TourManagement.css"; // Đưa file CSS riêng
import { useForm } from "antd/es/form/Form";
import upLoadFile from "../../../utils/file";

function TourManagement() {
  const [datas, setDatas] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [fileList, setFileList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [form] = useForm();
  const token = localStorage.getItem("token");

  const fetchData = async (values) => {
    try {
      if (!token) {
        throw new Error("Token không tồn tại. Vui lòng đăng nhập lại.");
      }

      const response = await api.get("tour", values, {
        headers: {
          Accept: "*/*",
          Authorization: `Bearer ${token}`,
        },
      });
      console.log(response.data);
      setDatas(response.data);
    } catch (error) {
      // Cải thiện cách hiển thị lỗi
      const errorMessage =
        error.response?.data?.message || "Có lỗi xảy ra khi tải dữ liệu.";
      toast.error(errorMessage);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleOpenModal = () => {
    setOpenModal(true);
  };

  const handleCloseModal = () => {
    setOpenModal(false);
    form.resetFields();
    setFileList([]);
  };

  const handleOk = () => {
    form.submit();
  };

  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }
    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
  };

  const handleChange = ({ fileList: newFileList }) => setFileList(newFileList);
  const uploadButton = (
    <button style={{ border: 0, background: "none" }} type="button">
      <PlusOutlined />
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );

  const handleSubmit = async (values) => {
    values.departureDate = dayjs(values.departureDate).format("DD-MM-YYYY");
    values.tourSchedules = values.tourSchedules.map((schedule) => ({
      ...schedule,
      startDate: dayjs(schedule.startDate).format("DD-MM-YYYY"),
      endDate: dayjs(schedule.endDate).format("DD-MM-YYYY"),
    }));
    if (fileList.length > 0) {
      const file = fileList[0];
      const url = await upLoadFile(file.originFileObj);
      values.tourImage = url;
    }
    try {
      setLoading(true);
      if (values.tourId) {
        await api.put(`/tour/${values.tourId}`, values);
        toast.success("Cập nhật tour thành công");
      } else {
        await api.post(`tour?tourType=${values.tourType}`, values, {
          headers: {
            Accept: "*/*",
            Authorization: `Bearer ${token}`,
          },
        });
        toast.success("Tạo tour thành công");
      }
      form.resetFields();
      setFileList([]);
      handleCloseModal(false);
    } catch (error) {
      toast.error(error.response?.data); // Hiển thị lỗi từ API nếu có
      toast.error("Tạo tour mới không thành công"); // Thông báo tùy chỉnh sau đó
    } finally {
      setLoading(false);
      fetchData();
    }
  };

  return (
    <div>
      <h1>Quản lí tour</h1>
      <button className="create-tour-btn" onClick={handleOpenModal}>
        Tạo tour mới
      </button>

      {/* Hiển thị thẻ thay vì bảng */}
      <div className="tour-card-container">
        {datas.length > 0 ? (
          datas.map((tour) => (
            <Card key={tour.tourId} className="tour-card">
              <div className="tour-card-content">
                <div className="image-container">
                  <AntImage
                    src={tour.tourImage}
                    alt={tour.tourName}
                    style={{
                      objectFit: "cover",
                      objectPosition: "center",
                      width: "100%",
                      height: "100%", // Đặt chiều cao 100%
                    }}
                  />
                </div>

                <div className="tour-info">
                  <h3>{tour.tourName}</h3>
                  <p>
                    <strong>Chủ đề:</strong> {tour.description}
                  </p>
                  <p>
                    <strong>Ngày khởi hành:</strong>{" "}
                    {dayjs(tour.departureDate).format("DD-MM-YYYY")}
                  </p>
                  <p>
                    <strong>Số lượng:</strong> {tour.maxParticipants}
                  </p>
                  <p>
                    <strong>Còn lại:</strong> {tour.remainSeat}
                  </p>
                  <p>
                    <strong>Thời lượng:</strong> {tour.duration}
                  </p>
                  <p>
                    <strong>Giá:</strong> {tour.price} VND
                  </p>
                </div>
              </div>

              <div className="tour-schedules">
                <p>
                  <strong>Lịch trình:</strong>
                </p>
                {tour.tourSchedules.map((schedule, index) => (
                  <div key={index}>
                    <Tag color="blue">Farm: {schedule.farmName}</Tag>
                    {schedule.scheduleDescription && (
                      <div className="schedule-description">
                        {schedule.scheduleDescription}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </Card>
          ))
        ) : (
          <p>Không có tour nào để hiển thị.</p>
        )}
      </div>

      <Modal
        open={openModal}
        onCancel={handleCloseModal}
        onOk={handleOk}
        width={1200} // Chỉnh sửa kích thước modal ở đây
      >
        <Form
          form={form}
          onFinish={handleSubmit}
          layout="vertical"
          initialValues={{
            tourSchedules: [],
          }}
        >
          {/* Nội dung modal */}
          <Row gutter={16}>
            <Col span={12}>
              <Divider orientation="left">Thông tin tour</Divider>
              <Form.Item
                label="Tên tour"
                name="tourName"
                rules={[{ required: true, message: "Vui lòng nhập tên tour" }]}
              >
                <Input placeholder="Nhập tên tour" type="text" />
              </Form.Item>

              <Form.Item
                label="Số lượng"
                name="maxParticipants"
                rules={[
                  {
                    required: true,
                    message: "Vui lòng nhập số lượng người tham gia",
                  },
                ]}
              >
                <InputNumber
                  placeholder="Nhập số lượng"
                  type="number"
                  min={0}
                  controls={false}
                />
              </Form.Item>

              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="Ngày khởi hành"
                    name="departureDate"
                    rules={[
                      {
                        required: true,
                        message: "Vui lòng nhập ngày khởi hành",
                      },
                    ]}
                  >
                    <DatePicker placeholder="Nhập ngày" format="DD-MM-YYYY" />
                  </Form.Item>
                </Col>

                <Col span={12}>
                  <Form.Item
                    label="Thời lượng"
                    name="duration"
                    rules={[
                      {
                        required: true,
                        message: "Vui lòng nhập thời lượng của tour",
                      },
                    ]}
                  >
                    <Input placeholder="Nhập thời lượng" type="text" />
                  </Form.Item>
                </Col>
              </Row>

              <Form.Item label="Mô tả" name="description">
                <TextArea
                  placeholder="Description"
                  autoSize={{ minRows: 3, maxRows: 10 }} // Tăng số dòng tối đa
                  style={{ maxHeight: "120px", overflowY: "auto" }} // Thêm chiều cao tối đa và cuộn
                />
              </Form.Item>

              <Form.Item
                label="Nhân viên tư vấn"
                name="consulting"
                rules={[
                  { required: true, message: "Vui lòng nhập nhân viên tư vấn" },
                ]}
              >
                <Input placeholder="Nhập nhân viên tư vấn" type="text" />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Divider orientation="left">Loại tour và giá</Divider>
              <Form.Item
                label="Loại tour"
                name="tourType"
                rules={[{ required: true, message: "Vui lòng nhập loại tour" }]}
              >
                <Select>
                  <Select.Option value="AVAILABLE_TOUR">
                    Tour có sẵn
                  </Select.Option>
                  <Select.Option value="REQUESTED_TOUR">
                    Tour theo yêu cầu
                  </Select.Option>
                </Select>
              </Form.Item>

              <Form.Item
                label="Giá"
                name="price"
                rules={[{ required: true, message: "Vui lòng nhập giá" }]}
              >
                <InputNumber
                  placeholder="Nhập giá"
                  type="number"
                  min={0}
                  controls={false}
                />
              </Form.Item>

              <Form.Item label="Ảnh" name="tourImage">
                <Upload
                  action={`http://localhost:8082/api/tour?tourType=${datas.tourType}`}
                  listType="picture-card"
                  fileList={fileList}
                  onPreview={handlePreview}
                  onChange={handleChange}
                  maxCount={1}
                >
                  {fileList.length >= 1 ? null : uploadButton}
                </Upload>
              </Form.Item>

              <Divider orientation="left">Lịch trình tour</Divider>
              <Form.List name="tourSchedules">
                {(fields, { add, remove }) => (
                  <>
                    {fields.map(({ key, name, fieldKey, ...restField }) => (
                      <div key={key}>
                        <Row
                          gutter={16}
                          align="middle"
                          style={{ marginBottom: 8 }}
                        >
                          <Col span={12}>
                            <Form.Item
                              {...restField}
                              name={[name, "farmId"]}
                              fieldKey={[fieldKey, "farmId"]}
                              rules={[
                                {
                                  required: true,
                                  message: "Please input farm ID!",
                                },
                              ]}
                              label="Farm ID"
                              labelCol={{ span: 24 }}
                            >
                              <Input placeholder="Farm ID" />
                            </Form.Item>
                          </Col>

                          <Col
                            span={2}
                            style={{ display: "flex", alignItems: "center" }}
                          >
                            <MinusCircleOutlined
                              style={{ fontSize: "18px", color: "red" }}
                              onClick={() => remove(name)}
                            />
                          </Col>
                        </Row>

                        <Row gutter={16} style={{ marginBottom: 8 }}>
                          <Col span={24}>
                            <Form.Item
                              {...restField}
                              name={[name, "scheduleDescription"]}
                              fieldKey={[fieldKey, "scheduleDescription"]}
                              rules={[
                                {
                                  required: true,
                                  message: "Please enter a description!",
                                },
                              ]}
                              label="Mô tả lịch trình"
                              labelCol={{ span: 24 }}
                            >
                              <TextArea
                                placeholder="Description"
                                autoSize={{ minRows: 3, maxRows: 10 }} // Tăng số dòng tối đa
                                style={{
                                  maxHeight: "120px",
                                  overflowY: "auto",
                                }} // Thêm chiều cao tối đa và cuộn
                              />
                            </Form.Item>
                          </Col>
                        </Row>
                      </div>
                    ))}

                    <Form.Item>
                      <Button
                        type="dashed"
                        onClick={() => add()}
                        block
                        icon={<PlusOutlined />}
                      >
                        Thêm lịch trình
                      </Button>
                    </Form.Item>
                  </>
                )}
              </Form.List>
            </Col>
          </Row>
        </Form>
      </Modal>

      {previewImage && (
        <AntImage
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
