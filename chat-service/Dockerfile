FROM node:18-alpine
WORKDIR /usr/src/app
COPY package*.json .
RUN npm install
COPY tsconfig*.json .
COPY src/ src/
RUN npm run build
ENV NODE_ENV production
CMD [ "node", "dist/main.js" ]
