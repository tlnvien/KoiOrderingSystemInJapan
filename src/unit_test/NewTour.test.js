import React from 'react';
import { render, fireEvent, waitFor, screen } from '@testing-library/react';
import axios from 'axios';
import NewTour from './NewTour';
import '@testing-library/jest-dom';

jest.mock('axios');

describe('NewTour Component', () => {
  beforeEach(() => {
    axios.get.mockResolvedValue({
      data: [
        {
          Tour_ID: "Tour_ID 1",
          Tour_name: "Tour_name 1",
          start_date: 1728271187,
          description: "description 1",
          price: 86,
          id: "1",
        },
        {
          Tour_ID: "Tour_ID 2",
          Tour_name: "Tour_name 2",
          start_date: 1728271127,
          description: "description 2",
          price: 59,
          id: "2",
        },
      ],
    });

    axios.post.mockResolvedValue({
      data: {
        Tour_ID: "Tour_ID 3",
        Tour_name: "Tour_name 3",
        start_date: 1728271187,
        description: "description 3",
        price: 100,
        id: "3",
      },
    });
  });

  test('renders NewTour component', async () => {
    render(<NewTour />);

    expect(screen.getByText('Add New Tour')).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText('Tour_name 1')).toBeInTheDocument();
      expect(screen.getByText('Tour_name 2')).toBeInTheDocument();
    });
  });

  test('adds a new tour', async () => {
    render(<NewTour />);

    fireEvent.click(screen.getByText('Add New Tour'));

    fireEvent.change(screen.getByLabelText('Tour Name'), { target: { value: 'Tour_name 3' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'description 3' } });
    fireEvent.change(screen.getByLabelText('Price'), { target: { value: 100 } });

    fireEvent.click(screen.getByText('Add Tour'));

    await waitFor(() => {
      expect(screen.getByText('Tour_name 3')).toBeInTheDocument();
    });
  });

  test('displays validation errors if form fields are empty', async () => {
    render(<NewTour />);

    // Open the modal
    fireEvent.click(screen.getByText('Add New Tour'));

    // Click "Add Tour" without filling any information
    fireEvent.click(screen.getByText('Add Tour'));

    // Check if the validation errors appear
    await waitFor(() => {
      expect(screen.getByText('Please input tour name!')).toBeInTheDocument();
      expect(screen.getByText('Please input description!')).toBeInTheDocument();
      expect(screen.getByText('Please input price!')).toBeInTheDocument();
    });
  });

  test('displays validation error for invalid price input', async () => {
    render(<NewTour />);

    // Open the modal
    fireEvent.click(screen.getByText('Add New Tour'));

    // Enter invalid price input
    fireEvent.change(screen.getByLabelText('Tour Name'), { target: { value: 'Tour_name 3' } });
    fireEvent.change(screen.getByLabelText('Description'), { target: { value: 'description 3' } });
    fireEvent.change(screen.getByLabelText('Price'), { target: { value: 'invalid-price' } });

    // Click "Add Tour"
    fireEvent.click(screen.getByText('Add Tour'));

    // Ensure the invalid price validation error appears
    await waitFor(() => {
      expect(screen.getByText('Please input a valid price!')).toBeInTheDocument();
    });
  });
});
