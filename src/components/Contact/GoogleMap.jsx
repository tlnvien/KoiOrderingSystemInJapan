import React from "react";
import { GoogleMap, LoadScript, Marker } from "@react-google-maps/api";

const GoogleMapComponent = () => {
  const mapContainerStyle = {
    width: "100%",
    height: "400px",
  };

  // Tọa độ vị trí của công ty
  const center = {
    lat: 10.875089, // Vĩ độ
    lng: 106.80067, // Kinh độ
  };

  return (
    <LoadScript googleMapsApiKey="AIzaSyD7AceOYz7BiHRdnr0XIyzZZz83I1ii_LQ">
      <GoogleMap
        mapContainerStyle={mapContainerStyle}
        center={center}
        zoom={15}
      >
        <Marker position={center} />
      </GoogleMap>
    </LoadScript>
  );
};

export default GoogleMapComponent;
