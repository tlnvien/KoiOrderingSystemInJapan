// SendQuoteToManager.js
import React, { useState } from "react";
import {
  Form,
  Input,
  Button,
  Select,
  Card,
  InputNumber,
  notification,
} from "antd";

const { Option } = Select;

const SendQuote = () => {
  // State to store form data
  const [quoteData, setQuoteData] = useState(null);

  // Handle form submission
  const onFinish = (values) => {
    setQuoteData(values);
    notification.success({
      message: "Quote Sent",
      description: "Your quote has been successfully sent to the manager!",
    });
    console.log("Quote Data Sent to Manager:", values);
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
        title="Send Quote to Manager"
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
              placeholder="Select farms to visit"
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
                message: "Please select at least one Koi breed!",
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

          <Form.Item label="Additional Requests" name="additionalRequests">
            <Input.TextArea
              rows={4}
              placeholder="Add any additional requests here..."
            />
          </Form.Item>

          {/* Finalized Price */}
          <Form.Item
            label="Finalized Price (USD)"
            name="finalPrice"
            rules={[
              { required: true, message: "Please input the finalized price!" },
            ]}
          >
            <InputNumber
              min={0}
              formatter={(value) =>
                `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ",")
              }
              parser={(value) => value.replace(/\$\s?|(,*)/g, "")}
              style={{ width: "100%" }}
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              Send Quote
            </Button>
          </Form.Item>
        </Form>

        {/* Optionally display the submitted quote data */}
        {quoteData && (
          <div style={{ marginTop: 20 }}>
            <h3>Quote Summary:</h3>
            <p>
              <strong>Customer Name:</strong> {quoteData.customerName}
            </p>
            <p>
              <strong>Selected Farms:</strong> {quoteData.farms.join(", ")}
            </p>
            <p>
              <strong>Selected Koi Breeds:</strong>{" "}
              {quoteData.koiBreeds.join(", ")}
            </p>
            <p>
              <strong>Additional Requests:</strong>{" "}
              {quoteData.additionalRequests}
            </p>
            <p>
              <strong>Finalized Price:</strong> ${quoteData.finalPrice}
            </p>
          </div>
        )}
      </Card>
    </div>
  );
};

export default SendQuote;
