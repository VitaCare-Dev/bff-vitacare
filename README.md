# bff-vitacare

Backend For Frontend (BFF) de **VitaCare**, una aplicación móvil de seguimiento de salud crónica. Este servicio actúa como fachada única para el frontend: valida la autenticación de Firebase, resuelve la identidad del paciente autenticado y orquesta/agrega las llamadas hacia los microservicios de dominio, exponiendo una API REST simplificada y coherente para el cliente móvil.

## Tabla de contenidos

- [Arquitectura](#arquitectura)
- [Stack tecnológico](#stack-tecnológico)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución local](#ejecución-local)
- [Pruebas y cobertura](#pruebas-y-cobertura)
- [Docker](#docker)
- [Endpoints de la API](#endpoints-de-la-api)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Manejo de errores](#manejo-de-errores)

## Arquitectura

```
                     ┌─────────────────────┐
  App móvil  ───────▶│     bff-vitacare     │
 (Firebase ID Token) │  (valida el token,   │
                     │  orquesta y agrega)  │
                     └──────────┬───────────┘
                                │
        ┌───────────┬───────────┼───────────┬───────────────┐
        ▼           ▼           ▼           ▼               ▼
  user-service  patient-service  measurement-service  medication-service  chatbot-service
                                                                  │
                                                                  ▼
                                                          ai-alert-service
                                                        (Azure Functions)
```

El BFF es el **único punto del backend donde se valida el token de Firebase**. Los microservicios de dominio confían en el tráfico proveniente del BFF y no validan JWT por su cuenta.

## Stack tecnológico

| Componente | Detalle |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5.16 |
| Seguridad | Spring Security + OAuth2 Resource Server (validación de JWT de Firebase) |
| Cliente HTTP | `RestClient` (Spring 6) |
| Utilidades | Lombok |
| Testing | JUnit 5, Mockito, `MockRestServiceServer`, JaCoCo |
| Build | Maven (con wrapper `mvnw`) |
| Contenedor | Docker multi-stage (Maven + Eclipse Temurin JRE Alpine) |

## Requisitos previos

- JDK 21
- Maven (o usar el wrapper incluido `./mvnw`)
- Un proyecto de Firebase con Authentication habilitado
- Los microservicios de dominio corriendo (local o remoto) para pruebas funcionales end-to-end

## Configuración

Todas las variables de entorno son opcionales en desarrollo local (tienen valores por defecto apuntando a `localhost`), pero son obligatorias en un despliegue real.

| Variable de entorno | Descripción | Valor por defecto |
|---|---|---|
| `FIREBASE_PROJECT_ID` | ID del proyecto de Firebase contra el que se valida el `issuer` y la audiencia del ID Token | `vitacare-a6641` |
| `USER_SERVICE_URL` | URL base de `user-service` | `http://localhost:8085` |
| `PATIENT_SERVICE_URL` | URL base de `patient-service` | `http://localhost:8082` |
| `MEASUREMENT_SERVICE_URL` | URL base de `measurement-service` | `http://localhost:8083` |
| `MEDICATION_SERVICE_URL` | URL base de `medication-service` | `http://localhost:8084` |
| `CHATBOT_SERVICE_URL` | URL base de `chatbot-service` | `http://localhost:8080` |
| `AI_ALERT_SERVICE_URL` | URL base de `ai-alert-service` (Azure Functions) | URL de despliegue en Azure |
| `AI_ALERT_SERVICE_FUNCTION_KEY` | Function key de Azure requerida para autenticar cada llamada a `ai-alert-service` | _(vacío)_ |

El servicio escucha por defecto en el puerto **8086** (`server.port`).

## Ejecución local

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Con las variables de entorno necesarias exportadas antes de levantar el proceso, por ejemplo:

```bash
export FIREBASE_PROJECT_ID=mi-proyecto-firebase
export USER_SERVICE_URL=http://localhost:8085
# ...resto de variables
./mvnw spring-boot:run
```

## Pruebas y cobertura

El proyecto mantiene **100% de cobertura** de instrucciones y ramas (medido con JaCoCo, excluyendo el punto de entrada `main()` y las clases DTO generadas por Lombok, que JaCoCo excluye automáticamente).

```bash
./mvnw test
```

El reporte HTML queda disponible en `target/site/jacoco/index.html` tras correr los tests.

## Docker

```bash
docker build -t bff-vitacare .
docker run -p 8086:8086 --env-file .env bff-vitacare
```

El `Dockerfile` usa build multi-stage: compila con `maven:3.9.6-eclipse-temurin-21` y empaqueta el `.jar` final sobre `eclipse-temurin:21-jre-alpine` para una imagen liviana.

## Endpoints de la API

Todas las rutas bajo `/api/**` requieren un header `Authorization: Bearer <ID_TOKEN_DE_FIREBASE>`. El resto de rutas no exige autenticación.

### Autenticación y perfil

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/me` | Datos del usuario autenticado |
| `POST` | `/api/auth/register` | Registra (o continúa el registro de) el paciente asociado al usuario autenticado |

### Paciente

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/patients/me` | Perfil del paciente autenticado |
| `PUT` | `/api/patients/me` | Actualiza parcialmente el perfil |
| `DELETE` | `/api/patients/me` | Elimina la cuenta (paciente + usuario) |
| `GET` | `/api/patients/me/thresholds` | Umbrales médicos derivados de sus enfermedades crónicas |
| `GET` | `/api/patients/me/diseases` | Enfermedades crónicas asociadas |
| `POST` | `/api/patients/me/diseases` | Asocia una enfermedad crónica |
| `GET` | `/api/diseases` | Catálogo completo de enfermedades |

### Direcciones

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/patients/me/addresses` | Crea una dirección |
| `GET` | `/api/patients/me/addresses` | Lista las direcciones del paciente |
| `PUT` | `/api/patients/me/addresses/{id}` | Actualiza una dirección |
| `DELETE` | `/api/patients/me/addresses/{id}` | Elimina una dirección |

### Mediciones de salud

| Método | Ruta | Descripción |
|---|---|---|
| `POST` / `GET` | `/api/measurements/glucose` | Registrar / listar mediciones de glucosa |
| `GET` | `/api/measurements/glucose/latest` | Última medición de glucosa |
| `GET` / `DELETE` | `/api/measurements/glucose/{id}` | Consultar / eliminar una medición puntual |
| `POST` / `GET` | `/api/measurements/lipids` | Registrar / listar perfiles lipídicos |
| `GET` | `/api/measurements/lipids/latest` | Último perfil lipídico |
| `GET` / `DELETE` | `/api/measurements/lipids/{id}` | Consultar / eliminar un perfil puntual |
| `POST` / `GET` | `/api/measurements/vitals` | Registrar / listar signos vitales |
| `GET` | `/api/measurements/vitals/latest` | Última medición de signos vitales |
| `GET` / `DELETE` | `/api/measurements/vitals/{id}` | Consultar / eliminar una medición puntual |
| `GET` | `/api/measurements/history` | Historial combinado de controles de salud |

### Medicamentos

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/medications` | Registra un medicamento |
| `GET` | `/api/medications?active={bool}` | Lista medicamentos (todos o solo activos) |
| `PATCH` | `/api/medications/{id}/deactivate` | Desactiva un medicamento (fin de tratamiento) |
| `DELETE` | `/api/medications/{id}` | Elimina un medicamento |

### Alertas y recomendaciones de IA

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/alerts` | Alertas de IA del paciente |
| `GET` | `/api/alerts/unread` | Alertas no leídas |
| `PUT` | `/api/alerts/{id}/read` | Marca una alerta como leída |
| `PUT` | `/api/alerts/read-all` | Marca todas las alertas como leídas |
| `GET` | `/api/recommendations` | Recomendaciones alimentarias de IA |
| `GET` | `/api/recommendations/unread` | Recomendaciones no leídas |
| `PUT` | `/api/recommendations/{id}/read` | Marca una recomendación como leída |
| `PUT` | `/api/recommendations/read-all` | Marca todas las recomendaciones como leídas |

### Asistente de IA (chatbot)

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/chat` | Envía un mensaje al chatbot de IA y devuelve su respuesta |

## Estructura del proyecto

```
src/main/java/com/grupo10/bff_vitacare/
├── client/       # Clientes HTTP hacia cada microservicio (RestClient)
├── config/       # Configuración de seguridad (validación de JWT, CORS)
├── controller/   # Controladores REST (capa fina de delegación)
├── dto/          # Objetos de transferencia de datos
├── exception/    # Excepciones propias y manejador global
└── service/      # Lógica de orquestación entre microservicios
```

## Manejo de errores

Todas las excepciones se traducen a un cuerpo JSON consistente vía `GlobalExceptionHandler`:

```json
{
  "message": "Descripción legible del error",
  "status": 404,
  "timestamp": "2026-07-05T21:00:00"
}
```

Los errores devueltos por los microservicios ascendentes (4xx) se traducen preservando su código de estado original; nunca se expone el detalle técnico crudo del error al cliente.
