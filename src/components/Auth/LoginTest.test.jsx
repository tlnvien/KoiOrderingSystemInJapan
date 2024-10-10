import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { BrowserRouter } from "react-router-dom";
import LoginTest from "./LoginTest";
import React from "react";

// Mock useNavigate from react-router-dom để kiểm tra điều hướng
const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockNavigate, // Sử dụng mockNavigate cho việc kiểm tra điều hướng
}));

// Mock localStorage để kiểm tra việc lưu trữ token
beforeEach(() => {
  Storage.prototype.setItem = jest.fn(); // Thay thế phương thức setItem trong localStorage bằng một hàm mock
});

// Bắt đầu một nhóm các test case cho component LoginTest
describe("LoginTest Component", () => {
  // Mocking the fetch request trước mỗi test case
  beforeEach(() => {
    global.fetch = jest.fn(); // Thay thế phương thức fetch toàn cục bằng một hàm mock
    // Mock window.alert để tránh hiển thị hộp thoại
    jest.spyOn(window, "alert").mockImplementation(() => {});
  });

  // Reset mocks sau mỗi test case
  afterEach(() => {
    jest.resetAllMocks(); // Đặt lại tất cả các mock
    // Khôi phục window.alert về trạng thái ban đầu
    window.alert.mockRestore();
  });

  // Test 1: Kiểm tra hành vi khi đăng nhập thành công
  test("displays 'Login successful!' and navigates to homepage on successful login", async () => {
    // Mock phản hồi thành công từ API
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => [
        {
          username: "correctUser", // Tên người dùng đúng
          password: "correctPassword", // Mật khẩu đúng
          token: "12345", // Token giả được trả về
        },
      ],
    });
    render(
      <BrowserRouter>
        <LoginTest />
      </BrowserRouter>
    );
    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: "correctUser" },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: "correctPassword" },
    });
    // Click vào nút login
    fireEvent.click(screen.getByRole("button", { name: /login/i }));
    // Kiểm tra kết quả sau khi thực hiện thao tác
    await waitFor(() => {
      // Đảm bảo không có thông báo lỗi nào xuất hiện
      expect(screen.queryByRole("alert")).not.toBeInTheDocument();
      // Kiểm tra xem alert thông báo thành công đã được gọi
      expect(window.alert).toHaveBeenCalledWith("Login successful!");
      // Kiểm tra xem token đã được lưu vào localStorage
      expect(localStorage.setItem).toHaveBeenCalledWith("token", "12345");
      // Kiểm tra xem có điều hướng tới trang chính hay không
      expect(mockNavigate).toHaveBeenCalledWith("/");
    });
  });

  // Test 2: Kiểm tra hành vi khi đăng nhập không thành công
  test("displays 'Invalid username or password' on failed login", async () => {
    // Mock phản hồi không thành công từ API
    fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => [
        { username: "user1", password: "password1" },
        { username: "user2", password: "password2" },
      ],
    });
    render(
      <BrowserRouter>
        <LoginTest />
      </BrowserRouter>
    );
    fireEvent.change(screen.getByLabelText(/username/i), {
      target: { value: "wrongUser" },
    });
    fireEvent.change(screen.getByLabelText(/password/i), {
      target: { value: "wrongPassword" },
    });
    // Click vào nút login
    fireEvent.click(screen.getByRole("button", { name: /login/i }));
    // Kiểm tra kết quả sau khi thực hiện thao tác
    await waitFor(() => {
      // Kiểm tra xem thông báo lỗi đã xuất hiện
      expect(screen.getByRole("alert")).toHaveTextContent(
        "Invalid username or password"
      );
      // Đảm bảo không có điều hướng nào xảy ra
      expect(mockNavigate).not.toHaveBeenCalled();
    });
  });
});

// Test 3: Kiểm tra xem các thành phần username, 
//password và nút login có hiển thị trong trang đăng nhập không
test("renders username, password, and login button on the login page", () => {
  // Render component LoginTest trong ngữ cảnh của BrowserRouter
  render(
    <BrowserRouter>
      <LoginTest />
    </BrowserRouter>
  );

  // Kiểm tra rằng input username có mặt trong tài liệu
  expect(screen.getByLabelText(/username/i)).toBeInTheDocument();

  // Kiểm tra rằng input password có mặt trong tài liệu
  expect(screen.getByLabelText(/password/i)).toBeInTheDocument();

  // Kiểm tra rằng nút login có mặt trong tài liệu
  expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
});
