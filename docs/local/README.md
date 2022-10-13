## GemFire Setup


Start GemFire

cd /Users/devtools/repositories/IMDG/gemfire/vmware-gemfire-9.15.0/bin


```shell
start locator --name=locator --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
configure pdx --disk-store --read-serialized=true
start server --name=server1   --locators=127.0.0.1[10334]  --server-port=40404 --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1    
```


```shell
create lucene index --region=products --name=productIndex --field=productName
create region --name=products --type=PARTITION
deploy --dir=/Users/Projects/solutions/cloudNativeData/showCase/dev/retail-store-cloud-native-data/applications/libs
y
create region  --type=PARTITION --name=customerPromotions
create region  --type=PARTITION --name=customerFavorites
create region  --type=PARTITION --name=alerts
create region  --type=PARTITION --name=users
create region  --type=PARTITION --name=beaconProducts
create region  --type=PARTITION --name=beaconPromotions 
create region  --type=PARTITION --name=customerLocation
create region  --type=PARTITION --name=productAssociations
create region  --type=PARTITION --name=products
```

	


## Postgres Setup

Please use JDK version 11

Run migration to setup database

Change the following to reflect your local Postgres installation

- spring.datasource.url=...
- spring.datasource.username=...
- spring.datasource.password=...

```shell
java -jar applications/retail-analytics-data-migration/target/retail-analytics-data-migration-0.0.1-SNAPSHOT.jar --spring.datasource.url=jdbc:postgresql://localhost:5432/postgres  --spring.datasource.username=postgres  --spring.datasource.password=$POSTGRES_DB_PASSWORD --server.port=-1
```

## Execute the Streaming Applications

```shell
java -jar applications/retail-analytics-stream/target/retail-analytics-stream-0.0.4-SNAPSHOT.jar --spring.profiles.active=postgres --server.port=-1
```


# start application

```shell
java -jar applications/retail-store-app/target/retail-store-app-0.0.3.jar --server.port=2000
```


```shell
open http://localhost:2000
```

Create User
```yaml
username: Nyla
FirstName: Nyla
LastName: Nyla
```