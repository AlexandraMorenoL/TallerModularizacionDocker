# microframework-eci

Mini framework web con:
- Anotaciones `@GET`, `@PathParam`, `@QueryParam`
- Reflexión para registrar rutas a partir de métodos anotados
- Concurrencia con `ThreadPoolExecutor`
- Apagado elegante con `shutdownHook`
- Endpoints de ejemplo: `/greeting?name=...`, `/users/{id}`

## Ejecutar local
```bash
mvn -q -DskipTests package
java -jar target/microframework-eci-1.0.0-shaded.jar
# http://localhost:6000/greeting?name=ECI
# http://localhost:6000/users/123
```

## Docker
```bash
mvn -q -DskipTests package
docker build -t microframework-eci:latest .
docker run -d -p 34001:6000 --name microeci microframework-eci:latest
# http://localhost:34001/greeting?name=ECI
```

## Docker Compose (web + Mongo opcional)
```bash
mvn -q -DskipTests package
docker compose up -d
# http://localhost:8087/greeting
```

## Publicar en Docker Hub
```bash
# Cambia <usuario> por tu usuario de Docker Hub
docker tag microframework-eci:latest <usuario>/microframework-eci:latest
docker login
docker push <usuario>/microframework-eci:latest
```

## Desplegar en AWS EC2 (resumen)
1. Lanza una instancia Amazon Linux 2023 (x86_64), abre el puerto 42000/TCP y 22/TCP en el Security Group.
2. Conéctate por SSH:
   ```bash
   ssh -i tu_llave.pem ec2-user@EC2_PUBLIC_DNS
   ```
3. Instala Docker y habilítalo:
   ```bash
   sudo dnf -y update
   sudo dnf -y install docker
   sudo systemctl enable --now docker
   sudo usermod -aG docker ec2-user
   exit
   ```
   (Vuelve a entrar por SSH para aplicar el grupo).
4. Ejecuta el contenedor desde Docker Hub:
   ```bash
   docker run -d -p 42000:6000 --name microeci <usuario>/microframework-eci:latest
   ```
5. Prueba: `http://EC2_PUBLIC_DNS:42000/greeting?name=AWS`

## Estructura
```
src/main/java/co/eci/microframework/
  App.java
  MicroServer.java
  annotations/...
```
