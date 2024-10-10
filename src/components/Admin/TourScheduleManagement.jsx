import React, { useState, useEffect } from "react";
import axios from "axios";
import { Button, Table, Modal, Form, Input, DatePicker, Select } from "antd";
import moment from "moment";

const { RangePicker } = DatePicker;
const TourScheduleManagement = () => {
  const [tourSchedules, setTourSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editData, setEditData] = useState(null);
  const [tours, setTours] = useState([]);
  const [farms, setFarms] = useState([]);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchTourSchedules();
    fetchTours();
    fetchFarms();
  }, []);

  const apiUrl = "http://localhost:8080/api/tour/schedule/all";
  const tourApi = "http://localhost:8080/api/tour";
  const farmApi = "http://localhost:8080/api/farm";
  const token =
    "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMSIsImlhdCI6MTcyODMyMTk4OSwiZXhwIjoxNzI4NDA4Mzg5fQ.YG8AFw5VhUM3iHlINXqO3waYcdKHlXQcpHx2ouXoWlA";

  const fetchTourSchedules = async () => {
    try {
      const response = await axios.get(apiUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setTourSchedules(response.data);
      setLoading(false);
    } catch (error) {
      console.error("Error fetching tour schedules:", error);
    }
  };

  const fetchTours = async () => {
    try {
      const response = await axios.get(tourApi, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setTours(response.data);
    } catch (error) {
      console.error("Error fetching tours:", error);
    }
  };

  const fetchFarms = async () => {
    try {
      const response = await axios.get(farmApi, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setFarms(response.data);
    } catch (error) {
      console.error("Error fetching farms:", error);
    }
  };

  const handleAddEdit = (data) => {
    setEditData(data);
    setIsModalVisible(true);
    form.setFieldsValue({
      tour: data ? data.tour.id : null,
      farm: data ? data.farm.id : null,
      dateRange: data ? [moment(data.startDate), moment(data.endDate)] : [],
    });
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`${apiUrl}/${tourID}`);
      fetchTourSchedules();
    } catch (error) {
      console.error("Error deleting tour schedule:", error);
    }
  };

  const handleOk = async () => {
    try {
      const values = form.getFieldsValue();
      const payload = {
        tourId: values.tour,
        farmId: values.farm,
        startDate: values.dateRange[0].toISOString(),
        endDate: values.dateRange[1].toISOString(),
      };

      if (editData) {
        await axios.put(`/api/tour-schedules/${editData.id}`, payload);
      } else {
        await axios.post("/api/tour-schedules", payload);
      }

      fetchTourSchedules();
      setIsModalVisible(false);
      setEditData(null);
      form.resetFields();
    } catch (error) {
      console.error("Error saving tour schedule:", error);
    }
  };

  const handleCancel = () => {
    setIsModalVisible(false);
    setEditData(null);
    form.resetFields();
  };

  const columns = [
    {
      title: "Tour",
      dataIndex: ["tour", "name"],
      key: "tour",
    },
    {
      title: "Farm",
      dataIndex: ["farm", "name"],
      key: "farm",
    },
    {
      title: "Start Date",
      dataIndex: "startDate",
      key: "startDate",
      render: (startDate) => moment(startDate).format("YYYY-MM-DD HH:mm"),
    },
    {
      title: "End Date",
      dataIndex: "endDate",
      key: "endDate",
      render: (endDate) => moment(endDate).format("YYYY-MM-DD HH:mm"),
    },
    {
      title: "Actions",
      key: "actions",
      render: (text, record) => (
        <>
          <Button onClick={() => handleAddEdit(record)} type="link">
            Edit
          </Button>
          <Button onClick={() => handleDelete(record.id)} type="link" danger>
            Delete
          </Button>
        </>
      ),
    },
  ];

  return (
    <div>
      <Button
        type="primary"
        onClick={() => handleAddEdit(null)}
        style={{ marginBottom: "16px" }}
      >
        Add Tour Schedule
      </Button>
      <Table
        columns={columns}
        dataSource={tourSchedules}
        loading={loading}
        rowKey="id"
      />

      <Modal
        title={editData ? "Edit Tour Schedule" : "Add Tour Schedule"}
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="tour"
            label="Tour"
            rules={[{ required: true, message: "Please select a tour!" }]}
          >
            <Select placeholder="Select Tour">
              {tours.map((tour) => (
                <Select.Option key={tour.id} value={tour.id}>
                  {tour.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="farm"
            label="Farm"
            rules={[{ required: true, message: "Please select a farm!" }]}
          >
            <Select placeholder="Select Farm">
              {farms.map((farm) => (
                <Select.Option key={farm.id} value={farm.id}>
                  {farm.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="dateRange"
            label="Date Range"
            rules={[{ required: true, message: "Please select date range!" }]}
          >
            <RangePicker showTime />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default TourScheduleManagement;
