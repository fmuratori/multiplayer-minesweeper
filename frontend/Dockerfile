FROM node:14-alpine

WORKDIR /app
COPY . .
RUN npm install -g npm@latest
RUN npm ci 
RUN npm run build
ENV NODE_ENV production
EXPOSE 3000
CMD [ "npx", "serve", "build" ]
