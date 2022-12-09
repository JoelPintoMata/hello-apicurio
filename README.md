# Apicurio

## Overview

|     |     |
| --- | --- |
| URL | [https://www.apicur.io](https://www.apicur.io/registry/download/) |
| Docker file | docker pull apicurio/apicurio-registry-mem:2.3.1.Final |
| Formats | Apache Avro, Google Protobuf, GraphQL, AsyncAPI, OpenAPI, and others |
| Storage | SQL, Kafka |
| REST | Yes |
| UI  | Yes (http://localhost:8080/ui/artifacts) |
| Schema Governance | Validity: yes<br>Compatibility: NONE, BACKWARD, BACKWARD\_TRANSITIVE, FORWARD, FORWARD\_TRANSITIVE, FULL, FULL_TRANSITIVE |
| Schema deprecation | yes: https://www.apicur.io/registry/docs/apicurio-registry/2.3.x/assets-attachments/registry-rest-api.htm#tag/Artifacts/operation/updateArtifactState<br>(check what it translates to) |
| License | [Apache License 2.0](https://choosealicense.com/licenses/apache-2.0) |

## Docker

Docker pull

```
docker pull apicurio/apicurio-registry-mem:2.3.1.Final
```

Docker run

```
docker run -it -p 8080:8080 apicurio/apicurio-registry-mem:2.3.1.Final
```

## Schema management

Add schema

```
curl -X POST -H "Content-type: application/json; artifactType=AVRO" -H "X-Registry-ArtifactId: my-kafka-topic-value" --data '{"type":"record","name":"thing","namespace":"com.example","fields":[{"name":"code","type":"string"},{"name":"title","type":"string"}]}' http://localhost:8080/apis/registry/v2/groups/default/artifacts
```

Update version

```
curl -X PUT -H "Content-type: application/json; artifactType=AVRO" -H "X-Registry-ArtifactId: my-kafka-topic-value" --data '{"type":"record","name":"thing","namespace":"com.example","fields":[{"name":"code","type":"string"},{"name":"title","type":"string"},{"name":"description","type":"string"}]}' http://localhost:8080/apis/registry/v2/groups/default/artifacts/my-kafka-topic-value
```