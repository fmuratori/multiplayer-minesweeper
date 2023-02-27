FROM node:14.17-alpine

WORKDIR /app

COPY package*.json ./

RUN npm install --no-package-lock

COPY . ./

# RUN npm run build

# CMD ["npx", "serve", "build"]    
CMD ["npm", "run", "start"]    