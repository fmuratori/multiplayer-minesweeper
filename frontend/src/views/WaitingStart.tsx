import './WaitingStart.css';
import { useEffect, useState } from 'react';
import { sessionSocket } from '../scripts/SessionSocket'
import { useLocation, useNavigate } from 'react-router-dom';

function WaitingStart() {

  const [playersCount, setPlayersCount] = useState({connectedCount: 0, maxPlayersCount: 0});
  const {state} = useLocation();
  const navigate = useNavigate();

  
  useEffect(() => {
    sessionSocket.on('connect', () => {
      console.log('SocketIo [SESSION] - Connect to server-session');
      sessionSocket.emit('join_room', state.roomId)
    });
    sessionSocket.on('disconnect', () => {
      console.log('SocketIo [SESSION] - Disconnect from server-session');
    });
    sessionSocket.on('game_starting', (data:any) => {
      console.log('SocketIo [SESSION] - Game starting update', data);
      navigate('/game', { state: data });
    });
    sessionSocket.on('players_count_update', (data: any) => {
      console.log('SocketIo [SESSION] - Players count update', data);
      setPlayersCount(data)
    });
    sessionSocket.on('session_error', (data: any) => {
      console.log('SocketIo - Session error', data);
      setTimeout(() => navigate('/sessions'), 5000);
    });

    sessionSocket.open();

    return () => {
      sessionSocket.off('connect');
      sessionSocket.off('disconnect');
      sessionSocket.off('game_starting');
      sessionSocket.off('players_count_update');
      sessionSocket.off('session_error');
      sessionSocket.close();
    };
  }, [])

  function exitSession(e:any) {
    sessionSocket.emit('leave_room', state.roomId, 
      () => {
        sessionSocket.close();
        navigate('/sessions');
      });
  }

  return (
    <div className='container mt-6'>
      <div className='row justify-content-md-center'>
        <div className='col-6 '>
          <div>
            <div className='row gx-2'>
              <div className='col'>
                <div className='border rounded p-5'>
                  <p className='text-center mb-0 pb-0 '>
                    <span className='spinner-border my-big-spinner ' role='status'>
                      <span className='visually-hidden text-center'>Loading...</span>
                    </span>
                  </p>
                  <h2 className='mt-4 text-center'>
                    {state.sessionName}
                  </h2>
                  <div className='text-center'>
                    <h2>
                        Giocatori {playersCount.connectedCount} / {playersCount.maxPlayersCount}
                    </h2>
                  </div>
                  <div className='d-flex justify-content-center'>
                    <button className='btn btn-secondary' onClick={exitSession}>Esci</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default WaitingStart;
