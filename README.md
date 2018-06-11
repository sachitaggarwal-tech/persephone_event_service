# persephone_event_service

## Description
This app enables user to subscribe for specific queue of enterprise messaging service and process events. 

[Enterprise Messaging Service](https://wiki.wdf.sap.corp/wiki/display/CoCo/Enterprise+Messaging+on+SAP+CP+CF+-+Getting+Started) available in SAP Cloud Platform CF enables users to implement event-driven architecture.Key features of messaging service are - 
1. Java API to send and recieve events. 
2. Native integration with S4Hana to fire events from S4Hana and consume same in SAP cloud platform

# Table of Contents
* [Installation](README.md#2.Installation)
* [Running Application](README.md#3.Running-Application)
* [Contribution Guide](README.md#4.Contributing)
* [Code Walkthrough](README.md#5.Code-Walkthrough)


# 1.Installation
## Create new messaging instance
1. Login to the Cloud Foundry trial account : https://account.hanatrial.ondemand.com/cockpit#/home/trialhome
2. Click on 'Cloud Foundry Trial' to go to the Cloud Foundry Global Account.
3. Click on 'trial' to go to the trial sub-account.
4. Click on 'spaces' and select your space
4. In the Left pane, expand 'Service Marketplace' below Services
5. Select 'Enterprise Messaging' tile
6. Go to Instances
7. Click on new instance
8. In new instance wizard , for specify parameters  , specify below json
  {"emname": "messaging-prod"}
9. In confirm also give same name
10. Open dashboard for new instance from actions column
11. Select 'Queues' from left pane in dashboard and create new queue with name - "Persephone_Queue"

Note :- For calling S4Hana OData API , you need to create communication user and arrangement in S4Hana , refer [here](https://help.sap.com/viewer/f544846954f24b9183eddadcc41bdc3b/1705%20500/en-US/2e84a10c430645a88bdbfaaa23ac9ff7.html)

Note :- Name of destination service instance should be 'dest-service' , in case you create a service instance with different name please update same in manifest.yml file


##  Get project sources in Web IDE 
1. Right click on workspace in your WebIDE 'Files' navigation view and select Git > Clone Repository
2. Specify this repo URL
3. Project should be imported and available in your workspace
4. Select project , right click and go to project settings and reinstall builder . Wait till installation is done , once installation is done , click save and close
5. Select project and perform build action using right click
6. If build is succesful it will generate MTA folder for your project in workspace
7. Navigate to mta file for your project in MTA folder and click on deploy. Select required CF space to deploy app

##  Get project sources local and deploy (Optional)
1. Use git clone \<project URL.git\>_ to download the zip of this project to a local folder. 
2. Navigate to 'eventconsumer' folder
3. Open a command window. 
4. Adapt application name and host    
5. Build the jar file using maven
    ```
    mvn clean install
    ```
6. Via console login to your account. eg. if working on Europe: 
    ```
    cf api https://api.cf.eu10.hana.ondemand.com
    cf login 
    ```
    >**Hint:** If you want to find out which target are you currently using:
    > ```
    >  cf target
    >  ```
7. Run the command **push** of the **CLI**:
    ```
    cf push
    ```


   
# 3.Running Application
* Open Events APP - https://<cf app url deployed above>/messages
* Postman Setup
    * Import attached [postman collection](docs/Events_Demo.postman_collection.json) and try
* Send Messages
    * Update URL in postman request - 'Send Msg to Q persephone'
    * Click on send , if response is 202 ; message was sent to Q successfully.
    * Refresh events app and you should be able to see your URL
    * Deploy above events app , with another host name too ; so that you can see message being recieved by multiple subscribers


# 4.Code Walkthrough

* Package com.example.demo - Main package which contains actual events subscription and handling ligic
    * DemoApplication is our base sprint application
    * MessageController hosts REST servlet with get and post message to send messages to queue or reads messages recieved 
    * MessageEvent is model to hold message
    * [MessageService](/event-consumer-app/src/main/java/com/example/demo/MessageService.java) is main event service class which subscribes to CF event service 
Below classes are for complete Persephone Events scenario to be used later -
* Package com.example.base.destination
    * [DestinationAccessor](/event-consumer-app/src/main/java/com/example/base/destination/DestinationAccessor.java) class is used to read destination data for your CF account
* Package com.example.base.utils
    * API Handler class is a wrapper over springframework http to fire get/post calls
* Package com.example.S4Hana
    * BaseEntity is model to hold common properties between Customer and Proposal
    * Customer is model to hold customer details
    * [S4HanaAPIFacade](/event-consumer-app/src/main/java/com/example/s4hana/S4HanaApiFacade.java) is facade to read customer details from S4Hana system using OData API and destinations
* Package com.example.S4Hana.proposal
    * Proposal is model to hold proposal details
    * [Proposal API Facade](/event-consumer-app/src/main/java/com/example/s4hana/proposal/ProposalAPIFacade.java) is used to work with REST apis exposed by proposal extension to compare proposal and close duplicate proposal

## Security Settings

* Communication between APP and Destination Service
    * Events APP is binded to destination service via manifest.yml file
    * We can read details of connectivity details for destination service from environment variables of app
    * To work with destination URL , we need to first fetch JWT token using client crdential grant type
    * Using JWT token we can fetch destination service details
    
* Communication between APP and S4Hana
    * Enable inbound communication user in S4Hana
    * Create destination in CP , for S4Hana URL and communication user
    * Use destination details to send get call to S4Hana

* Communication between APP and Proposal Java APP
    * Give grant in xs-security.json of UAA for Java APP .More details on same [here](https://github.wdf.sap.corp/xs2-samples/security-feature-demo)
    * In xs-security.json of Events APP , accept granted authorities
    * Fetch JWT_Token using clientid , client secret of UAA for Java APP.
    * Use JWT_Token as Bearer token to call Java APP

# 5.Contributing
Find the contribution guide here: [Contribution Guidelines](docs/CONTRIBUTING.md)
