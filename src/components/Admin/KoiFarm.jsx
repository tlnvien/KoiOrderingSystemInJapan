import React, { useEffect, useState } from "react";
import { Form, Input, Button, Select, List, message } from "antd";
import axios from "axios";
import Sidebar from "./Admin";
import api from "../../config/axios";

const { Option } = Select;

const KoiFarm = () => {
  const [farms, setFarms] = useState([]);
  const [kois, setKois] = useState([]);
  const [selectedFarm, setSelectedFarm] = useState("");
  const [species, setSpecies] = useState("");

  // Get token from localStorage
  const token = localStorage.getItem("token"); // Adjust key if needed

  // Fetch farms when component mounts
  useEffect(() => {
    fetchFarms();
  }, []);

  // Function to fetch all farms
  const fetchFarms = async () => {
    try {
      const response = await api.get("farm/list", {
        headers: { Authorization: `Bearer ${token}` }, // Add token here
      });
      if (Array.isArray(response.data)) {
        setFarms(response.data);
      } else {
        console.error("Unexpected data format:", response.data);
        message.error("Data không đúng định dạng.");
        setFarms([]); // Set to an empty array if the format is incorrect
      }
    } catch (error) {
      console.error("Error fetching farms:", error);
      message.error("Không thể fetch các trang trại.");
      setFarms([]); // Set to an empty array if there's an error
    }
  };

  // Function to fetch all koi in the selected farm
  const fetchKoisByFarmId = async (farmId) => {
    try {
      const response = await api.get(`koiFarm/listKoi/${farmId}`, {
        headers: { Authorization: `Bearer ${token}` }, // Add token here
      });
      setKois(response.data);
    } catch (error) {
      console.error("Error fetching kois:", error);
      message.error("Không thể fetch kois cho trang trại này.");
    }
  };

  // Handle farm selection change
  const handleFarmChange = (value) => {
    setSelectedFarm(value);
    fetchKoisByFarmId(value);
  };

  // Function to add a koi to the selected farm
  const handleAddKoi = async (values) => {
    const { species } = values;
    if (!selectedFarm || !species) {
      message.error("Vui lòng chọn một trang trại và nhập loài koi.");
      return;
    }
    try {
      await api.post(`koiFarm/${selectedFarm}`, null, {
        headers: { Authorization: `Bearer ${token}` }, // Add token here
        params: { species },
      });
      message.success("Koi đã được thêm vào trang trại thành công!");
      fetchKoisByFarmId(selectedFarm); // Refresh koi list
    } catch (error) {
      console.error("Error adding koi:", error);
      message.error("Không thể thêm koi vào trang trại.");
    }
  };

  // Function to delete a koi from the selected farm
  const handleDeleteKoi = async (species) => {
    try {
      await api.delete(`koiFarm/koi/${selectedFarm}`, {
        headers: { Authorization: `Bearer ${token}` }, // Add token here
        params: { species },
      });
      message.success("Koi đã được xóa khỏi trang trại thành công!");
      fetchKoisByFarmId(selectedFarm); // Refresh koi list
    } catch (error) {
      console.error("Error deleting koi:", error);
      message.error("Không thể xóa koi khỏi trang trại.");
    }
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <h1>Quản lý liên kết Koi với trang trại</h1>
        <Form onFinish={handleAddKoi}>
          <Form.Item label="Chọn trang trại">
            <Select onChange={handleFarmChange} style={{ width: 200 }}>
              {farms &&
                Array.isArray(farms) &&
                farms.map((farm) => (
                  <Option key={farm.farmId} value={farm.farmId}>
                    {farm.farmName}
                  </Option>
                ))}
            </Select>
          </Form.Item>
          <Form.Item label="Loài Koi" name="species">
            <Input
              value={species}
              onChange={(e) => setSpecies(e.target.value)}
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Thêm Koi
            </Button>
          </Form.Item>
        </Form>
        <h2>Kois trong trang trại</h2>
        <List
          bordered
          dataSource={kois}
          renderItem={(item) => (
            <List.Item
              key={item.species}
              actions={[
                <Button
                  key={`delete-${item.species}`}
                  danger
                  onClick={() => handleDeleteKoi(item.species)}
                >
                  Xóa
                </Button>,
              ]}
            >
              {item.species}
            </List.Item>
          )}
        />
      </div>
    </div>
  );
};

export default KoiFarm;
