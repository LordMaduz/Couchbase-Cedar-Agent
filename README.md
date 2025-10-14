# Cedar Policy Authorization Agent

> Reactive authorization service implementing AWS Cedar policy language for fine-grained access control with Apache APISIX integration and Couchbase backend.

## Overview

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

## Architecture
<img width="1043" height="2023" alt="Mermaid Chart - Create complex, visual diagrams with text -2025-10-14-103426" src="https://github.com/user-attachments/assets/44e1a1b9-e864-4384-9fe4-f1eb071dce18" />

---

## Authorization Flow
<img width="2276" height="2448" alt="Mermaid Chart - Create complex, visual diagrams with text -2025-10-14-103257" src="https://github.com/user-attachments/assets/e060b0de-b7fc-44fc-8a70-fc6b607dd99d" />

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

## API Usage

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

## Key Features

### Cedar Authorization Engine

Evaluates policies using AWS Cedar engine:
```java
@Service
public class AuthorizationService {
    
    private final AuthorizationEngine authorizationEngine = new WrapperAuthorizationEngine();

    public Mono authorize(String securityGroup, String serviceId,
                                       String actionId, String resourceId,
                                       String context, String dataObject,
                                       boolean isColumnLevelValidation) {
        
        // Load policies from database
        return policyRepository.findByServiceId(serviceId)
            .collectList()
            .flatMap(policies -> {
                
                // Load entity data
                Set entities = generateEntitySet(entityList);
                Set policySet = convertToCedarPolicies(policies);
                
                BasicSlice slice = new BasicSlice(policySet, entities);
                
                // Create authorization query
                AuthorizationQuery query = new AuthorizationQuery(
                    Optional.of(principalUid),
                    actionUid,
                    Optional.of(resourceUid),
                    contextMap,
                    Optional.empty()
                );
                
                // Evaluate
                AuthorizationResult result = authorizationEngine.isAuthorized(query, slice);
                
                return Mono.just(buildResponse(result));
            });
    }
}
```

### Policy Syntax

Cedar policies use a declarative syntax:
```cedar
permit(
  principal in AuthorizationGroup::"Authorization",
  action == Action::"ViewTrade",
  resource == Collection::"TradeCollection"
)
when {
  resource.allowedUsers.contains(principal.id)
};
```

### Apache APISIX Plugin

Custom Lua plugin for gateway integration:
```lua
-- custom-plugins/cedar.lua
function _M.access(conf, ctx)
    local request_header = core.request.headers(ctx)
    request_header["Content-Type"] = "application/json"
    request_header["service-id"] = conf.service

    local endpoint = conf.host .. "/api/cedar-agent/authorize"
    local res, err = httpc:request_uri(endpoint, params)

    -- Parse decision
    local data = core.json.decode(res.body)
    
    if not data.result.allow then
        return 403, data.result.reason
    end
end
```

**APISIX Route Configuration:**
```yaml
routes:
  - uri: /api/trade/*
    plugins:
      cedar:
        host: http://localhost:8070
        service: TradeService
        ssl_verify: false
```

## Policy Examples

### Basic Permission
```cedar
permit(
  principal == SecurityGroup::"TMOP_SG_ADMIN",
  action == Action::"View",
  resource == Service::"TradeService"
);
```

### Attribute-Based Access Control (ABAC)
```cedar
permit(
  principal in AuthorizationGroup::"Authorization",
  action == Action::"ViewTrade",
  resource == Collection::"TradeCollection"
)
when {
  resource.allowedUsers.contains(principal.id)
};
```

### Context-Based Authorization
```cedar
permit(
  principal in ServiceGroup::"Service",
  action,
  resource
)
when {
  context.authorised == true
};
```
## 📖 Learn More

For comprehensive guides on policy-based authorization and Cedar implementation:

**[Implementing Fine-Grained Authorization with AWS Cedar](https://blog.stackademic.com/a-look-in-to-aws-cedar-policy-language-1ac978ad5042)**


