import { useContext, useEffect } from 'react';
import React, { useState } from 'react';
import {SocketContext} from '../scripts/SocketIOContext';
import './Game.css';

function Game() {
  const socket:any = useContext(SocketContext);
  var [gridWidth, setGridWidth] = useState(4);
  var [gridHeight, setGridHeight] = useState(4);
  var [gameOverFlag, setGameOverFlag] = useState(false);
  var [gameWonFlag, setGameWonFlag] = useState(false);
  var [map, setMap] = useState(null);
  var [gameStartTime, setGameStartTime] = useState(new Date());
  
  useEffect(() => {
    // set the empty grid
    var map: string[][] = [];
    for (var i = 0; i < gridWidth; i++) {
      var row: string[] = [];
      for (var j = 0; j < gridHeight; j++) {
        row[j] = "";
      }
      map[i] = row;
    }
    setMap(map);

    // setup socket messages handlers
    socket.on('new_connection', (count: number) => {
      console.log("new_connection", count);
    });
    socket.on('game_won', (data: { map: string }) => {
      console.log("game_won", data["map"]);
      updateMap(data["map"]);
      gameOverFlag = true;
      gameWonFlag = true;
    });
    socket.on('game_lost', (data: { map: string }) => {
      console.log("game_lost", data["map"]);
      updateMap(data["map"]);
      gameOverFlag = true;
    });
    socket.on('game_update', (data: { map: string }) => {
      console.log("game_update", data["map"]);
      updateMap(data["map"]);
    });

    return () => {
      socket.off('new_connection');
      socket.off('game_won');
      socket.off('game_lost');
      socket.off('game_update');
    };
  }, []);

  function updateMap(newStringMap:string) {
    var tilesList: string[] = newStringMap.split(" ");
    var map: string[][] = [];
    for (var i = 0; i < gridWidth; i++) {
      var row: string[] = [];
      for (var j = 0; j < gridHeight; j++) {
        row[j] = tilesList[i*gridWidth + j];
      }
      map[i] = row;
    }
    setMap(map);
  }

  function connect(e:React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();
    socket.open();
  }

  function join_room(e:React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();
    socket.emit("join_room", "test_room");
  }

  function action(e:any, i:number, j:number) {
    if (e.type === 'click') {
      socket.emit("action", {xCoordinate: i, yCoordinate: j, action:"VISIT"})
    } else if (e.type === 'contextmenu') {
      socket.emit("action", {xCoordinate: i, yCoordinate: j, action:"FLAG"})
    }
  }

  function disconnect(e:React.MouseEvent<HTMLButtonElement>) {
    e.preventDefault();
    socket.close();
  }

  function drawTable() {
    if (map === null)
      return

    var content = [];
    for (var i = 0; i < gridHeight; i++) {
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
    for (let j = 0; j < gridWidth; j++) {
      content.push(
        <td key={"cell-" + j} className="game-tile text-center" onClick={(e) => action(e, rowNumber, j)} onContextMenu={(e) => action(e, rowNumber, j)}>
          { drawCellContent(row[j]) }
        </td>
      );
    }
    return content;
  }
  function drawCellContent(content:string) {
    switch (content) {
      case "EMPTY":
        return <i className='bi bi-square big-icon'></i>
      case "NEAR_1":
        return <i className='bi bi-1-square big-icon'></i>
      case "NEAR_2":
        return <i className='bi bi-2-square big-icon'></i>
      case "NEAR_3":
        return <i className='bi bi-3-square big-icon'></i>
      case "NEAR_4":
        return <i className='bi bi-4-square big-icon'></i>  
      case "NEAR_5":
        return <i className='bi bi-5-square big-icon'></i>
      case "NEAR_6":
        return <i className='bi bi-6-square big-icon'></i>
      case "NEAR_7":
        return <i className='bi bi-7-square big-icon'></i>
      case "NEAR_8":
        return <i className='bi bi-8-square big-icon'></i>
      case "FLAG":
        return <i className='bi bi-flag big-icon'></i>
      case "EXPLOSION":
        return <i className='bi bi-x-square big-icon'></i>
      case "NOT_VISITED":
        return <i className='bi bi-question-square big-icon'></i>
      default:
        return <i className='bi bi-question big-icon'></i>
    }
  }

  return (
    <div className="container">
      <div className="row justify-content-md-center">
        <div className="col-6">
          <div className="box">
            <div className="row align-items-center">
              <div className="col">
                <table>
                  <tbody>
                    {drawTable()}
                  </tbody>
                </table>
              </div>
              <div className="col text-center">
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
                    <button type="button" className="btn btn-danger btn-lg">Esci</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <button type="button" className="btn" onClick={connect}>Connect</button>
      <button type="button" className="btn" onClick={disconnect}>Disconnect</button>
      <button type="button" className="btn" onClick={join_room}>Join room</button>
    </div>
  );
}

export default Game;
