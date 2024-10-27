import React from "react";
import { useLocation } from "react-router-dom";

function useGetParams() {
  const location = useLocation();
  const getParams = (param) => {
    const data = new URLSearchParams(location.search);
    return data.get(param);
  };
  return getParams;
}

export default useGetParams;
