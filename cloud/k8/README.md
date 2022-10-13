Building local images


```shell script
mvn install
cd applications/retail-store-app

mvn spring-boot:build-image

cd ../retail-processor-orders-stream
mvn spring-boot:build-image

cd ../retail-analytics-stream
mvn spring-boot:build-image


cd ../retail-analytics-data-migration
mvn spring-boot:build-image
```




```shell script
docker tag retail-store-app:0.0.3 cloudnativedata/retail-store-app:0.0.3 
docker login
docker push cloudnativedata/retail-store-app:0.0.3
```

```shell script
docker tag retail-analytics-data-migration:0.0.1-SNAPSHOT cloudnativedata/retail-analytics-data-migration:0.0.1-SNAPSHOT 
docker push cloudnativedata/retail-analytics-data-migration:0.0.1-SNAPSHOT
```

```shell script
docker tag retail-analytics-stream:0.0.4-SNAPSHOT cloudnativedata/retail-analytics-stream:0.0.4-SNAPSHOT
docker push cloudnativedata/retail-analytics-stream:0.0.4-SNAPSHOT
```

```shell script
docker tag retail-processor-orders-stream:0.0.2-SNAPSHOT cloudnativedata/retail-processor-orders-stream:0.0.2-SNAPSHOT
docker push cloudnativedata/retail-processor-orders-stream:0.0.2-SNAPSHOT
```

## Gemfire on k8

From root project directory, execute the following

```shell script
./cloud/k8/data-services/gemfire/gf-k8-setup.sh
```
Then Execute

```shell
./cloud/k8/data-services/gemfire/gf-app-setup.sh
```

## Postgres

Execute for project root

```shell
./cloud/k8/data-services/postgres/bitnami/postgres-bitnami.sh
```



## RabbitMQ


```shell
./cloud/k8/data-services/rabbitmq/rabbit-k8-setup.sh
```

```shell
k apply -f cloud/k8/data-services/rabbitmq
```
## Applications

```shell
k apply -f cloud/k8/applications
```

```shell
k port-forward retail-store-app-799bd57cf5-mhrkk 8080:8080
```


```shell
open http://localhost:8080
```


## Troubleshooting

kubectl exec -it gemfire1-locator-0 -- bash

## Cleanup

k delete -f cloud/k8/applications


k delete -f cloud/k8/data-services/rabbitmq

helm uninstall postgres 

bitnami/postgresql