FROM node:18
RUN apt update && apt install -y libnss3 libatk1.0-0 libatk-bridge2.0-0 libcups2 libdrm2 libxcomposite1 libxdamage1 libxfixes3 libxrandr2 libgbm1 libxkbcommon0 libasound2
WORKDIR /usr/src/app
COPY package*.json .
RUN npm install
COPY tsconfig*.json .
COPY src/ src/
RUN npm run build
ENV NODE_ENV production
CMD [ "node", "dist/main.js" ]
