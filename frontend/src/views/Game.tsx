import { useContext, useEffect } from 'react';
import React, { useState } from 'react';
import {gameSocket} from '../scripts/GameSocket';
import './Game.css';
import { useLocation, useNavigate } from 'react-router-dom';
import moment from 'moment'

function Game() {
  var [gameOverFlag, setGameOverFlag] = useState(true);
  var [gameWonFlag, setGameWonFlag] = useState(false);
  var [map, setMap] = useState(null);
  var [gameStartTime, setGameStartTime] = useState(null);

  const {state} = useLocation();
  const navigate = useNavigate(); 
  
  useEffect(() => {
    gameSocket.on('connect', () => {
      console.log('SocketIo [GAME] - Connect to server-game');
      gameSocket.emit("join_room", state.roomName);
      setGameStartTime(moment(state.session.creationDate))
    });
    gameSocket.on('disconnect', () => {
      console.log('SocketIo [GAME] - Disconnect from server-game');
      
      // show disconnection message and redirect to home button
      setTimeout(() => navigate('/sessions'), 5000);
    });
    gameSocket.on('players_count_update', (count: number) => {
      console.log("players_count_update", count);
    });
    gameSocket.on('game_won', (data: { map: string }) => {
      console.log("game_won", data["map"]);
      updateMap(data["map"]);
      gameOverFlag = true;
      gameWonFlag = true;

      // show won game message and redirect to home button
      gameSocket.close();
      setTimeout(() => navigate('/sessions'), 5000);
    });
    gameSocket.on('game_lost', (data: { map: string }) => {
      console.log("game_lost", data["map"]);
      updateMap(data["map"]);
      gameOverFlag = true;

      // show lost game message and redirect to home button
      gameSocket.close();
      setTimeout(() => navigate('/sessions'), 5000);
    });
    gameSocket.on('game_update', (data: { map: string }) => {
      console.log("game_update", data["map"]);
      updateMap(data["map"]);
    });

    gameSocket.open();

    setEmptyGrid();

    return () => {
      gameSocket.off("connect");
      gameSocket.off("disconnect");
      gameSocket.off('players_count_update');
      gameSocket.off('game_won');
      gameSocket.off('game_lost');
      gameSocket.off('game_update');
    };
  }, []);

  function setEmptyGrid() {
    // set the empty grid
    var map: string[][] = [];
    for (var i = 0; i < state.session.gridHeight; i++) {
      var row: string[] = [];
      for (var j = 0; j < state.session.gridWidth; j++) {
        row[j] = "";
      }
      map[i] = row;
    }
    setMap(map);
  }

  function updateMap(newStringMap:string) {
    var tilesList: string[] = newStringMap.split(" ");
    var map: string[][] = [];
    for (var i = 0; i < state.session.gridHeight; i++) {
      var row: string[] = [];
      for (var j = 0; j < state.session.gridWidth; j++) {
        row[j] = tilesList[i*state.session.gridWidth + j];
      }
      map[i] = row;
    }
    setMap(map);
  }

  function action(e:any, i:number, j:number) {
    e.preventDefault()
    if (e.type === 'click') {
      gameSocket.emit("action", {xCoordinate: i, yCoordinate: j, action:"VISIT"})
    } else if (e.type === 'contextmenu') {
      gameSocket.emit("action", {xCoordinate: i, yCoordinate: j, action:"FLAG"})
    }
  }

  function disconnect(e:React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();
    gameSocket.close();
  }

  function drawTable() {
    if (map === null)
      return

    var content = [];
    for (var i = 0; i < state.session.gridHeight; i++) {
      content.push(
        <tr key={"row-" + i}>
          { drawRow(i, map[i]) }
        </tr>
      );
    }
    return content;
  }
  function drawRow(rowNumber:number, row: any) {
    var content = [];
    var baseClasses = "text-center game-tile";
    if (state.session.gridWidth === 9)
      baseClasses += " game-tile-big";
    else if (state.session.gridWidth === 16)
      baseClasses += " game-tile-medium";
    else if (state.session.gridWidth === 30)
      baseClasses += " game-tile-small";
    for (let j = 0; j < state.session.gridWidth; j++) {
      var classes = baseClasses;
      if (row[j] === "E") classes += " game-tile-explosion fading";
      if (row[j] === "N" || row[j] === "F") {
        classes += " game-tile-not-visited";
        content.push(
          <td key={"cell-" + j} 
            className={classes} 
            onClick={(e) => action(e, rowNumber, j)} 
            onContextMenu={(e) => action(e, rowNumber, j)}>
            { drawCellContent(row[j]) }
          </td>);
      } else {
        content.push(
          <td key={"cell-" + j} className={classes}>
            { drawCellContent(row[j]) }
          </td>);
        }
      
    }
    return content;
  }
  function drawCellContent(content:string) {
    switch (content) {
      case "C":
        return <span className='fs-3'></span>
      case "1":
        return <span className='fs-3'>1</span>
      case "2":
        return <span className='fs-3'>2</span>
      case "3":
        return <span className='fs-3'>3</span>
      case "4":
        return <span className='fs-3'>4</span>  
      case "5":
        return <span className='fs-3'>5</span>
      case "6":
        return <span className='fs-3'>6</span>
      case "7":
        return <span className='fs-3'>7</span>
      case "8":
        return <span className='fs-3'>8</span>
      case "F":
        return <i className='bi bi-flag big-icon fs-4'></i>
      case "E":
        return <i className="bi bi-virus2  big-icon"></i>
      case "M":
        return <i className="bi bi-virus2  big-icon"></i>
      case "N":
        return <span></span>
      default:
        return <i className='bi bi-question big-icon'></i>
    }
  }

  return (
    <div>
      <div className="mt-4">
        <div className="row justify-content-center">
          <div className="col-lg-12">
            <div className="box">
              <div className="row align-items-center justify-content-center">
                <div className="col-auto p-4">
                  <table>
                    <tbody>
                      {drawTable()}
                    </tbody>
                  </table>
                </div>
                <div className="col-3 text-center">
                  <div className="row row-cols-12">
                    <div className="bi-alarm big-icon">
                      <i></i>
                      <h3>
                        ORA
                      </h3>
                    </div>
                    <div>
                      <i className="bi-flag big-icon"></i>
                      <h3>
                        FLAG  
                      </h3>
                    </div>
                    <div>
                      <i className="bi-people big-icon"></i>
                      <h3>
                        GIOCATORI
                      </h3>
                    </div>
                    <div>
                      <button type="button" className="btn btn-danger btn-lg" onClick={disconnect}>Esci</button>
                    </div>
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

export default Game;
