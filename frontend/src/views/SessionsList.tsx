import { useState, useEffect } from 'react';
import './SessionsList.css';
import SessionItem from '../components/SessionItem';
import {browseSessionsSocket} from '../scripts/SessionSocket'
import { useNavigate } from 'react-router-dom';
import GameModeButton from "../components/GameModeButton";
import {postNewSession, getGameModes} from "../scripts/api";

function SessionsList() {
  const [sessions, setSessions] = useState([]);
  const navigate = useNavigate();
  const [formState, setFormState] = useState({
    sessionName:'',
    gameMode: null,
  })
  const [gameModes, setGameModes] = useState([]);
  
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

    getGameModes().then((response) => {
      if (response.status === 200) {
        setGameModes(response.data["game_modes"]);
      } 
    }).catch((error) => {
      console.log(error)
    })
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

  function gameModeButtonClick(gameMode) {
    setFormState({
      ...formState,
      gameMode: gameMode,
    })
  }

  function resetForm() {
    setFormState({
      sessionName:'',
      gameMode: null
    })
  }

  function handleFormChange(event) {
    setFormState({
      ...formState, 
      sessionName: event.target.value});
  }

  function handleFormSubmit(e) {
    e.preventDefault();
    if (formState.sessionName === '' || formState.gameMode === null) {
      return
    }

    postNewSession({
      name: formState.sessionName,
      mode: formState.gameMode.name,
      numPlayers: formState.gameMode.numPlayers,
      numMines: formState.gameMode.numMines,
      gridWidth: formState.gameMode.gridWidth,
      gridHeight: formState.gameMode.gridHeight,
    }).then((response) => {
      if (response.status === 200) {
        resetForm();
        navigate('/session', { state: {roomId: response.data.roomId, sessionName: response.data.sessionName} });
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
            <label className='fs-3 mb-3'>Available sessions</label>
            <div>
              { sessions.length === 0 ?
                <p className="text-center">
                  Sessions not found
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
          <div className="border p-3 mt-md-4 mt-lg-0">
            <form onSubmit={handleFormSubmit}>
              <label className='fs-3 mb-3'>Create a session</label>
              <div className="mb-3">
                <label htmlFor="sessionNameInput" className="form-label">Name</label>
                <input type="text" className="form-control" id="sessionNameInput" value={formState.sessionName} onChange={handleFormChange}/>
              </div>
              <div className="mb-3">
                <label className="form-label">Game mode</label>
                <div className="d-flex flex-row">
                  {
                    gameModes.length === 0 ? 
                    <p>No game mode available</p>
                    :
                    gameModes.map((gm, index) => (
                      <GameModeButton key={index} name={gm.name} config={gm} onclick={gameModeButtonClick} selected={formState.gameMode === gm}/>
                      ))
                    }
                </div>
              </div>
              <div className="d-flex justify-content-end">
                <button className="btn btn-outline-seconday me-1" onClick={() => resetForm()}>Reset</button>
                <button type="submit" className="btn btn-primary">Submit</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SessionsList;
