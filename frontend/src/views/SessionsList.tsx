import { useState, useEffect } from 'react';
import './SessionsList.css';
import moment from "moment";
import {browseSessionsSocket} from '../scripts/SessionSocket'
import { useNavigate } from 'react-router-dom';

function SessionsList() {
  const [sessions, setSessions] = useState([]);
  const navigate = useNavigate();
  
  useEffect(() => {
    browseSessionsSocket.on('connect', () => {
      console.log("SocketIo [BROWSE] - Connect to server-session");
    });
    browseSessionsSocket.on('disconnect', () => {
      console.log("SocketIo [BROWSE] - Disconnect from server-session");
    });
    browseSessionsSocket.on('sessions_update', (data: any) => {
      console.log("SocketIo [BROWSE] - Sessions list update", data);
      setSessions(data["sessions"])
    });
    browseSessionsSocket.on('session_update', (data: any) => {
      console.log("SocketIo [BROWSE] - Session update", data);

      setSessions((sessions) => {
        const idx = sessions.findIndex(s => s.roomId === data.session.roomId);
        if (idx !== -1) {
          if (data.updateType === "REMOVED_USER" || data.updateType === "ADDED_USER") {
            sessions[idx] = data.session; 
          } else if (data.updateType === "CLOSED" || data.updateType === "GAME_STARTING") { 
            sessions = sessions.filter((s) => s.roomId !== data.session.roomId);
          }
        }
        return structuredClone(sessions)
      })
    });

    browseSessionsSocket.open();

    return () => {
      browseSessionsSocket.off('connect');
      browseSessionsSocket.off('disconnect');
      browseSessionsSocket.off('sessions_update');
      browseSessionsSocket.off('session_update');
    };
  }, []);

  function selectSession(roomId: String, sessionName:String) {
    browseSessionsSocket.close();    
    navigate('/session', { state: {roomId: roomId, sessionName: sessionName} });
  }

  return (
    <>
    {
      sessions.map((s, index) => 
        <div className='' key={index}>
          {s.sessionName}, {s.gameMode}, {moment(s.creationDate).calendar()}, {s.numConnectedUsers}
          <button onClick={(e) => selectSession(s.roomId, s.sessionName)}>Join</button>
        </div>
      )
    }
    </>
  );
}

export default SessionsList;
