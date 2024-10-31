// DashboardPage.jsx
import React, { useEffect, useState } from "react";
import { Card, Col, Row, Statistic, DatePicker, message } from "antd";
import axios from "axios";
import Sidebar from "./Admin";
import api from "../../config/axios";

const DashboardPage = () => {
  const [data, setData] = useState({
    toursToday: 0,
    toursThisWeek: 0,
    toursThisMonth: 0,
    toursThisYear: 0,
    balance: 0,
    deliveredOrders: 0,
    ordersToday: 0,
    ordersThisWeek: 0,
    ordersThisMonth: 0,
    ordersThisYear: 0,
  });

  const fetchData = async () => {
    try {
      const [
        todayTours,
        weekTours,
        monthTours,
        yearTours,
        balance,
        deliveredOrders,
        todayOrders,
        weekOrders,
        monthOrders,
        yearOrders,
      ] = await Promise.all([
        api.get("dashboard/countTour/today"),
        api.get("dashboard/countTour/week"),
        api.get("dashboard/countTour/month", {
          params: {
            year: new Date().getFullYear(),
            month: new Date().getMonth() + 1,
          },
        }),
        api.get("dashboard/countTour/year", {
          params: { year: new Date().getFullYear() },
        }),
        api.get("dashboard/balance"),
        api.get("dashboard/countOrders/delivered"),
        api.get("dashboard/countOrders/today"),
        api.get("dashboard/countOrders/week"),
        api.get("dashboard/countOrders/month"),
        api.get("dashboard/countOrders/year"),
      ]);

      setData({
        toursToday: todayTours.data,
        toursThisWeek: weekTours.data,
        toursThisMonth: monthTours.data,
        toursThisYear: yearTours.data,
        balance: balance.data,
        deliveredOrders: deliveredOrders.data,
        ordersToday: todayOrders.data,
        ordersThisWeek: weekOrders.data,
        ordersThisMonth: monthOrders.data,
        ordersThisYear: yearOrders.data,
      });
    } catch (error) {
      message.error("Failed to load dashboard data.");
      console.error("Error fetching dashboard data:", error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleMonthChange = async (date) => {
    const year = date.year();
    const month = date.month() + 1;
    try {
      const response = await api.get("dashboard/countTour/month", {
        params: { year, month },
      });
      setData((prev) => ({ ...prev, toursThisMonth: response.data }));
    } catch (error) {
      message.error("Failed to load monthly tour data.");
    }
  };

  const handleYearChange = async (date) => {
    const year = date.year();
    try {
      const response = await api.get("dashboard/countTour/year", {
        params: { year },
      });
      setData((prev) => ({ ...prev, toursThisYear: response.data }));
    } catch (error) {
      message.error("Failed to load yearly tour data.");
    }
  };

  return (
    <div className="admin">
      <Sidebar />
      <div className="admin-content">
        <Row gutter={16}>
          <Col span={8}>
            <Card>
              <Statistic title="Tours Today" value={data.toursToday} />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="Tours This Week" value={data.toursThisWeek} />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="Tours This Month" value={data.toursThisMonth} />
              <DatePicker
                onChange={handleMonthChange}
                picker="month"
                style={{ marginTop: 8 }}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginTop: 20 }}>
          <Col span={8}>
            <Card>
              <Statistic title="Tours This Year" value={data.toursThisYear} />
              <DatePicker
                onChange={handleYearChange}
                picker="year"
                style={{ marginTop: 8 }}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="System Balance"
                value={`$${data.balance.toFixed(2)}`}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="Delivered Orders"
                value={data.deliveredOrders}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginTop: 20 }}>
          <Col span={8}>
            <Card>
              <Statistic title="Orders Today" value={data.ordersToday} />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic title="Orders This Week" value={data.ordersThisWeek} />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="Orders This Month"
                value={data.ordersThisMonth}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginTop: 20 }}>
          <Col span={8}>
            <Card>
              <Statistic title="Orders This Year" value={data.ordersThisYear} />
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default DashboardPage;
