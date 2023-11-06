import { DataSource, DataSourceOptions } from 'typeorm';

if (process.env.NODE_ENV === 'migration') {
  require('dotenv').config({ path: 'src/db/.migration.env' });
}

const entityPath = process.env.NODE_ENV === 'test' ? 'src/**/*.entity.ts' : 'dist/**/*.entity.js';

export const dataSourceOptions: DataSourceOptions = {
  type: 'mysql',
  host: process.env.DB_URL,
  port: parseInt(process.env.DB_PORT),
  username: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
  entities: [entityPath],
  migrations: ['dist/db/migrations/*.js'],
  synchronize: false,
  migrationsRun: true,
};

const dataSource = new DataSource(dataSourceOptions);
export default dataSource;
