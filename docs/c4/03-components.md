# C4 уровень 3. Компоненты: matching-service и billing-service

## matching-service

```mermaid
flowchart LR
    C[MatchController] --> S[MatchService]
    S --> IDX[TfIdfIndex]
    S --> CAT[FreelancerCatalog]
    IDX --> TV[TextVector]
    S --> SIM[Similarity]
    SIM --> TV
```

| Компонент | Ответственность |
| --- | --- |
| MatchController | приём запроса, валидация текста |
| MatchService | векторизация запроса, ранжирование, отсечение по limit |
| TfIdfIndex | словарь и веса IDF по корпусу профилей |
| TextVector | токенизация и частоты |
| Similarity | косинусная близость с защитой от нулевой нормы |
| FreelancerCatalog | каталог профилей (в демо — в памяти) |

## billing-service

```mermaid
flowchart LR
    BC[BillingController] --> BS[BillingService]
    BS --> WR[WalletRepository<br/>PESSIMISTIC_WRITE]
    BS --> HR[PaymentHoldRepository]
    WR --> W[Wallet]
    HR --> H[PaymentHold]
    W --> EX[InsufficientFundsException]
    H --> ST[IllegalHoldStateException]
```

| Компонент | Ответственность |
| --- | --- |
| BillingController | REST-контракт кошельков и холдов |
| BillingService | идемпотентность по ключу, порядок операций |
| Wallet | инвариант «баланс не отрицателен» |
| PaymentHold | конечный автомат HELD → CAPTURED/RELEASED |
| WalletRepository | блокировка строки на время транзакции |

## Жизненный цикл холда

```mermaid
stateDiagram-v2
    [*] --> HELD: POST /wallets/{id}/holds
    HELD --> CAPTURED: POST /holds/{id}/capture
    HELD --> RELEASED: POST /holds/{id}/release (возврат средств)
    CAPTURED --> [*]
    RELEASED --> [*]
```

Повторный `capture` или `release` невозможен: `guardHeld()` бросает `IllegalHoldStateException`,
контроллер возвращает 409.
