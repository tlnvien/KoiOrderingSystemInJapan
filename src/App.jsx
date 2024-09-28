import { useState } from "react";

const App = () => {
  const [number, setNumber] = useState(10);

  const handleAddNumber = () => {
    setNumber(number + 1);
  };

  return (
    <div>  
      <h1>Number: {number}</h1>
      <button onClick={handleAddNumber}>Add</button>
    </div>
  );
};

export default App;
