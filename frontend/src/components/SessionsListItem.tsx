import { Link } from "react-router-dom";
import React from "react";
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import './SessionsListItem.css';

function SessionsListItem() {
  return (
    <Container>
      <Row>
        <Col>
          <Link to={`/create-session`}>Your Name</Link>
        </Col>
      </Row>
    </Container>
  );
}

export default SessionsListItem;
