# C4 уровень 2. Контейнеры

```mermaid
flowchart TB
    subgraph Клиенты
        WEB[Портфолио и демо-дашборд<br/>HTML/CSS/ES-модули]
    end

    subgraph Платформа
        AUTH[auth-service :8081<br/>Spring Security, JWT]
        VAC[vacancy-service :8082<br/>JPA, кэш]
        MATCH[matching-service :8083<br/>TF-IDF, cosine]
        NOTIF[notification-service :8084<br/>идемпотентность, ретраи]
        BILL[billing-service :8085<br/>холды, блокировки]
        ANL[analytics-service :8086<br/>счётчики, JVM]
        AI[ai-assistant-service :8087<br/>провайдер генерации]
    end

    subgraph Данные и инфраструктура
        PG[(PostgreSQL)]
        RD[(Redis)]
        KF[[Kafka]]
        ING[Ingress nginx]
    end

    WEB --> ING
    ING --> AUTH
    ING --> VAC
    ING --> MATCH
    ING --> NOTIF
    ING --> BILL
    ING --> ANL
    ING --> AI

    AUTH --> PG
    AUTH --> RD
    VAC --> PG
    VAC --> RD
    NOTIF --> PG
    BILL --> PG
    ANL --> RD
    NOTIF -.события.-> KF
    KF -.подписка.-> ANL
```

## Ответственность контейнеров

| Контейнер | Порт | Состояние | Ключевой риск |
| --- | --- | --- | --- |
| auth-service | 8081 | PostgreSQL + Redis | компрометация секрета подписи |
| vacancy-service | 8082 | PostgreSQL + кэш | расхождение кэша и БД |
| matching-service | 8083 | in-memory индекс | рост каталога профилей |
| notification-service | 8084 | PostgreSQL | дубли доставки при ретраях |
| billing-service | 8085 | PostgreSQL | конкуренция за кошелёк |
| analytics-service | 8086 | Redis или память | потеря счётчиков при рестарте |
| ai-assistant-service | 8087 | память | злоупотребление квотой |

## Поток запроса «опубликовать вакансию»

```mermaid
sequenceDiagram
    participant U as Заказчик
    participant I as Ingress
    participant V as vacancy-service
    participant P as PostgreSQL
    participant A as analytics-service

    U->>I: POST /api/vacancies
    I->>V: проксирование
    V->>V: валидация payload
    V->>P: insert vacancy
    P-->>V: id
    V-->>I: 201 Created
    I-->>U: JSON
    V-->>A: POST /api/analytics/events
    A-->>A: инкремент vacancy_created
```
