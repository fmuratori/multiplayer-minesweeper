import './GameModeButton.scss';

function GameModeButton(props) {

  return (
    <div className="container px-1" onClick={() => props.onclick(props.config.name)}>
      <div className={`border py-4 my-big-button` + (props.selected ? ` my-selected-button` : ``)}>
        <p className="text-center mb-2">
          <span className="fs-4"> {props.name} </span>
        </p>
        <p className="text-center mb-0">
          <i className="bi bi-grid-3x3"></i>
          <span> {props.config.gridWidth} x {props.config.gridHeight} </span>
        </p>
        <p className="text-center mb-0">
          <i className="bi bi-virus2"></i>
          <span> {props.config.numMines} </span>
        </p>
        <p className="text-center mb-0">
          <i className="bi bi-person"></i>
          <span> {props.config.numPlayers} </span>
        </p>
      </div>
    </div>
  );
}

export default GameModeButton;
