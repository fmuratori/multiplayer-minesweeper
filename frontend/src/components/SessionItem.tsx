import React from 'react';
import './SessionItem.scss';
import moment from 'moment';

function SessionItem(props) {
  return (
    <div className='border rounded p-3 m-1'> 
      <div className='row align-items-center mb-1'>
        <div className='col-3'>
          <p className='mb-0'>{props.data.sessionName}</p>
          <p className='mb-0'>{props.data.gameMode}</p>
        </div>
        <div className='col'>
          <p className='mb-0'>
            <i className="bi bi-clock me-1"></i>
            {moment(props.data.creationDate).calendar()}
          </p>
        </div>
        <div className='col-auto'>
          <p className='mb-0'>
            <i className="bi bi-person-fill"></i>
            {props.data.numConnectedUsers}
          </p>
        </div>
        <div className='col-auto'>
          <button className='btn btn-primary' onClick={(e) => props.onClick(props.data)}>Join</button>
        </div>
      </div>
    </div>
  );
}

export default SessionItem;
