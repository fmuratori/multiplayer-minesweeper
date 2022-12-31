import { useState, useEffect } from 'react';
import './SessionsList.css';
import SessionItem from '../components/SessionItem';
import {browseSessionsSocket} from '../scripts/SessionSocket'
import { useNavigate } from 'react-router-dom';
import GameModeButton from "../components/GameModeButton";
import {postNewSession} from "../scripts/api";

const GAME_MODES = [
  {
    'id': 'SMALL',
    'name': 'Facile',
    'numMaxPlayers': 1,
    'gridWidth': 9,
    'gridHeight': 9
  }, {
    'id': 'MEDIUM',
    'name': 'Media',
    'numMaxPlayers': 2,
    'gridWidth': 16,
    'gridHeight': 16
  }, {
    'id': 'BIG',
    'name': 'Difficile',
    'numMaxPlayers': 4,
    'gridWidth': 30,
    'gridHeight': 16
  }
]

function SessionsList() {
  const [sessions, setSessions] = useState([]);
  const navigate = useNavigate();
  const [formState, setFormState] = useState({
    sessionName:'',
    selectedGameMode:''
  })

  
  useEffect(() => {
    browseSessionsSocket.on('connect', () => {
      console.log("SocketIo [BROWSE] - Connect to server-session");
    });
    browseSessionsSocket.on('disconnect', () => {
      console.log("SocketIo [BROWSE] - Disconnect from server-session");
      setSessions([]);
    });
    browseSessionsSocket.on('sessions_update', (data: any) => {
      console.log("SocketIo [BROWSE] - Sessions list update", data);
      setSessions(data["sessions"])
    });
    browseSessionsSocket.on('session_update', (data: any) => {
      console.log("SocketIo [BROWSE] - Session update", data);

      setSessions((sessions) => {
        const idx = sessions.findIndex(s => s.roomId === data.session.roomId);
        if (data.updateType === "NEW_SESSION" || (idx === -1 && data.updateType === "REMOVED_USER")) {
          sessions.push(data.session)
        } else if (idx !== -1) {
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
      browseSessionsSocket.close();
    };
  }, []);

  function selectSession(roomId: String, sessionName:String) {
    navigate('/session', { state: {roomId: roomId, sessionName: sessionName} });
  }

  function gameModeButtonClick(gameModeId) {
    setFormState({
      ...formState,
      selectedGameMode: gameModeId,
    })
  }

  function resetNewSessionForm() {
    setFormState({
      sessionName:'',
      selectedGameMode:null
    })
  }

  function handleFormChange(event) {
    setFormState({
      ...formState, 
      sessionName: event.target.value});
  }

  function handleNewSessionSubmit(e) {
    e.preventDefault();
    if (formState.sessionName === null || formState.selectedGameMode === null) {
      console.log("ERROR")
      return
    }

    postNewSession(formState).then((response) => {
      if (response.status === 200) {
        resetNewSessionForm();
      } 
    }).catch((error) => {
      console.log(error)
    })
  }

  return (
    <div className="container-fluid p-5">
      <div className='row justify-content-center'>
        <div className='col-lg-6 col-xl-4'>
          <div className='border p-3'>
            <label className='fs-3 mb-3'>Lista sessioni</label>
            <div>
              { sessions.length === 0 ?
                <p className="text-center">
                  Nessuna sessione disponibile
                </p>
                :
                sessions.map((s, index) =>
                  <SessionItem key={index} data={s} onClick={(data) => selectSession(data.roomId, data.sessionName)}/>
                )
              }
            </div>
          </div>
        </div>
        <div className="col-lg-6 col-xl-4">
            <div className="border p-3">
              <form onSubmit={handleNewSessionSubmit}>
                <label className='fs-3 mb-3'>Crea una sessione</label>
                <div className="mb-3">
                  <label htmlFor="sessionNameInput" className="form-label">Nome sessione</label>
                  <input type="text" className="form-control" id="sessionNameInput" value={formState.sessionName} onChange={handleFormChange}/>
                </div>
                <div className="mb-3">
                  <label className="form-label">Difficoltà gioco</label>
                  <div className="d-flex flex-row">
                    {
                      GAME_MODES.map((gm, index) => (
                        <GameModeButton key={index} name={gm.name} config={gm} onclick={gameModeButtonClick} selected={formState.selectedGameMode === gm.id}/>
                        ))
                      }
                  </div>
                </div>
                <div className="d-flex justify-content-end">
                  <button className="btn btn-outline-seconday me-1" onClick={() => resetNewSessionForm()}>Annulla selezione</button>
                  <button type="submit" className="btn btn-primary">Crea</button>
                </div>
              </form>
            </div>
        </div>
      </div>
    </div>
  );
}

export default SessionsList;
