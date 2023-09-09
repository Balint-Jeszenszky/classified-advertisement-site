import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { Transport } from '@nestjs/microservices';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.setGlobalPrefix('api');

  app.connectMicroservice({
    transport: Transport.RMQ,
    options: {
      urls: [{
        protocol: 'amqp',
        hostname: process.env.RABBITMQ_HOST,
        port: 5672,
        username: process.env.RABBITMQ_USER,
        password: process.env.RABBITMQ_PASS,
      }],
      queue: 'email-queue',
      noAck: false,
      persistent: true,
      prefetchCount: 1,
      deserializer: {
        deserialize(value) {
          return ({ pattern: 'email', data: value });
        },
      },
      queueOptions: {
        durable: true,
      },
    },
  });

  app.connectMicroservice({
    transport: Transport.RMQ,
    options: {
      urls: [{
        protocol: 'amqp',
        hostname: process.env.RABBITMQ_HOST,
        port: 5672,
        username: process.env.RABBITMQ_USER,
        password: process.env.RABBITMQ_PASS,
      }],
      queue: 'push-queue',
      noAck: false,
      persistent: true,
      prefetchCount: 1,
      deserializer: {
        deserialize(value) {
          return ({ pattern: 'push', data: value });
        },
      },
      queueOptions: {
        durable: true,
      },
    },
  });

  await app.startAllMicroservices();
  await app.listen(process.env.PORT);
}
bootstrap();
