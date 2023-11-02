testing Hazelcast with different configurations
---

This project is a test of Hazelcast with different configurations.
There is a Medium article that explains the results of this test.
https://medium.com/@kgignatyev/hazelcast-performance-for-distributed-search-889636f4106f



# Run Multiple instances to form a cluster

Run one line per terminal

```bash
sbr -Dspring-boot.run.arguments=--server.port=8080
sbr -Dspring-boot.run.arguments=--server.port=8081
sbr -Dspring-boot.run.arguments=--server.port=8082
```

Example results:
----

Single node summary:

![single-node-summary.png](docs%2Fsingle-node-summary.png))

3 nodes with 100K dataset summary

![3node100k-summary.png](docs%2F3node100k-summary.png)

3 nodes with 300K dataset summary

![3node300k-summary.png](docs%2F3node300k-summary.png)
