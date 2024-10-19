import React from "react";
import { Outlet } from "react-router-dom";

const ConsultingStaff = () => {
  return (
    <div>
      <h2>Consulting Staff Dashboard</h2>
      <Outlet /> {/* Render nội dung các chức năng của Consulting */}
    </div>
  );
};

export default ConsultingStaff;
