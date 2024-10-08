// CustomerRequest.js
import React, { useState } from "react";
import {
  Form,
  Input,
  Button,
  Select,
  Card,
  DatePicker,
  notification,
} from "antd";
import moment from "moment";

const { Option } = Select;
const { RangePicker } = DatePicker;

const CustomerRequestPage = () => {
  // State to store form data
  const [requestData, setRequestData] = useState(null);

  // Handle form submission
  const onFinish = (values) => {
    setRequestData(values);
    notification.success({
      message: "Request Sent",
      description: "Your request has been successfully submitted!",
    });
    console.log("Customer Request Data Submitted:", values);
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100%",
      }}
    >
      <Card
        title="Customer Tour Request"
        bordered={false}
        style={{ width: 500 }}
      >
        <Form
          layout="vertical"
          onFinish={onFinish} // Form submission handler
        >
          <Form.Item
            label="Customer Name"
            name="customerName"
            rules={[
              { required: true, message: "Please input the customer's name!" },
            ]}
          >
            <Input />
          </Form.Item>

          {/* Multiple Farm Selection */}
          <Form.Item
            label="Select Farms"
            name="farms"
            rules={[
              { required: true, message: "Please select at least one farm!" },
            ]}
          >
            <Select
              mode="multiple" // Allow multiple selections
              placeholder="Select farms for the tour"
              style={{ width: "100%" }}
            >
              <Option value="farm1">Farm 1</Option>
              <Option value="farm2">Farm 2</Option>
              <Option value="farm3">Farm 3</Option>
              <Option value="farm4">Farm 4</Option>
            </Select>
          </Form.Item>

          {/* Multiple Koi Fish Breeds Selection */}
          <Form.Item
            label="Select Koi Fish Breeds"
            name="koiBreeds"
            rules={[
              {
                required: true,
                message: "Please select at least one Koi fish breed!",
              },
            ]}
          >
            <Select
              mode="multiple" // Allow multiple selections
              placeholder="Select Koi fish breeds"
              style={{ width: "100%" }}
            >
              <Option value="kohaku">Kohaku</Option>
              <Option value="showa">Showa</Option>
              <Option value="sanke">Sanke</Option>
              <Option value="utsurimono">Utsurimono</Option>
            </Select>
          </Form.Item>

          {/* Date Picker for Tour */}
          <Form.Item
            label="Preferred Tour Date"
            name="tourDate"
            rules={[
              {
                required: true,
                message: "Please select the preferred tour date!",
              },
            ]}
          >
            <RangePicker
              style={{ width: "100%" }}
              disabledDate={(current) =>
                current && current < moment().endOf("day")
              }
            />
          </Form.Item>

          <Form.Item label="Additional Requests" name="additionalRequests">
            <Input.TextArea
              rows={4}
              placeholder="Add any additional requests here..."
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              Submit Request
            </Button>
          </Form.Item>
        </Form>

        {/* Optionally display the submitted request data */}
        {requestData && (
          <div style={{ marginTop: 20 }}>
            <h3>Request Summary:</h3>
            <p>
              <strong>Customer Name:</strong> {requestData.customerName}
            </p>
            <p>
              <strong>Selected Farms:</strong> {requestData.farms.join(", ")}
            </p>
            <p>
              <strong>Selected Koi Breeds:</strong>{" "}
              {requestData.koiBreeds.join(", ")}
            </p>
            <p>
              <strong>Preferred Tour Date:</strong>{" "}
              {requestData.tourDate &&
                `${requestData.tourDate[0].format(
                  "YYYY-MM-DD"
                )} to ${requestData.tourDate[1].format("YYYY-MM-DD")}`}
            </p>
            <p>
              <strong>Additional Requests:</strong>{" "}
              {requestData.additionalRequests}
            </p>
          </div>
        )}
      </Card>
    </div>
  );
};

export default CustomerRequestPage;
