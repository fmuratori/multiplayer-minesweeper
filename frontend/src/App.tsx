import {Outlet} from 'react-router-dom';
import './App.css';
import React from 'react';

function App() {
  return (
    <React.StrictMode>
      <Outlet />
    </React.StrictMode>
  );
}

export default App;