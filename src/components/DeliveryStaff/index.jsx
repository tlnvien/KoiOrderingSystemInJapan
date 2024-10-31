import React from "react";
import { Outlet } from "react-router-dom";

function DeliveryStaff() {
  return (
    <div>
      <h2>Delivery Staff Dashboard</h2>
      <Outlet /> {/* Nơi chứa các route con */}
    </div>
  );
}

export default DeliveryStaff;
