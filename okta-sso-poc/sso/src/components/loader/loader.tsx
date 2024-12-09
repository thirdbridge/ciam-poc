import React from "react";
import "./loader.css";

export const Loader = () => {
  return (
    <div className="loader-container">
      <div className="spinner">
        <div className="double-bounce1"></div>
        <div className="double-bounce2"></div>
      </div>
    </div>
  );
};
