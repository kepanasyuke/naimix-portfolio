# Инфраструктура: локальный запуск и установка недостающего

## Что уже установлено на этой машине

| Инструмент | Версия | Путь |
| --- | --- | --- |
| JDK | 17 (Oracle) | `C:\Program Files\Java\jdk-17` |
| Maven | 3.9.9 | `C:\Users\Admin\Tools\apache-maven-3.9.9` |
| Node.js | 26.13.0 | `C:\Program Files\nodejs` |
| Redis | 5.0.14.1 (портативный) | `C:\Users\Admin\Tools\redis` |
| Git | установлен | `C:\Program Files\Git` |

Maven скачан бинарником: в winget пакета нет, а установка Postgres/Docker требует прав администратора,
поэтому они не поставлены в рамках этой сессии.

## Redis: запуск портативной копии

```powershell
& "C:\Users\Admin\Tools\redis\redis-server.exe" --port 6379
```

Проверка:

```powershell
& "C:\Users\Admin\Tools\redis\redis-cli.exe" ping   # ожидается PONG
```

## PostgreSQL 17: установка с правами администратора

Запустите PowerShell **от имени администратора**:

```powershell
winget install --id PostgreSQL.PostgreSQL.17 -e `
  --accept-package-agreements --accept-source-agreements `
  --override "--mode unattended --superpassword postgres --serverport 5432"
```

Проверка:

```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -c "select version();"
```

Затем создайте базы под сервисы:

```sql
CREATE DATABASE auth;
CREATE DATABASE vacancy;
CREATE DATABASE notification;
CREATE DATABASE billing;
```

## Docker Desktop: установка с правами администратора

```powershell
winget install --id Docker.DockerDesktop -e --accept-package-agreements --accept-source-agreements
```

После установки требуется перезагрузка и вход в систему заново (WSL 2 backend).

Проверка:

```powershell
docker version
docker compose version
```

## Полный стек в контейнерах

```powershell
docker compose -f infra/docker-compose.yml up --build
```

Поднимаются PostgreSQL, Redis, Kafka и семь сервисов на портах 8081–8087.

## Kubernetes (опционально)

```powershell
kubectl apply -f infra/k8s/00-base.yaml
kubectl apply -f infra/k8s/10-data.yaml
kubectl apply -f infra/k8s/20-services.yaml
kubectl apply -f infra/k8s/30-scaling.yaml
kubectl get pods -n naimix-portfolio
```

## Без Docker: только JVM

Каждый сервис запускается независимо и использует H2 и in-memory аналоги:

```powershell
mvn -pl services/auth-service spring-boot:run
mvn -pl services/vacancy-service spring-boot:run
mvn -pl services/matching-service spring-boot:run
mvn -pl services/notification-service spring-boot:run
mvn -pl services/billing-service spring-boot:run
mvn -pl services/analytics-service spring-boot:run
mvn -pl services/ai-assistant-service spring-boot:run
```

## Фронтенд

```powershell
cd web
npm install          # уже выполнено, eslint установлен
npm run lint         # проверка JS правилами проекта
```

Сайт открывается напрямую из файловой системы:
`web/index.html` — портфолио, `web/dashboard.html` — демо сервисов.
Самодостаточные копии для отправки: `web/portfolio.html`, `web/dashboard-standalone.html`.
