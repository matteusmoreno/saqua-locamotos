# Saqua Locamotos - Infraestrutura e Deploy

## Servidor

### Oracle Cloud Free Tier

* Sistema Operacional: Oracle Linux 9.7
* IP Público: `152.67.34.202`
* Usuário SSH: `opc`

---

## Conexão SSH

### Conectar na VM

```bash
ssh -i ~/Downloads/github-oracle opc@152.67.34.202
```

---

## Estrutura atual da VM

### Home do usuário

```text
/home/opc
```

### Arquivos existentes

```text
/home/opc/docker-compose.yml
/home/opc/.env
/home/opc/cloudflared/
/home/opc/.ssh/
```

---

## Docker

### Ver containers em execução

```bash
docker ps
```

### Ver logs da API

```bash
docker logs -f saqua-locamotos-backend
```

### Reiniciar container

```bash
docker restart saqua-locamotos-backend
```

### Atualizar para a última imagem

```bash
cd /home/opc

docker compose pull
docker compose up -d --force-recreate
```

---

## Docker Compose

### Localização

```text
/home/opc/docker-compose.yml
```

### Imagem utilizada

```text
matteusmoreno/saqua-locamotos-backend:latest
```

---

## Variáveis de Ambiente

### Arquivo

```text
/home/opc/.env
```

### Editar

```bash
nano /home/opc/.env
```

### Após alterar

```bash
docker compose up -d --force-recreate
```

---

## CI/CD

### Workflow

```text
.github/workflows/ci.yml
```

### Fluxo

```text
git push main
        ↓
GitHub Actions
        ↓
Build Quarkus
        ↓
Build Docker
        ↓
Push Docker Hub
        ↓
SSH Oracle
        ↓
docker compose pull
        ↓
docker compose up -d --force-recreate
```

### Secrets utilizados

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN

ORACLE_HOST
ORACLE_USER
ORACLE_SSH_KEY
```

---

## Backend

### Porta da aplicação

```text
9292
```

### Health Check

```bash
curl http://localhost:9292/q/health
```

### Health Check Live

```bash
curl http://localhost:9292/q/health/live
```

---

## Swagger

### Swagger Local

```text
http://localhost:9292/swagger-ui
```

### Swagger Público

```text
https://instances-fuel-shipping-hands.trycloudflare.com/swagger-ui
```

### OpenAPI

```text
https://instances-fuel-shipping-hands.trycloudflare.com/q/openapi
```

---

## Base URL Atual

```text
https://instances-fuel-shipping-hands.trycloudflare.com
```

### Exemplo Axios

```javascript
const api = axios.create({
  baseURL: 'https://instances-fuel-shipping-hands.trycloudflare.com',
});
```

---

## Cloudflare Tunnel

### Diretório

```text
/home/opc/cloudflared
```

### Ver versão

```bash
cd ~/cloudflared

./cloudflared --version
```

### Iniciar túnel

```bash
cd ~/cloudflared

./cloudflared tunnel --url http://localhost:9292
```

### URL atual

```text
https://instances-fuel-shipping-hands.trycloudflare.com
```

### Importante

Esta URL foi criada usando Quick Tunnel.

Se o processo for encerrado ou a VM reiniciar:

* A URL pode mudar.
* Será necessário criar um novo túnel.

Próxima melhoria recomendada:

* Criar um Cloudflare Named Tunnel permanente.

---

## Banco de Dados

### Provedor

```text
MongoDB Atlas
```

### Banco

```text
saqua-locamotos
```

---

## Deploy Manual

### Atualizar imagem

```bash
cd /home/opc

docker compose pull
```

### Recriar container

```bash
docker compose up -d --force-recreate
```

### Limpar imagens antigas

```bash
docker image prune -f
```

---

## Comandos Úteis

### Ver uso de memória

```bash
free -h
```

### Ver uso de disco

```bash
df -h
```

### Ver processos

```bash
top
```

### Ver portas abertas

```bash
sudo ss -tulpn
```

### Ver informações do sistema

```bash
cat /etc/os-release
```

---

## Observações

Atualmente o backend está hospedado gratuitamente em uma VM Oracle Cloud e atualizado automaticamente via GitHub Actions.

A API está exposta publicamente através de um Cloudflare Tunnel HTTPS.

O único ponto pendente para produção definitiva é substituir o Quick Tunnel por um Cloudflare Tunnel permanente ou por um domínio próprio.
