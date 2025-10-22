docker build -t item-service:latest .
docker run -d -p 8080:8080 --name item-service-container item-service:latest
