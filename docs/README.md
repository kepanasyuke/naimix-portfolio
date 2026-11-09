# Портфолио backend-разработчика: семь сервисов

Репозиторий показывает полный backend-контур маркетплейса внештатных специалистов: от выпуска
токенов до эскроу-биллинга и ИИ-генерации текста. Всё запускается локально без внешних сервисов.

## Состав

| Модуль | Порт | Что демонстрирует |
| --- | --- | --- |
| `services/auth-service` | 8081 | Spring Security, OAuth 2.0 resource server, JWT, отзыв токенов, BCrypt |
| `services/vacancy-service` | 8082 | JPA, пагинация, кэш read-through, оптимистичная блокировка |
| `services/matching-service` | 8083 | TF-IDF, косинусная близость, детерминированный ранжировщик |
| `services/notification-service` | 8084 | идемпотентность по ключу, ретраи, парковка в DLQ-статус |
| `services/billing-service` | 8085 | холды, `PESSIMISTIC_WRITE`, инварианты денежных операций |
| `services/analytics-service` | 8086 | счётчики, агрегаты, метрики JVM, Actuator |
| `services/ai-assistant-service` | 8087 | провайдер генерации, скользящее окно квоты, 429 |
| `services/workspace-service` | 8090 | бизнес-оркестрация: черновик проекта, эскроу, приёмка, отмена |
| `web/` | — | портфолио, технические демо и кабинет заказчика в стиле сайта компании |

## Бизнес-процесс (workspace-service)

```
POST /api/workspace/projects              -> ИИ-описание + 5 ранжированных исполнителей
POST /api/workspace/projects/{id}/assign   -> эскроу HELD + уведомление исполнителю
POST /api/workspace/projects/{id}/complete -> эскроу CAPTURED (оплата зафиксирована)
POST /api/workspace/projects/{id}/cancel   -> эскроу RELEASED (средства возвращены)
GET  /api/workspace/overview               -> события аналитики + число вакансий
```

Сервис-оркестратор не хранит состояние сделки: источник истины — биллинг (холд), уведомления и вакансии.
Идемпотентность держится на ключах `hold-<vacancyId>` и `assign-<vacancyId>`.

### Проверенный сценарий на реальных PostgreSQL и Redis

| Шаг | Результат |
| --- | --- |
| Черновик проекта | вакансия создана, 5 кандидатов, топ-score 0.389 |
| Назначение | эскроу `HELD` на 12 000 ₽, уведомление создано и доставлено |
| Приёмка работы | эскроу `CAPTURED`, событие `project_completed` в аналитике |
| Отмена проекта | эскроу `RELEASED`, событие `project_cancelled` |


## Быстрый старт

```bash
# проверка всего: компиляция, тесты, стиль
mvn verify

# один сервис
mvn -pl services/matching-service spring-boot:run
curl -X POST http://localhost:8083/api/match \
  -H "Content-Type: application/json" \
  -d '{"text":"Java Spring PostgreSQL Kafka транзакции","limit":3}'

# всё окружение в контейнерах
docker compose -f infra/docker-compose.yml up --build

# фронтенд
cd web && npx serve .    # или любой статический сервер
```

## Проверки качества

- **Checkstyle** (`config/checkstyle/checkstyle.xml`) — фаза `verify`, падает сборка:
  `ReturnCount=1`, `ExecutableStatementCount=2`, запрет `break`/`continue`/`while`/`do`,
  `CyclomaticComplexity=3`, javadoc на публичных методах.
- **ESLint** (`web/eslint.config.mjs`) — те же ограничения для JS: `max-statements: 2`,
  `complexity: 3`, запрет циклов и переходов.
- **Тесты** — JUnit 5 и Mockito в каждом модуле.

## Документация

- `docs/adr` — шесть решений с контекстом, альтернативами и последствиями.
- `docs/c4` — контекст, контейнеры, компоненты, последовательности и состояния.

## Ограничения демо

- PostgreSQL и Redis в локальном профиле заменены на H2 и in-memory аналоги (см. ADR 0003, 0004).
- ИИ-фичи работают на локальных детерминированных движках (см. ADR 0005).
- Строгий лимит в два оператора делает код фрагментированным — это осознанный компромисс (ADR 0006).
