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
    completedOrderPayments: 0,
    completedTourPayments: 0,
    completedDeliveringPayments: 0,
  });

  const fetchData = async () => {
    const currentDate = new Date();
    const currentMonth = currentDate.getMonth() + 1; // Month is 0-based
    const currentYear = currentDate.getFullYear();

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
        completedOrderPayments,
        completedTourPayments,
        completedDeliveringPayments,
      ] = await Promise.all([
        api.get("dashboard/countTour/today"),
        api.get("dashboard/countTour/week"),
        api.get("dashboard/countTour/month", {
          params: {
            year: currentYear,
            month: currentMonth,
          },
        }),
        api.get("dashboard/countTour/year", {
          params: { year: currentYear },
        }),
        api.get("dashboard/balance"),
        api.get("dashboard/countOrders/delivered"),
        api.get("dashboard/countOrders/today"),
        api.get("dashboard/countOrders/week"),
        api.get("dashboard/countOrders/month", {
          params: {
            year: currentYear,
            month: currentMonth,
          },
        }),
        api.get("dashboard/countOrders/year", {
          params: { year: currentYear },
        }),
        api.get("dashboard/count/completed/orders"),
        api.get("dashboard/count/completed/tour"),
        api.get("dashboard/count/completed/delivering"),
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
        completedOrderPayments: completedOrderPayments.data,
        completedTourPayments: completedTourPayments.data,
        completedDeliveringPayments: completedDeliveringPayments.data,
      });
    } catch (error) {
      message.error("Failed to load dashboard data.");
      console.error("Error fetching dashboard data:", error);
    }
  };

  const handleMonthChange = async (date) => {
    if (!date) return;
    const year = date.year();
    const month = date.month() + 1;
    try {
      const response = await api.get("dashboard/countTour/month", {
        params: { year, month },
      });
      setData((prev) => ({ ...prev, toursThisMonth: response.data }));
    } catch (error) {
      message.error("Failed to load monthly tour data.");
      console.error("Error fetching monthly tour data:", error);
    }
  };

  const handleYearChange = async (date) => {
    if (!date) return; // Guard clause for null date
    const year = date.year();
    try {
      const response = await api.get("dashboard/countTour/year", {
        params: { year },
      });
      setData((prev) => ({ ...prev, toursThisYear: response.data }));
    } catch (error) {
      message.error("Failed to load yearly tour data.");
      console.error("Error fetching yearly tour data:", error);
    }
  };

  const handleOrderMonthChange = async (date) => {
    if (!date) return;
    const year = date.year();
    const month = date.month() + 1;
    try {
      const response = await api.get("dashboard/countOrders/month", {
        params: { year, month },
      });
      setData((prev) => ({ ...prev, ordersThisMonth: response.data }));
    } catch (error) {
      message.error("Failed to load monthly order data.");
      console.error("Error fetching monthly order data:", error);
    }
  };

  const handleOrderYearChange = async (date) => {
    if (!date) return;
    const year = date.year();
    try {
      const response = await api.get("dashboard/countOrders/year", {
        params: { year },
      });
      setData((prev) => ({ ...prev, ordersThisYear: response.data }));
    } catch (error) {
      message.error("Failed to load yearly order data.");
      console.error("Error fetching yearly order data:", error);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

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
                value={new Intl.NumberFormat("vi-VN", {
                  style: "currency",
                  currency: "VND",
                }).format(data.balance)}
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
              <DatePicker
                onChange={handleOrderMonthChange}
                picker="month"
                style={{ marginTop: 8 }}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginTop: 20 }}>
          <Col span={8}>
            <Card>
              <Statistic title="Orders This Year" value={data.ordersThisYear} />
              <DatePicker
                onChange={handleOrderYearChange}
                picker="year"
                style={{ marginTop: 8 }}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="Completed Order Payments"
                value={data.completedOrderPayments}
              />
            </Card>
          </Col>
          <Col span={8}>
            <Card>
              <Statistic
                title="Completed Tour Payments"
                value={data.completedTourPayments}
              />
            </Card>
          </Col>
        </Row>

        <Row gutter={16} style={{ marginTop: 20 }}>
          <Col span={8}>
            <Card>
              <Statistic
                title="Completed Delivering Payments"
                value={data.completedDeliveringPayments}
              />
            </Card>
          </Col>
        </Row>
      </div>
    </div>
  );
};

export default DashboardPage;
