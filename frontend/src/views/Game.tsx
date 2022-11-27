import './Game.css';

function Game() {
  return (
    <div className="container">
      <div className="row justify-content-md-center">
        <div className="col-6">
          <div className="box">
            <div className="row align-items-center">
              <div className="col">
                <table className="game-grid">
                  <tr>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                    <td>
                      <button className="game-tile"></button>
                    </td>
                  </tr>
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
    </div>
  );
}

export default Game;
