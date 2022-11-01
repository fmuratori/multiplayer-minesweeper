Per creare una immagine docker per il container

docker build -t mm-server-frontend .

---

Per inizializzare un container ed il server node

docker run -dp 8000:8000 mm-server-frontend