testing Hazelcast with different configurations
---


# Run Multiple instances to form a cluster

sbr -Dspring-boot.run.arguments=--server.port=8080
sbr -Dspring-boot.run.arguments=--server.port=8081
sbr -Dspring-boot.run.arguments=--server.port=8082
