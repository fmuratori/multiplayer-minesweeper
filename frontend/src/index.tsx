import ReactDOM from 'react-dom/client';
import {RouterProvider} from "react-router-dom";
import './index.css';
import 'bootstrap/dist/css/bootstrap.css';
import "bootstrap-icons/font/bootstrap-icons.css";

import router from "./router/root";

ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
).render(
  <RouterProvider router={router} />
);