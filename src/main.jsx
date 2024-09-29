import { createRoot } from "react-dom/client";
import StudentManagement from "./StudentManagement.jsx";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

createRoot(document.getElementById("root")).render(
  <>
    <StudentManagement />
    <ToastContainer />
  </>
);
