#  Observability  POC

### Requirements

- Docker
- Java 8+

### IDE Settings

- Maven
- VSCode Plugins
	- Lombok
	- Java Extension
- Docker
- Docker Compose	

### Kafka , Zookepper & Cassandra Setup

1. From Command Window, login to docker hub
```sh
	> docker login
```
2. Bring up Kafka , Zookepper & Cassandra containers
```sh
   > docker-compose up -d
```

### Cassandra Initialization

1. Create keyspace and table

```sh
> docker exec -it cassandra cqlsh
cqlsh> CREATE KEYSPACE reactive WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : '1'};
cqlsh> USE reactive;
cqlsh:reactive> CREATE TABLE item (id int, itemname text, amount text, primary key(id));
cqlsh:reactive> CREATE TABLE message (msgoffset text, message text, offset text, topic text, primary key(msgoffset));
cqlsh:reactive> DESCRIBE TABLE item;
```

### Open Telemetry Setup

1. Install and run Otel Exporters: Zipkin, Jaegar, and AWSXRay

```sh
> cd otel-collector
> docker-compose up
> curl -I http://localhost:9411/zipkin/
> curl -I http://localhost:16686
```

### Microservices Setup

1. Run Consume Microservice

```sh
> cd consumeMicroService
> mvn install
> export AWS_PROFILE=adfs; OTEL_RESOURCE_ATTRIBUTES=service.name=consume OTEL_EXPORTER=otlp OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:55680 java -javaagent:../otel-collector/opentelemetry-javaagent-all-v1.0.0.jar -jar target/*.jar
```

2. Run Adapter Microservice

```sh
> cd itemAdapterMicroService
> mvn install
> export AWS_PROFILE=adfs; OTEL_RESOURCE_ATTRIBUTES=service.name=itemAdapter OTEL_EXPORTER=otlp OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:55680 java -javaagent:../otel-collector/opentelemetry-javaagent-all-v1.0.0.jar -jar target/*.jar
```


3. Run Shopingcart Microservice

```sh
> cd shoppingCartService
> mvn install
> export AWS_PROFILE=adfs; OTEL_RESOURCE_ATTRIBUTES=service.name=shoppingCart OTEL_EXPORTER=otlp OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:55680 java -javaagent:../otel-collector/opentelemetry-javaagent-all-v1.0.0.jar -jar target/*.jar
```

### End-to-End Test

1. Invoke Shopingcart Service

```sh
> curl http://localhost:9090/shop
```

2. Check traces and metrics!

- [Zipkin](http://localhost:9411/zipkin/)
- [Jaeger](http://localhost:16686)

- [Prometheus](http://localhost:9045)
- [Grafana](http://localhost:3000)

