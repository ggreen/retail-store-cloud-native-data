# Cloud Native Data Demo

[http://pivotmarketapp.apps.pcfone.io](http://pivotmarketapp.apps.pcfone.io)


Strawman Scenario/Architecture
Retail supermarket data driven app

There are BlueTooth Beacons throughout the store tracking customers as they move through the store.   This is a simplification.  Some  beacons are  merely transmitters.  The mobile apps, connected via BlueTooth receive the signals and respond both in the app as well as sending some data back to a customer database.     

The app constantly receives signals, connects back to a database that stores the customer id, location, and timestamp.   The customer id is part of a loyalty program for which we have a history of customer orders 

For the first version (MVP), we envision a small supermarket with a beacon at the entry, several around the store, say one on each aisle, one prior to checkout, and one at checkout.  Here’s one possible supermarket layout.   See this diagram for another set of beacons.   Simulated customers will enter the store, have randomly assigned paths through the stores with random inter-arrival times at the beacons, and eventually make their way to the checkout beacon.  They will then check out and their basket list will be stored.  My understanding of beacons is that their transmission times are configurable.  We might try N  seconds as the interval and detect if a customer is staying in an area if they get M consecutive beacon hits.  What are reasonable values of N and M?  We are under no obligation to mimic real beacon activity.  Anything simple and realistic works in the MVP.

Goal:  on the basis of store location, past buying history, and dwell time in certain locations, some logic will alert customers on their mobile device to savings on products while in the store (short half life).  For example:
Customer is nearing checkout line.  The customer frequently buys Leonard’s Garlic Flavored Yoghurt.  Tell customer that it’s on special for them in the next 20 minutes.
Customer usually buys hot dogs.  Customer who buy hot dogs usually buy buns, relish, and mustard.  Remind customer.  

Send these as notification to mobile device associated with customer id. 

While in the store, the customer’s path is calculated via the beacons and the phone app. The app will send location information back to GemFire.  

Maybe GemFire can send have a server side function that sends something back to the app as mentioned above.

Resources required:
Data generator for customer arrivals and paths through the store
Customer  database with customer id, mobile phone id., past buying history
Cache to hold generated data from beacon hits and scanners, orchestrated by SCDF/Kafka
Analytics to suggest real time offers to customers.  
Analytics to send messages to supermarket’s mobile app when not in store
Mobile phone simulator to receive in store real time messages.  

Pivotal architecture  (picture preferred - plain text for the moment)
Data generators for beacons and (apps running in PCF)  flows into
Spring Cloud DF and Kafka running in PCF which flows into
GemFire running in PCF and GP (running as a tile in PCF)
Some server side functions in GemFire that send messages to mobiles ?
based on rules generated by analytics discovered by ML in GP
GemFire pushes data to GP to refine model and rules.
GP pushes suggestions to GemFire for customer notification as they enter the store

Ideas deferred for a next version:
Hand held scanners used by customers.  They are attached wirelessly to a local store device and then back to the enterprise database.  Customers use the scanners as they walk through the store, have their credit card on file, and can avoid the checkout lines.  We could envision a scenario in which if they scan pastrami and rye bread, we could suggest adding swiss cheese or the like.
For a future version we could also have online shopping as well.


	
	
# GemFire Notes
	
	cf create-service-key pcc-dev-plan pcc-dev
	cf service-key pcc-dev-plan pcc-dev
	
	create region --name=/alerts --type=PARTITION
		
		
	cf bind-service pivotMarketApp pcc-dev-plan
	cf restage pivotMarketApp
	
	
	
## SCDF


SCDF, GemFire and initial web application instances deployed to PCFONe.
I needed to deploy Kafka outside of PCFONe.
It is currently deployed on AWS ec2-35-174-17-30.compute-1.amazonaws.com:9092
with a topic “beacon”.

The current application is deployed here:
	
	https://pivotmarketapp.apps.pcfone.io/
	
The backend pieces of Kafka, Greenplum and SCDF still need to be connected.

Paul will you be able to integrate your latest backend code into the UI using the instructions below. Will you have sometime tomorrow to chat more?


 Access PCFONE
 
  Goto https://pivotal.okta.com/app/UserHome

	Search PCFONe
	(you must request to add it if you have not done so already)
	
	Select -> PCEOne Ignition
	
	Select -> My Org -> signin with Okta
	
	On left hand side ORG -> group-pde-dev
	
	spec - cloud-demo
	
	--------------------
	

CF Login

	  
	cf login -a api.run.pcfone.io --sso 
	cf dataflow-shell scdf	

## UI integration



The UI will push a Customer/Beacon message to the AWS kafka beacon topic

For messages coming back to the UI, currently the UI will display in near realtime records written to a GemFire region.


## GemFire

    start locator --name=locator --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1  --jmx-manager-hostname-for-clients=127.0.0.1 --http-service-bind-address=127.0.0.1
    configure pdx --disk-store --read-serialized=true
    start server --name=server   --locators=127.0.0.1[10334] --bind-address=127.0.0.1 --hostname-for-clients=127.0.0.1 --start-rest-api=true --http-service-bind-address=127.0.0.1 --http-service-port=9090
    
    run --file=/Users/Projects/solutions/cloudNativeData/showCase/dev/retail-store-cloud-native-data/database/geode/scripts/gfsh/setup.gfsh


#### GemFire Setup details

	cf bind-service pivotMarketStreams  pcc-dev-plan
	
## K8 Setup details


```shell
mvn install
cd applications/retail-store-app
mvn spring-boot:build-image
```


docker tag retail-store-app:0.0.3 cloudnativedata/retail-store-app:0.0.3
docker login
docker push cloudnativedata/retail-store-app:0.0.3

```shell
kubectl apply -f cloud/k8/applications/retail-store-app.yml
```



```shell
kubectl port-forward deployment/retail-store-app  8080:8080
```

```shell
open http://localhost:8080/
```


Register User/Password

```properties
User=Nyla
Password=CHANGEME
First_Name=Nyla
Last_Name=Nyla
Email=Nyla
```


Login

```properties
User=Nyla
Password=CHANGEME
```