# spring-microservices-demo

Monorepo Maven com **Spring Boot 3.1** e **Spring Cloud 2022.0.x**: três microserviços de domínio (**peças**, **clientes**, **representantes**), **API Gateway**, **Eureka** (service discovery) e **Config Server** (configuração centralizada).

## Como subir (ordem)

1. **Config Server** — porta `8888` (opcional se usar só o YAML embarcado)  
   `mvn -pl config-server spring-boot:run`
2. **Eureka** — porta `8761`  
   `mvn -pl eureka-server spring-boot:run`
3. **Microserviços** (ordem livre):  
   `mvn -pl pecas-service spring-boot:run`  
   `mvn -pl clientes-service spring-boot:run`  
   `mvn -pl representantes-service spring-boot:run`
4. **Gateway** — porta `8080` (único ponto de entrada para o cliente)  
   `mvn -pl api-gateway spring-boot:run`

Cada módulo carrega `application.yml` com portas, datasource e integração. O Config Server (YAML em `config-server/src/main/resources/config/`, perfil `native`) continua disponível: para aplicar o import centralizado, suba o Config Server e inicie o serviço com **`--spring.profiles.active=cloud`** (há `application-cloud.yml` em cada app).

## Testar só pelo Gateway

- Base URL: `http://localhost:8080`
- Coleção Postman: `postman/Gateway-API.postman_collection.json`
- Página simples: abra `test-ui/index.html` no navegador (CORS liberado no Gateway)

## APIs (via Gateway)

| Recurso | Métodos |
|--------|---------|
| `/api/pecas` | `POST` cadastro; `GET` listar; `GET ?nome=` busca por nome; `GET /{id}` por id |
| `/api/clientes` | `POST` cadastro; `GET` listar; `GET ?nome=`; `GET /{id}`; `GET /cpf/{cpf}` |
| `/api/representantes` | Igual clientes, sob `/api/representantes` |

CPF é armazenado só com dígitos (pontuação é ignorada no cadastro e na busca).

## Observabilidade (Prometheus + Grafana)

Todos os serviços expõem métricas Micrometer em **`/actuator/prometheus`** (Actuator + registry Prometheus), com tag comum `application` para filtrar no Grafana.

1. Suba os microserviços no **host** (portas `8888`, `8761`, `8080`–`8083` como no README).
2. Suba os containers:

   `docker compose -f docker-compose.observability.yml up -d`

3. **Prometheus:** [http://localhost:9090](http://localhost:9090) — targets em *Status → Targets* (`host.docker.internal`).
4. **Grafana:** [http://localhost:3000](http://localhost:3000) — usuário `admin`, senha `admin`. O datasource *Prometheus* e o dashboard *Spring / Micrometer* são provisionados automaticamente.

Arquivos: `observability/prometheus/prometheus.yml`, `observability/grafana/provisioning/`. Em Linux sem `host.docker.internal`, ajuste os targets para o IP da máquina host ou use a mesma rede Docker dos apps.

## Testes (unitário + integração)

Nos módulos `pecas-service`, `clientes-service` e `representantes-service` há:

- **Serviços** (`*ServiceTest`): Mockito, sem Spring, repositório mockado.
- **Controllers** (`*ControllerTest`): `@WebMvcTest` + `MockMvc`, serviço mockado (`@MockBean`), web isolado.
- **Persistência** (`*RepositoryTest`): `@DataJpaTest` + H2, só JPA/repositórios.
- **HTTP integração** (`*HttpIntegrationTest`): `@SpringBootTest` + `MockMvc`, perfil de teste `spring.config.name=*-service-test` (H2, sem Eureka/Config).

O POM raiz configura o Surefire com `-Dnet.bytebuddy.experimental=true` para suportar **Java 22+** com Mockito (ex.: JDK 25 no macOS Homebrew).

## Build

Na raiz do repositório: `mvn verify`

Os módulos antigos `microservico1` e `microservico2` não fazem parte do POM pai; o trabalho novo está nos módulos listados no `pom.xml` raiz.
