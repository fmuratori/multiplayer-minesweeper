import {
    createBrowserRouter,
  } from "react-router-dom";
import SessionsList from "../views/SessionsList";
import CreateSession from "../views/CreateSession";
import UserSession from "../views/UserSession";
import LoadingGame from "../views/LoadingGame";
import Game from "../views/Game";
import App from "../App";

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: "/sessions",
        element: <SessionsList />,
      },
      {
        path: "/create-session",
        element: <CreateSession />,
      },
      {
        path: "/session",
        element: <UserSession />,
      },
      {
        path: "/loading",
        element: <LoadingGame />,
      },
      {
        path: "/game",
        element: <Game />,
      },
    ]
  },
  
  ]);

export default router;