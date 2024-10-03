import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "./Login";

test("renders login form elements", () => {
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

  // Check for the Username input and its label
  const usernameLabel = screen.getByLabelText(/username/i);
  expect(usernameLabel).toBeInTheDocument();
  const usernameInput = screen.getByRole("textbox", { name: /username/i });
  expect(usernameInput).toBeInTheDocument();

  // Check for the Password input and its label
  const passwordLabel = screen.getByLabelText(/password/i); // Use lowercase to match the label
  expect(passwordLabel).toBeInTheDocument();
  const passwordInput = screen.getByLabelText(/password/i);
  expect(passwordInput).toBeInTheDocument();

  // Check for the Login button
  const buttonElement = screen.getByRole("button", { name: /login/i });
  expect(buttonElement).toBeInTheDocument();
});

test("successful login", async () => {
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

  const usernameInput = screen.getByRole("textbox", { name: /username/i });
  const passwordInput = screen.getByLabelText(/password/i);
  const loginButton = screen.getByRole("button", { name: /login/i });

  // Simulate valid user input
  fireEvent.change(usernameInput, { target: { value: "validUsername" } });
  fireEvent.change(passwordInput, { target: { value: "validPassword" } });

  // Mock fetch to simulate successful login
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () =>
        Promise.resolve([
          { id: 1, username: "validUsername", token: "testToken" },
        ]),
    })
  );

  fireEvent.click(loginButton);

  const errorMessage = await screen.findByText(/invalid username or password/i);

  // Assert that the error message is displayed
  expect(errorMessage).toBeInTheDocument();
});

test("unsuccessful login", async () => {
  // Render the component inside MemoryRouter for routing context
  render(
    <MemoryRouter>
      <Login />
    </MemoryRouter>
  );

  // Locate form elements by role and label
  const usernameInput = screen.getByRole("textbox", { name: /username/i });
  const passwordInput = screen.getByLabelText(/password/i);
  const loginButton = screen.getByRole("button", { name: /login/i });

  // Simulate user input for invalid credentials
  fireEvent.change(usernameInput, { target: { value: "invalidUsername" } });
  fireEvent.change(passwordInput, { target: { value: "invalidPassword" } });

  // Mock fetch to simulate unsuccessful login (no user found)
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      json: () => Promise.resolve([]), // Empty array, no user found
    })
  );

  // Click the login button to submit the form
  fireEvent.click(loginButton);

  // Wait for the error message to appear (due to async state updates)
  const errorMessage = await screen.findByText(/invalid username or password/i);

  // Assert that the error message is displayed
  expect(errorMessage).toBeInTheDocument();
});
