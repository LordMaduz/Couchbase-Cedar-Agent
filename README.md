# Cedar Policy Authorization Agent

> Reactive authorization service implementing AWS Cedar policy language for fine-grained access control with Apache APISIX integration and Couchbase backend.

## 🎯 Overview

A production-ready authorization agent built with AWS Cedar policy language, providing centralized policy-based access control for microservices. Features dynamic policy evaluation, entity relationship management, and column-level data filtering with seamless Apache APISIX gateway integration.

**Key Features:**
- AWS Cedar policy language implementation
- Fine-grained authorization (entity, action, resource)
- Column-level data access control
- Dynamic entity relationships and attributes
- Apache APISIX custom plugin integration
- Reactive Couchbase backend for policies and entities
- RESTful API for policy and data management
- OpenAPI/Swagger documentation

---

## 🏗️ Architecture
```
%%{init: {'theme':'base', 'themeVariables': { 'fontSize':'12px'}}}%%
graph TB
subgraph Client["Client Applications"]
APP[Application]
endsubgraph Gateway["Apache APISIX"]
    APISIX[APISIX Gateway]
    CEDAR_PLUGIN[Cedar Plugin<br/>cedar.lua]
endsubgraph Agent["Cedar Agent :8070"]
    API[Authorization API]
    ENGINE[Cedar Engine]
    POLICY[Policy Service]
    DATA[Data Service]
    COLUMN[Column Mapping]
endsubgraph Storage["Couchbase Database"]
    CB[(cedar-policy bucket)]
    P_COLL[CedarPolicy<br/>collection]
    D_COLL[CedarData<br/>collection]
    C_COLL[ColumnMapping<br/>collection]
endAPP -->|HTTP Request| APISIX
APISIX --> CEDAR_PLUGIN
CEDAR_PLUGIN -->|POST /authorize| APIAPI --> ENGINE
ENGINE --> POLICY
ENGINE --> DATA
ENGINE --> COLUMNPOLICY --> P_COLL
DATA --> D_COLL
COLUMN --> C_COLLP_COLL --> CB
D_COLL --> CB
C_COLL --> CBAPI -->|Allow/Deny| CEDAR_PLUGIN
CEDAR_PLUGIN -->|Response| APPstyle Client fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
style Gateway fill:#fff3e0,stroke:#f57c00,stroke-width:2px
style Agent fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
style Storage fill:#e8f5e9,stroke:#388e3c,stroke-width:2px
```
---

## Tech Stack

| Category | Technologies |
|----------|-------------|
| **Core** | Java 17, Spring Boot 3.0.6 |
| **Reactive** | Spring WebFlux, Project Reactor |
| **Policy Engine** | AWS Cedar (Java SDK) |
| **Database** | Couchbase 7.0+ (Reactive) |
| **Gateway** | Apache APISIX (Lua Plugin) |
| **API Docs** | SpringDoc OpenAPI 3 |

---

## Getting Started

### Prerequisites
```bash
Java 17+
Maven 3.8+
Couchbase Server 7.0+
Apache APISIX 3.x (optional, for gateway integration)
```

### Couchbase Setup

Using Docker
```bash
docker run -d --name couchbase 
-p 8091-8096:8091-8096 
-p 11210:11210
```
#### couchbase:
- latestAccess UI: http://localhost:8091
- Create bucket: cedar-policy
- Create scope: dev
- Create collections: CedarPolicy, CedarData, ColumnMapping

### Installation

#### 1. Build Cedar Java Commons
This is a custom library dependency
```bash
cd cedar-java-commons
mvn clean install
```

#### 2. Build Application
```bash
cd Cedar-Policies
mvn clean install
```

#### 3. Configure Application

**application.yaml:**

```yml
server:
  port: 8070

app:
  couchbase:
    connection-string: couchbase://localhost
    user-name: Administrator
    password: password
    bucketName: cedar-policy
    scopeName: dev

spring:
  application:
    name: cedar-agent
  main:
    web-application-type: reactive
  webflux:
    base-path: /api/cedar-agent
```

#### 4. Run Application
```bash
mvn spring-boot:run
```
Application starts on `http://localhost:8070`

**Swagger UI:** `http://localhost:8070/webjars/swagger-ui/index.html`

---

## 🔧 API Usage

### 1. Create Policy

**Request:**
```bash
curl -X POST http://localhost:8070/api/cedar-agent/policy \
  -H "Content-Type: application/json" \
  -d '{
    "serviceId": "TradeService",
    "policy": "permit(principal in AuthorizationGroup::\"Authorization\", action == Action::\"ViewTrade\", resource == Collection::\"TradeCollection\") when {resource.allowedUsers.contains(principal.id)};"
  }'
```

### 2. Create Entity Data

**Request:**
```bash
curl -X POST http://localhost:8070/api/cedar-agent/data/TradeService \
  -H "Content-Type: application/json" \
  -d '[
    {
      "uid": {"type": "SecurityGroup", "id": "TMOP_SG_ADMIN"},
      "attrs": {"id": "TMOP_SG_ADMIN"},
      "parents": [{"type": "AuthorizationGroup", "id": "Authorization"}]
    },
    {
      "uid": {"type": "Collection", "id": "TradeCollection"},
      "attrs": {"allowedUsers": ["TMOP_SG_ADMIN", "TMOP_SG_OWNER"]},
      "parents": []
    }
  ]'
```

### 3. Authorize Request

**Request:**
```bash
curl -X POST http://localhost:8070/api/cedar-agent/authorize \
  -H "service-id: TradeService" \
  -H "security-group: TMOP_SG_ADMIN" \
  -H "action-id: ViewTrade" \
  -H "resource-id: TradeCollection"
```
