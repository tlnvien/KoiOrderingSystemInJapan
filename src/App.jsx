import React from "react";
import { BrowserRouter as Router, Route, Routes, Link } from "react-router-dom";

import { Layout, Menu } from "antd";
import TourManagement from "./components/SaleStaff/TourManagement";
import PersonalPage from "./components/SaleStaff/PersonalPage";

const { Header, Content } = Layout;

const App = () => {
  return (
    <Router>
      <Layout>
        <Header>
          <Menu theme="dark" mode="horizontal">
            <Menu.Item key="1">
              <Link to="/">Tour Management</Link>
            </Menu.Item>
            <Menu.Item key="2">
              <Link to="/personal">Personal Information</Link>
            </Menu.Item>
          </Menu>
        </Header>
        <Content style={{ padding: "20px" }}>
          <Routes>
            <Route path="/" element={<TourManagement />} />
            <Route path="/personal" element={<PersonalPage />} />
          </Routes>
        </Content>
      </Layout>
    </Router>
  );
};

export default App;
