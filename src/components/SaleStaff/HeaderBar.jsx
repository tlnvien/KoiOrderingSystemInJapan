// CustomHeader.js
import React from "react";
import { Layout, Typography, Tabs } from "antd";
import "./HeaderBar.css"; // Import the CSS file for styling

const { Header } = Layout;
const { Text } = Typography;
const { TabPane } = Tabs;

const HeaderBar = ({ activeTabKey, onTabChange, title }) => {
  return (
    <Header className="custom-header">
      <div className="header-items">
        <Text className="header-item">{title}</Text>
        <Tabs
          defaultActiveKey={activeTabKey}
          onChange={onTabChange}
          tabBarStyle={{ margin: 0 }}
        >
          <TabPane tab="Tours" key="1" />
          <TabPane tab="Consulting Staff" key="2" />
        </Tabs>
      </div>
    </Header>
  );
};

export default HeaderBar;
