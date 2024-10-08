import { createRoot } from "react-dom/client";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import SaleStaff from "./components/SaleStaff/SaleStaff.jsx";

createRoot(document.getElementById("root")).render(
  <>
    <SaleStaff />
    <ToastContainer />
  </>
);
