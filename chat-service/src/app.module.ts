import { Module } from '@nestjs/common';
import { ChatModule } from './chat/chat.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { dataSourceOptions } from './db/data-source';
import { GraphQLModule } from '@nestjs/graphql';
import { ApolloDriver, ApolloDriverConfig } from '@nestjs/apollo';
import { join } from 'path';
import { DateScalar } from './common/scalars/date.scalar';
import { ConfigModule } from '@nestjs/config';
import { APP_GUARD } from '@nestjs/core';
import { HeaderAuthGuard } from './auth/header-auth.guard';

@Module({
  imports: [
    ConfigModule.forRoot({
      envFilePath: process.env.NODE_ENV ? `.${process.env.NODE_ENV}.env` : '.env',
      isGlobal: true,
    }),
    TypeOrmModule.forRoot(dataSourceOptions),
    ChatModule,
    GraphQLModule.forRoot<ApolloDriverConfig>({
      driver: ApolloDriver,
      playground: process.env.NODE_ENV === 'dev',
      typePaths: ['./**/*.graphql'],
      path: '/api/graphql',
      definitions: {
        path: join(process.cwd(), 'src/graphql/graphql.ts'),
        outputAs: 'class',
      },
    }),
  ],
  controllers: [],
  providers: [
    {
      provide: APP_GUARD,
      useClass: HeaderAuthGuard,
    },
    DateScalar,
  ],
})
export class AppModule {}