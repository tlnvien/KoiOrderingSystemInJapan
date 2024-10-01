import { createRoot } from "react-dom/client";
import StudentManagement from "./pages/sale-staff/TourManagement.jsx";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { App } from "antd";
import TourManagement from "./pages/sale-staff/TourManagement.jsx";

createRoot(document.getElementById("root")).render(
  <>
    <TourManagement />
    <ToastContainer />
  </>
);
