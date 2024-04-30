# Introduction

This is an implementation of ActiveMQ with Spring Boot using JmsListener. This application can be used to move messages from one queue to another, transform messages between queues, and even have concurrent listeners for specified queues. The best part about the application is that it is scalable and only requires changing the JSON configuration file to add queues or transformation functions.

## How to run the ActiveMQRelay

1. Install the ActiveMQ server on your local machine. You can download the ActiveMQ server from [here](https://activemq.apache.org/components/classic/download/).
2. Start the ActiveMQ server by running the command `./activemq.bat start` in the terminal.
3. Clone the repository.
4. Navigate to the root directory of the project and run the command `mvn clean install`.
5. Run the command `mvn spring-boot:run` to start the application.

## Configuring Queues

All configurations for the queues can be done in ```messageFlows.json``` and ```queueConcurrency.json``` which can be found in the ```src/main/resources``` folder. Transformation functions can be defined in ```src/main/java/com/activemqrelay/QueueRelay/TransformationFunctions.java``` 