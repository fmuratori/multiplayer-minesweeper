import {Link, Outlet} from 'react-router-dom';
import './App.css';
import { useEffect } from 'react';

import {SocketContext, gameSocket} from './scripts/GameSocket'
import {SessionSocketContext, sessionSocket} from './scripts/SessionSocket'

function App() {
  // useEffect(() => {
  //   gameSocket.on('connect', () => {
  //     console.log("connect");
  //   });

  //   gameSocket.on('disconnect', () => {
  //     console.log("disconnect");
  //   });

  //   sessionSocket.on('connect', () => {
  //     console.log("connect");
  //   });

  //   sessionSocket.on('disconnect', () => {
  //     console.log("disconnect");
  //   });

  //   return () => {
  //     gameSocket.off('connect');
  //     gameSocket.off('disconnect');
  //     sessionSocket.off('connect');
  //     sessionSocket.off('disconnect');
  //   };
  // }, []);

  return (
    <div>
      <SessionSocketContext.Provider value={sessionSocket}>
        <SocketContext.Provider value={gameSocket}>
          {/* <div>
            <Link to={`/create-session`}>Create</Link>
            <Link to={`/session`}>Your session</Link>
            <Link to={`/game`}>Gioco</Link>
            <Link to={`/sessions`}>Home</Link>
          </div> */}
          <Outlet />
        </SocketContext.Provider>
      </SessionSocketContext.Provider>
    </div>
  );
}

export default App;