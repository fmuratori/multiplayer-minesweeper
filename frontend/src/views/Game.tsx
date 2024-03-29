import { useEffect } from 'react';
import React, { useState } from 'react';
import {gameSocket} from '../scripts/GameSocket';
import './Game.css';
import { useLocation, useNavigate } from 'react-router-dom';
import Spinner from 'react-bootstrap/Spinner';
import moment from 'moment';
import Modal from 'react-bootstrap/Modal';

function Game() {
  const [gameOverFlag, setGameOverFlag] = useState(false);
  const [gameWonFlag, setGameWonFlag] = useState(false);
  const [gameData, setGameData] = useState(null);
  const [flagsCount, setFlagsCount] = useState(0);
  const [timerData, setTimerData] = useState(null);
  const [connPlayersCount, setConnPlayersCount] = useState(0);

  const {state} = useLocation();
  const navigate = useNavigate(); 
  
  useEffect(() => {
    gameSocket.on('connect', () => {
      console.log('SocketIo [GAME] - Connect to server-game');
      gameSocket.emit('join_room', state.roomName);
    });
    gameSocket.on('disconnect', () => {
      console.log('SocketIo [GAME] - Disconnect from server-game');
      gameSocket.emit('leave_room', state.roomName);
      
      // show disconnection message and redirect to home button
      setTimeout(() => navigate('/'), 5000);
    });
    gameSocket.on('players_count_update', (data: {connectedCount: number}) => {
      console.log('players_count_update', data);
      setConnPlayersCount(data.connectedCount);
    });
    gameSocket.on('game_won', (data: { map: string, duration: string }) => {
      console.log('game_won', data);
      setGameData(gameData => ({ ...gameData, map: data.map }));
      setGameOverFlag(true);
      setGameWonFlag(true);
      setTimerData(timerData => {
        clearInterval(timerData.interval);
        return {
          interval: null,
          elapsed: data.duration
        };
      });

      // show won game message and redirect to home button
      setTimeout(() => navigate('/'), 5000);
    });
    gameSocket.on('game_lost', (data: { map: string, duration: string }) => {
      console.log('game_lost', data);
      setGameData(gameData => ({ ...gameData, map: data.map }));
      setGameOverFlag(true);
      setFlagsCount(data.map.split('F').length - 1);
      setTimerData(timerData => {
        clearInterval(timerData.interval);
        return {
          interval: null,
          elapsed: data.duration
        };
      });

      // show lost game message and redirect to home button
      setTimeout(() => navigate('/'), 5000);
    });
    gameSocket.on('game_update', (data: { map: string }) => {
      console.log('game_update', data['map'], gameData);
      setGameData(gameData => ({ ...gameData, map: data.map }));
      setFlagsCount(data.map.split('F').length - 1);
    });
    gameSocket.on('game_info', (newGameData: {any}) => {
      console.log('game_info', newGameData);
      setGameData(newGameData);
      setFlagsCount(0);

      const interval = setInterval(() => {
        setTimerData(timerData => (
          {
            ...timerData, 
            elapsed: new Date().getTime() - new Date(newGameData['startedAt']).getTime()
          }
        ));
      }, 1000);
      setTimerData({
        interval: interval,
        elapsed: 0
      });
    });

    gameSocket.open();
    
    return () => {
      gameSocket.off('connect');
      gameSocket.off('disconnect');
      gameSocket.off('players_count_update');
      gameSocket.off('game_won');
      gameSocket.off('game_lost');
      gameSocket.off('game_update');
      gameSocket.off('game_info');
      gameSocket.close();

      if (timerData != null)
        clearInterval(timerData.interval);
    };
  // eslint-disable-next-line
  }, []);

  function getMapAsArray() {
    var tilesList: string[] = gameData.map.split(' ');
    var map: string[][] = [];
    for (var i = 0; i < gameData.gridHeight; i++) {
      var row: string[] = [];
      for (var j = 0; j < gameData.gridWidth; j++) {
        row[j] = tilesList[i*gameData.gridWidth + j];
      }
      map[i] = row;
    }
    return map;
  }

  function action(e:any, i:number, j:number) {
    e.preventDefault()
    if (e.type === 'click') {
      gameSocket.emit('action', {xCoordinate: i, yCoordinate: j, action:'VISIT'})
    } else if (e.type === 'contextmenu') {
      gameSocket.emit('action', {xCoordinate: i, yCoordinate: j, action:'FLAG'})
    }
  }

  function disconnect(e:React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();
    gameSocket.emit('leave_room', state.roomName, () => {
      gameSocket.close();
      navigate('/');
    });
  }

  function drawTable() {
    var map = getMapAsArray();
    if (map === null)
      return
    var content = [];
    for (var i = 0; i < gameData.gridHeight; i++) {
      content.push(
        <tr key={'row-' + i}>
          { drawRow(i, map[i]) }
        </tr>
      );
    }
    return content;
  }

  function drawRow(rowNumber:number, row: any) {
    var content = [];
    var baseClasses = 'text-center game-tile';
    if (gameData.gridWidth === 9)
      baseClasses += ' game-tile-big';
    else if (gameData.gridWidth === 16)
      baseClasses += ' game-tile-medium';
    else if (gameData.gridWidth === 30)
      baseClasses += ' game-tile-small';
    for (let j = 0; j < gameData.gridWidth; j++) {
      var classes = baseClasses;
      if (row[j] === 'E') classes += ' game-tile-explosion fading';
      if (row[j] === 'N' || row[j] === 'F') {
        classes += ' game-tile-not-visited';
        content.push(
          <td key={'cell-' + j} 
            className={classes} 
            onClick={(e) => action(e, rowNumber, j)} 
            onContextMenu={(e) => action(e, rowNumber, j)}>
            { drawCellContent(row[j]) }
          </td>);
      } else {
        content.push(
          <td key={'cell-' + j} className={classes}>
            { drawCellContent(row[j]) }
          </td>);
        }
      
    }
    return content;
  }

  function drawCellContent(content:string) {
    switch (content) {
      case 'C':
        return <span className=''></span>
      case '1':
        return <span className=''>1</span>
      case '2':
        return <span className=''>2</span>
      case '3':
        return <span className=''>3</span>
      case '4':
        return <span className=''>4</span>  
      case '5':
        return <span className=''>5</span>
      case '6':
        return <span className=''>6</span>
      case '7':
        return <span className=''>7</span>
      case '8':
        return <span className=''>8</span>
      case 'F':
        return <i className='bi bi-flag'></i>
      case 'E':
        return <i className='bi bi-virus2'></i>
      case 'M':
        return <i className='bi bi-virus2'></i>
      case 'N':
        return <span></span>
      default:
        return <i className='bi bi-question'></i>
    }
  }

  function formatElapsedTime(milliseconds) {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;
  
    return (hours < 10 ? '0'+hours : hours)+':'+
      (minutes < 10 ? '0'+minutes : minutes)+':'+
      (seconds < 10 ? '0'+seconds : seconds);
  }
  
  function formatDatetime(stringDate) {
    return moment(stringDate).calendar()
  }

  return (
    <div>
      {
        gameData == null ? 
          <Spinner animation='border' role='status'>
            <span className='visually-hidden'>Loading...</span>
          </Spinner>
        :
          <div>
            <div className='container-fluid p-3'>
              <div className='row justify-content-center'>
                <div className={`border rounded ` + (gameData.gridWidth === 30 ? `col-12` : gameData.gridWidth === 16 ? `col-xl-auto col-lg-12` : `col-xl-auto col-lg-10 col-md-12`)}>
                  <div className='row align-items-center justify-content-center text-center m-4'>
                    <div className='col-auto'>
                      <table>
                        <tbody>
                          {drawTable()}
                        </tbody>
                      </table>
                    </div>
                    <div className='col-auto mt-4'>
                      <div>
                        {
                          <div>
                            <p className='text-center title-3'>{state.sessionName}</p>
                            <p className='mb-0'>
                              <i className='bi bi-grid-3x3'></i>
                              <span> {gameData.gridWidth} x {gameData.gridHeight} </span>
                            </p>
                            <p className='mb-0'>
                              <i className='bi bi-virus2'></i>
                              <span> {gameData.minesCount} </span>
                            </p>
                            <p className='mb-0'>
                              <i className='bi bi-flag'></i>
                              <span> {flagsCount} </span>
                            </p>
                            <p className='mb-0'>
                              <i className='bi bi-person'></i>
                              <span> {connPlayersCount} / {gameData.numMaxPlayers} </span>
                            </p>
                            <p className='mb-0'>
                              <i className='bi bi-clock'></i>
                              <span> {formatDatetime(gameData.startedAt)} </span>
                            </p>
                            <p className='mb-3'>
                              <i className='bi bi-stopwatch'></i>
                              <span> {formatElapsedTime(timerData.elapsed)} </span>
                            </p>
                          </div>
                        }
                        <button type='button' className='btn btn-danger btn-lg' onClick={disconnect}>Esci</button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
      }
      <Modal show={gameOverFlag}>
        <Modal.Header>
          <Modal.Title> Game Over </Modal.Title>
        </Modal.Header>
        <Modal.Body className='text-center'>
          {
            gameWonFlag ? 
            <p className='fs-5 mb-5 mt-3 text-success'>
              <i className='bi-stars fs-3'></i>
              <br />
              Congratulation! 
              <br />
              You and your team won this game
            </p>
            :
            <p className='fs-5 mb-5 mt-3 text-danger'>
              <i className='bi-emoji-frown fs-3'></i>
              <br />
              Git gud! 
              <br />
              You and your team lost this game
            </p>
          }
          <p className='fs-6 mb-2 pb-0'>
            <i>
              You will be redirected to the main page shortly.
            </i>
          </p>

        </Modal.Body>
      </Modal>
    </div>
  );
}

export default Game;
