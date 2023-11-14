# classified-advertisement-site

## How to run

### Using Docker

Create a .env file with al the required environment variables and run the `docker compose -f docker-compose.dev.yaml --env-file .env up -d` or `docker compose -f docker-compose.yaml --env-file .env up -d` command

### Using Kubernetes

The easyest way to run in Kubernetes is tu run the `deploy.js` ile from the `k8s` folder. You can set secrets and config values in the config and secret files

## Required environment variables

### Gateway
- PORT
- DATABASE_URL
- DB_USER
- DB_PASS
- JWT_SECRET
- JWT_EXPIRATION
- JWT_REFRESH_SECRET
- JWT_REFRESH_EXPIRATION
- USER_SERVICE_URI
- ADVERTISEMENT_SERVICE_URI
- IMAGE_SERVICE_URI
- WEBSCRAPER_SERVICE_URI
- NOTIFICATION_SERVICE_URI
- CHAT_SERVICE_URI
- BID_SERVICE_URI
- FRONTEND_URI

### User service

- DATABASE_URL
- DB_PASS
- DB_USER
- PORT
- RABBITMQ_HOST
- RABBITMQ_USER
- RABBITMQ_PASS

### Advertisement service

- PORT
- DATABASE_URL
- DB_USER
- DB_PASS
- MINIO_URL
- MINIO_USER
- MINIO_PASS
- RABBITMQ_HOST
- RABBITMQ_USER
- RABBITMQ_PASS
- BID_API_URL

### Image processing service

- PORT
- DATABASE_URL
- DB_USER
- DB_PASS
- MINIO_URL
- MINIO_USER
- MINIO_PASS
- RABBITMQ_HOST
- RABBITMQ_USER
- RABBITMQ_PASS
- WATERMARK_TEXT

### Web scraper service

- PORT
- RABBITMQ_HOST
- RABBITMQ_PORT
- RABBITMQ_USER
- RABBITMQ_PASS
- MONGO_URL
- MONGO_USER
- MONGO_PASS

### Notification service

- PORT
- RABBITMQ_HOST
- RABBITMQ_PORT
- RABBITMQ_USER
- RABBITMQ_PASS
- MONGO_URL=notificationservice-db/notification
- MONGO_USER
- MONGO_PASS
- ADMIN_EMAIL
- VAPID_PUBLIC_KEY
- VAPID_PRIVATE_KEY
- EMAIL_HOST
- EMAIL_PORT
- EMAIL_USER
- EMAIL_PASS
- BASEURL

### Chat service

- PORT
- DB_URL
- DB_PORT
- DB_USER
- DB_PASS
- DB_NAME
- REDIS_HOST
- REDIS_PORT
- RABBITMQ_HOST
- RABBITMQ_PORT
- RABBITMQ_USER
- RABBITMQ_PASS
- ADVERTISEMENT_SERVICE_INTERNAL_API_URL
- ADVERTISEMENT_SERVICE_ADVERTISEMENT_EXISTS_PATH

### Bid service

- PORT
- DATABASE_URL
- DB_USER
- DB_PASS
- USER_API_URL
- RABBITMQ_HOST
- RABBITMQ_USER
- RABBITMQ_PASS
- REDIS_HOST
- REDIS_PORT

### Docker compose

Use these environment variables in the .env file:

- TZ
- BASEURL
- RABBITMQ_USER
- RABBITMQ_PASS
- MINIO_ROOT_USER
- MINIO_ROOT_PASSWORD
- MYSQL_GATEWAY_ROOT_PASSWORD
- MYSQL_GATEWAY_USERNAME
- MYSQL_GATEWAY_PASSWORD
- JWT_EXPIRATION
- JWT_REFRESH_EXPIRATION
- JWT_SECRET
- JWT_REFRESH_SECRET
- USERSERVICE_POSTGRES_USER
- USERSERVICE_POSTGRES_PASSWORD
- MYSQL_ADVERTISEMENTSERVICE_ROOT_PASSWORD
- MYSQL_ADVERTISEMENTSERVICE_USERNAME
- MYSQL_ADVERTISEMENTSERVICE_PASSWORD
- IMAGEPROCESSING_POSTGRES_USER
- IMAGEPROCESSING_POSTGRES_PASSWORD
- WATERMARK_TEXT
- MONGO_WEBSCRAPINGSERVICE_USERNAME
- MONGO_WEBSCRAPINGSERVICE_PASSWORD
- MONGO_NOTIFICATIONSERVICE_USERNAME
- MONGO_NOTIFICATIONSERVICE_PASSWORD
- ADMIN_EMAIL
- VAPID_PUBLIC_KEY
- VAPID_PRIVATE_KEY
- EMAIL_HOST
- EMAIL_PORT
- EMAIL_USER
- EMAIL_PASS
- MYSQL_CHATSERVICE_ROOT_PASSWORD
- MYSQL_CHATSERVICE_USERNAME
- MYSQL_CHATSERVICE_PASSWORD
- BIDSERVICE_POSTGRES_USER
- BIDERVICE_POSTGRES_PASSWORD

## Testing

### Gateway

To run e2e tests on the gateway you need to do the following:

- create a .env file with the required environment variables
- run all microservices with `docker compose -f docker-compose.dev.yaml --env-file .env up -d`
- register two users for testing with the following credentials
  - Username: admin, password: AdminPass
  - Username: user, password: UserPass
- confirm these user accounts by navigating to [localhost:8080/auth/confirmEmail/{key}](http://localhost:8080/auth/confirmEmail/{key}) (replace the {key} for the generated key). The key can be found in the userservice logs or in rabbitmq in the email queue
- log in with the admin user and create a category, then create an ad

### User service

To run integration tests you need you need a test database. The easiest way to run one is to use Docker: `docker run --name user-service-test-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=test_users -p 5432:5432 --restart unless-stopped -d postgres`

### Advertisement service

There is no dependencies to this, it uses h2 database for tests

### Image processing service

This microservice requires a Postgres database and MinIO object storage to run the integration tests

To run Postgres: `docker run --name imageprocessing-service-test-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=imagedata -p 5432:5432 --restart unless-stopped -d postgres`

Run MinIO: `docker run -p 9000:9000 -p 9001:9001 -e MINIO_ROOT_USER=root -e MINIO_ROOT_PASSWORD=rootpass quay.io/minio/minio server /data --console-address ":9001"`

### Web scraper service

To run the e2e tests you need to run a MongoDB instance: `docker run --name webscraper-service-test-mongo -e MONGO_INITDB_ROOT_USERNAME=testuser -e MONGO_INITDB_ROOT_PASSWORD=testpass -e MONGO_INITDB_DATABASE=scraper -d mongo`

You also need to create a `.test.env` file with the required environment variables

### Notification service

To run the e2e tests you need to run a MongoDB instance: `docker run --name notification-service-test-mongo -e MONGO_INITDB_ROOT_USERNAME=testuser -e MONGO_INITDB_ROOT_PASSWORD=testpass -e MONGO_INITDB_DATABASE=notification -d mongo`

You also need to create a `.test.env` file with the required environment variables

### Chat service

To run the e2e tests you need to run a MySQL instance: `docker run --name chat-mysql-dev -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql`

You also need to create a `.test.env` file with the required environment variables

### Bid service

To run integration tests you need you need a Postgres and a redis instance running.

Postgres: `docker run --name bid-service-test-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=bid -p 5432:5432 --restart unless-stopped -d postgres`

Redis: `docker run --name bid-service-test-redis -p 6379:6379 -d redis`
