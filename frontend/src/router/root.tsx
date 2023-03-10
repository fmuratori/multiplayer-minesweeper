import {
    createBrowserRouter,
  } from "react-router-dom";
import SessionsList from "../views/SessionsList";
import WaitingStart from "../views/WaitingStart";
import Game from "../views/Game";
import App from "../App";

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: "/",
        element: <SessionsList />,
      },
      {
        path: "/session",
        element: <WaitingStart />,
      },
      {
        path: "/game",
        element: <Game />,
      }
    ]
  },
  
  ]);

export default router;