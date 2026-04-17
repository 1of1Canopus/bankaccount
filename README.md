# Bank Account

🌐 Disponible en :
[🇫🇷 Français](README.md) | [🇬🇧 English](README.en.md)

Application de gestion de comptes bancaires développée en Java 21 / Spring Boot 3, selon les principes de l'**architecture hexagonale**.

---

## Fonctionnalités

- **Compte courant** — création avec limite de découvert configurable, dépôt et retrait
- **Livret d'épargne** — création avec plafond de dépôt, aucun découvert autorisé
- **Règles métier** — retrait refusé si le solde (découvert inclus) est dépassé ; dépôt refusé si le plafond du livret est atteint
- **Audit des opérations** — historique glissant sur 30 jours, trié en ordre antéchronologique, accessible par numéro de compte

---

## Stack technique

| Couche | Technologie |
|---|---|
| Langage | Java 21 |
| Framework | Spring Boot 3.4.5 |
| Documentation API | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, Spring MockMvc |
| Conteneurisation | Docker (multi-stage build) |
| CI/CD | GitHub Actions |

---

## Architecture

Le projet suit l'**architecture hexagonale** (Ports & Adapters) :

```
domain/          → modèle métier pur (Account, CurrentAccount, SavingsAccount, BankOperation)
domain/port/     → interfaces des cas d'usage (DepositUseCase, WithdrawUseCase, ...)
application/     → implémentation des cas d'usage (AccountService)
infrastructure/  → adapteurs REST (controllers, DTOs, mappers) et persistance en mémoire
```

Le domaine ne dépend d'aucun framework. Spring ne touche qu'à la couche infrastructure.

---

## Lancer l'application

**Avec Maven :**
```bash
mvn spring-boot:run
```

**Avec Docker :**
```bash
docker build -t bank-account .
docker run -p 8080:8080 bank-account
```

L'API est disponible sur `http://localhost:8080`.
La documentation Swagger est accessible sur `http://localhost:8080/swagger-ui.html`.

---

## API

| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/accounts` | Créer un compte courant |
| GET | `/accounts/{accountNumber}` | Consulter un compte courant |
| POST | `/accounts/{accountNumber}/deposit` | Effectuer un dépôt |
| POST | `/accounts/{accountNumber}/withdraw` | Effectuer un retrait |
| GET | `/accounts/{accountNumber}/audit` | Consulter l'historique des opérations |
| POST | `/savings-accounts` | Créer un livret d'épargne |
| GET | `/savings-accounts/{accountNumber}` | Consulter un livret |
| POST | `/savings-accounts/{accountNumber}/deposit` | Effectuer un dépôt sur livret |
| POST | `/savings-accounts/{accountNumber}/withdraw` | Effectuer un retrait sur livret |
| GET | `/savings-accounts/{accountNumber}/audit` | Consulter l'historique du livret |

![architecture hexagonale](./assets/hexa-schema.png)
