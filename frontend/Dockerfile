FROM node:15.11-alpine as build
COPY package.json yarn.lock babel.config.js ./
COPY common common
RUN yarn install --frozen-lockfile
COPY .env ./
COPY public public
COPY src src
RUN yarn build

FROM node:15.11-alpine
RUN yarn add express
COPY server.js .
COPY --from=build /dist /dist
ENV PORT 8080
ENTRYPOINT ["node", "server.js"]
