import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { ValidationPipe } from '@nestjs/common';
import { Transport } from '@nestjs/microservices';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import * as fs from 'fs';
import * as http from 'http';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.setGlobalPrefix('api');
  app.useGlobalPipes(new ValidationPipe());

  const config = new DocumentBuilder()
    .setTitle('Web scraper microservice')
    .setDescription('The web scraper microservice provides price data of the products from external websites')
    .setVersion('1.0')
    .addTag('scraper')
    .addSecurity('JWT', {
      type: 'http',
      scheme: 'bearer',
    })
    .build();
  const document = SwaggerModule.createDocument(app, config);
  SwaggerModule.setup('api', app, document);

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
      queue: 'advertisement-queue',
      noAck: false,
      persistent: true,
      prefetchCount: 1,
      deserializer: {
        deserialize(value) {
          return ({ pattern: 'advertisement', data: value });
        },
      },
      queueOptions: {
        durable: true,
      },
    },
  });

  await app.startAllMicroservices();
  await app.listen(process.env.PORT);

  const file = fs.createWriteStream('src/openapi.yaml');
  http.get(`http://localhost:${process.env.PORT}/api-yaml`, response => {
    response.pipe(file);
    file.on('finish', () => {
        file.close();
        console.info('openapi.yaml saved');
    });
  });
}
bootstrap();
