import React from "react";
import { Outlet } from "react-router-dom"; // Outlet sẽ hiển thị nội dung các chức năng con

const SaleStaff = () => {
  return (
    <div>
      <h2>Sale Staff Dashboard</h2>
      <Outlet /> {/* Nơi chứa các route con */}
    </div>
  );
};

export default SaleStaff;
