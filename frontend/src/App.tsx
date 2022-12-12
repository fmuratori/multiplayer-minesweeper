import {Link, Outlet} from 'react-router-dom';
import './App.css';
import { useEffect } from 'react';

import {SocketContext, socket} from './scripts/SocketIOContext'

function App() {
  useEffect(() => {
    socket.on('connect', () => {
      console.log("connect");
    });

    socket.on('disconnect', () => {
      console.log("disconnect");
    });

    return () => {
      socket.off('connect');
      socket.off('disconnect');
    };
  }, []);

  return (
    <div>
      <SocketContext.Provider value={socket}>
        <div>
          <Link to={`/create-session`}>Create</Link>
          <Link to={`/session`}>Your session</Link>
          <Link to={`/game`}>Gioco</Link>
          <Link to={`/sessions`}>Home</Link>
        </div>
        <Outlet />
      </SocketContext.Provider>
    </div>
  );
}

export default App;