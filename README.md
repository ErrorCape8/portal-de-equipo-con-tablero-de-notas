# Portal de equipo con tablero de notas

Proyecto de prueba técnica para un portal interno con tablero de notas, autenticación por roles, administración de usuarios y métricas de dashboard integradas con AWS Lambda.

<div align="center">
  <a href="https://youtu.be/66GBdKaNS6s" target="_blank" rel="noopener noreferrer">
    <img src="https://img.youtube.com/vi/66GBdKaNS6s/maxresdefault.jpg" alt="Video de la demo del proyecto" width="900" />
  </a>
</div>

Video demostrativo: https://youtu.be/66GBdKaNS6s

## Requisitos

- Docker Desktop o Docker Engine + Docker Compose
- Java 26
- Maven 3.9+
- AWS CLI v2
- SAM CLI
- Git

## Arquitectura breve

- Frontend estático con HTML, CSS y JavaScript
- Backend en Spring Boot con Spring Security y JWT
- Persistencia en MySQL
- Lambda en Node.js para métricas del dashboard
- Infraestructura AWS con EC2, Lambda, S3 y CloudFront

## Arranque con Docker Compose

Desde la raíz del proyecto:

```bash
docker compose up --build -d
```

Esto levanta:

- MySQL en localhost:3307
- LocalStack en localhost:4566
- Backend en localhost:8080

URLs:

- Frontend: http://localhost:8080/frontend/login.html
- API: http://localhost:8080

## Arranque local sin Docker

```bash
docker compose up -d mysql localstack
./mvnw clean spring-boot:run
```

## Cuentas de demostración

El seeding inicial crea usuarios en `src/main/resources/Data.sql`:

- Administrador: admin@test.com
- Usuario: user@test.com

Roles:

- `ADMIN`: acceso a administración y dashboard
- `USER`: acceso al tablero y dashboard

## Uso

### Login

1. Abrir la pantalla de login.
2. Iniciar sesión con una cuenta demo.
3. El JWT se guarda en `localStorage`.

### Tablero de notas

- Crear notas
- Editar notas
- Cambiar estado: `PENDIENTE`, `EN_PROCESO`, `COMPLETADO`
- Mover notas por posición
- Eliminar notas

### Administración

Solo `ADMIN` puede entrar a la sección de administración:

- crear usuarios
- editar usuarios
- activar/desactivar cuentas
- cambiar roles

## Persistencia

La base de datos se define en `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/portal_db
spring.datasource.username=portal_user
spring.datasource.password=portal_pass
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```

Persistencia principal:

- `users`: usuarios y roles
- `notes`: notas, estado y posicionamiento

El volumen `mysql_data` mantiene los datos entre reinicios.

## Despliegue en AWS

La infraestructura está en `infra/template.yaml` y crea:

- EC2 para el backend
- IAM role para invocar la Lambda
- Lambda para métricas
- Bucket S3 + CloudFront para el frontend

Despliegue orientativo:

```bash
cd infra
aws cloudformation deploy \
  --template-file template.yaml \
  --stack-name portal-equipo-tablero-notas \
  --region us-east-1 \
  --capabilities CAPABILITY_IAM \
  --resolve-s3
```

Limpieza:

```bash
./infra/deploy.sh
```

## Datos relevantes

- `docker-compose.yml`: infraestructura local
- `dockerfile`: imagen de backend
- `pom.xml`: dependencias de Java/Spring Boot
- `src/main/resources/application.properties`: configuración del servicio
- `infra/template.yaml`: infraestructura AWS
- `lambda/dashboard-metrics/index.js`: lógica de métricas de Lambda

## Tiempo empleado

La prueba técnica se desarrolló en un periodo de aproximadamente 1-2 días, dependiendo del ajuste final de UI, validaciones y despliegue AWS.

## Limitaciones / pendientes conocidos

- El frontend es estático y no usa un framework moderno ni bundler
- La integración AWS es funcional para prueba técnica, no para entorno productivo completo
- La Lambda de métricas es básica y no incluye análisis histórico ni almacenamiento avanzado
- La seguridad es suficiente para la prueba, pero no contempla MFA ni auditoría avanzada

## Repositorio

- GitHub: https://github.com/ErrorCape8/portal-de-equipo-con-tablero-de-notas.git
