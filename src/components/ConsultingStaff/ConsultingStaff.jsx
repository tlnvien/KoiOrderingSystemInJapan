import React from "react";
import { List, Card } from "antd";

const staffMembers = [
  { id: 1, name: "Alice", expertise: "Koi Breeds" },
  { id: 2, name: "Bob", expertise: "Water Quality" },
  { id: 3, name: "Charlie", expertise: "Tank Setup" },
  { id: 4, name: "David", expertise: "Feeding & Care" },
  { id: 5, name: "Eva", expertise: "Breeding Techniques" },
];

const ConsultingStaff = () => {
  return (
    <List
      grid={{ gutter: 16, column: 1 }}
      dataSource={staffMembers}
      renderItem={(staff) => (
        <List.Item>
          <Card title={staff.name}>
            <p>Expertise: {staff.expertise}</p>
          </Card>
        </List.Item>
      )}
    />
  );
};

export default ConsultingStaff;
