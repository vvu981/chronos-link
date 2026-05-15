# Etapa 1: Build
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Etapa 2: Servidor estático
FROM nginx:alpine
# Copiamos nuestra configuración personalizada
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copiamos los archivos compilados
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]