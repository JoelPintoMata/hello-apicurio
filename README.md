# Apicurio

## Overview

|     |                                                                                                                                                                                        |
| --- |----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| URL | [https://www.apicur.io](https://www.apicur.io/registry/download/)                                                                                                                      |
| Docker file | docker pull apicurio/apicurio-registry-mem:2.3.1.Final                                                                                                                                 |
| Formats | Apache Avro, Google Prx§otobuf, GraphQL, AsyncAPI, OpenAPI, and others                                                                                                                 |
| Storage | SQL, Kafka                                                                                                                                                                             |
| REST | Yes                                                                                                                                                                                    |
| UI  | Yes (http://localhost:8080/ui/artifacts)                                                                                                                                               |
| Schema Governance | Validity: yes<br>Compatibility: NONE, BACKWARD, BACKWARD\_TRANSITIVE, FORWARD, FORWARD\_TRANSITIVE, FULL, FULL_TRANSITIVE                                                              |
| Schema deprecation | yes: https://www.apicur.io/registry/docs/apicurio-registry/2.3.x/assets-attachments/registry-rest-api.htm#tag/Artifacts/operation/updateArtifactState<br>(check what it translates to) |
| License | [Apache License 2.0](https://choosealicense.com/licenses/apache-2.0)                                                                                                                   |

## How to use this repo?

Get apicurio Docker image:

```
docker pull apicurio/apicurio-registry-mem:2.3.1.Final
```

run it:

```
docker run -it -p 8080:8080 apicurio/apicurio-registry-mem:2.3.1.Final
```

Add a schema

```
curl -X POST -H "Content-type: application/json; artifactType=AVRO" -H "X-Registry-ArtifactId: my-kafka-topic-value" --data '{"type":"record","name":"thing","namespace":"com.example","fields":[{"name":"code","type":"string"},{"name":"title","type":"string"}]}' http://localhost:8080/apis/registry/v2/groups/default/artifacts
```

Start your consumer. Once started you will see logs detailing your outgoing message and a `globalId` pointing to the specific schema/version being used.

Start your producer. Once started you will see logs detailing your incoming message and a `globalId` pointing to the specific schema/version being used.

## Schema management

Iif you wish to update the schema:

```
curl -X PUT -H "Content-type: application/json; artifactType=AVRO" -H "X-Registry-ArtifactId: my-kafka-topic-value" --data '{"type":"record","name":"thing","namespace":"com.example","fields":[{"name":"code","type":"string"},{"name":"title","type":"string"},{"name":"description","type":"string"}]}' http://localhost:8080/apis/registry/v2/groups/default/artifacts/my-kafka-topic-value
```

