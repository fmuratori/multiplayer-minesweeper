import './UserSession.css';

function UserSession() {
  return (
    <div className="container">
      <div className="row justify-content-md-center">
        <div className="col-6">
          <div>
            <div className="row gx-2">
              <div className="col">
                <div className="box p-2">
                  <div className="d-grid gap-2">
                    <h1 className="text-center mt-4">
                      NOME SESSIONE
                    </h1>
                    <hr />
                    <div className="row">
                      <div className="col">
                        <h2>
                          <span>Giocatori</span>
                        </h2>
                      </div>
                      <div className="col-md-auto">
                        <h2>
                          <label>
                            4/4
                          </label>
                        </h2>
                      </div>
                    </div>
                    <hr />
                    <h2>
                      Informazioni
                    </h2>
                    <div>
                      <p>
                        <label>Nome sessione:</label><br />
                        <label>NOME SESSIONE</label>
                      </p>
                      <p>
                        <label>Numero max. giocatori:</label><br />
                        <label>NUMERO MAX GIOCATORI</label>
                      </p>
                      <p>
                        <label>Modalità di gioco:</label><br />
                        <label>MODALITà DI GIOCO</label><br />
                      </p>
                      <p>
                        <label>Sessione aperta DELTA MINUTI minuti fà</label><br />
                      </p>
                    </div>
                    <div className="row justify-content-between">
                      <div className="col-auto">
                        <button className="btn btn-secondary">Esci da sessione</button>
                      </div>
                      <div className="col-auto">
                        <button className="btn btn-primary">Inizia partita</button>
                      </div>
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

export default UserSession;
