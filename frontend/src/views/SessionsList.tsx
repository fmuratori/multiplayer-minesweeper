import { Link } from "react-router-dom";
import './SessionsList.css';

function SessionsList() {
  return (
    <>
      <Link to={`/create-session`}>Your Name</Link>
      <Link to={`/session`}>Your Name</Link>
      <Link to={`/game`}>Your Name</Link>
      <Link to={`/create-session`}>Your Name</Link>
    </>
  );
}

export default SessionsList;
