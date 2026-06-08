# Saqua Locamotos - Infraestrutura

## Produção

### Backend

Base URL:

```text
https://saqualocamotos.qzz.io
```

Swagger:

```text
https://saqualocamotos.qzz.io/swagger-ui/
```

OpenAPI:

```text
https://saqualocamotos.qzz.io/q/openapi
```

Health Check:

```text
https://saqualocamotos.qzz.io/q/health
```

---

## Oracle Cloud

### VM

* Oracle Linux 9.7
* Forma: VM.Standard.E2.1.Micro
* 1 OCPU
* 1 GB RAM

### Conexão SSH

```bash
ssh -i ~/Downloads/github-oracle opc@152.67.34.202
```

---

## Docker

### Ver containers

```bash
docker ps
```

### Logs

```bash
docker logs -f saqua-locamotos-backend
```

### Atualizar manualmente

```bash
docker compose pull
docker compose up -d --force-recreate
```

### Reiniciar container

```bash
docker restart saqua-locamotos-backend
```

---

## Arquivos importantes

### Docker Compose

```text
/home/opc/docker-compose.yml
```

### Variáveis de ambiente

```text
/home/opc/.env
```

### Nginx

```text
/etc/nginx/conf.d/api.conf
```

---

## CI/CD

Fluxo:

```text
git push main
        ↓
GitHub Actions
        ↓
Build Maven
        ↓
Build Docker
        ↓
Push Docker Hub
        ↓
Deploy Oracle via SSH
        ↓
docker compose pull
        ↓
docker compose up -d --force-recreate
```

Imagem Docker:

```text
matteusmoreno/saqua-locamotos-backend:latest
```

---

## Banco de Dados

### MongoDB Atlas

Banco:

```text
saqua-locamotos
```

---

## Infraestrutura Atual

```text
Frontend (Vercel)
        ↓
Cloudflare
        ↓
https://saqualocamotos.qzz.io
        ↓
Nginx
        ↓
Docker Compose
        ↓
Quarkus
        ↓
MongoDB Atlas
```

---

## Comandos Úteis

### Ver saúde da aplicação

```bash
curl https://saqualocamotos.qzz.io/q/health
```

### Testar Swagger

```text
https://saqualocamotos.qzz.io/swagger-ui/
```

### Testar OpenAPI

```text
https://saqualocamotos.qzz.io/q/openapi
```

### Testar Nginx

```bash
sudo nginx -t
```

### Reiniciar Nginx

```bash
sudo systemctl restart nginx
```
