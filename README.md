# sg-gatling-load
Sync Gateway workload generator using gatling.io

The solution uses [ansible](www.ansible.com) to orchestrate the installation, configuration and running of [gatling](gatling.io) onto a group of servers (or VM's).

If you are using the [perfcluster-aws](https://github.com/couchbaselabs/perfcluster-aws.git) project to deploy your test clusters, use the ansible scripts provided in that repo.

This repo includes two ansible scripts that can be used to orchestrate the load test against an arbitrary set of hosts.

## Pre Requisites

  1. A single server instance that will act as the gatling.io test client
  2. One or more server instances with Sync Gateway deployed
  3. A named Sync Gateway DB with a sync function that matches the scenario that is being load tested.

## Setup

  1. gatling.io test client

  This host must have [ansible](http://www.ansible.com/home) installed
  
  Clone this repo onto the management host using the collowing command:
  
  ```
  git clone https://github.com/couchbaselabs/sg-gatling-load.git
  ```
  
  Install JDK and Maven roles
```
  ansible-galaxy install geerlingguy.java
  ansible-galaxy install https://github.com/silpion/ansible-maven.git
```

  
The ansible playbook steps are as follows:
  1. Install docker onto each server
  2. Call docker to install a gatling container
  3. Start the container
  4. Configure gatling with the load test scenario
  5. Run the load test
  6. Retrieve the run logs
  7. Merge the logs and generate the gatling reports

# Running a load test





  2. One or more servers for running the load test clients, the servers may run either of the following OS's

    1. Ubuntu 14.04
    2. RedHat/Centos 6.5

    These additonal steps must be run on Centos 6.5 to allow docker to run:
```
    $ sudo yum-config-manager --enable public_ol6_latest
    $ sudo yum install device-mapper-event-libs)
```
  Create a common user account and password on each server, ensure that the user is a member of the "sudo" group:
  
  ```
  $ sudo useradd -m <username>
  $ sudo passwd <username>
  $ sudo sudo adduser <username> sudo
  ```
  Add each hosts IP address to the [sg-gatling-load] section of the /etc/ansible/hosts file on the management host.
  
On the management host change directory to where sg-gatling-load was cloned and run the following command

## Run Test

```
$ ansible-playbook --ask-pass --ask-sudo-pass gateloadclients.yml
```

## Generate Report

## Adding a new theme

Change directory to

```
themes
```
Create a new theme using maven architypes

```
mvn archetype:generate
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 582: gatling
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): : 1
Choose io.gatling.highcharts:gatling-highcharts-maven-archetype version: 10
Define value for property 'groupId': : com.couchbase.sg
Define value for property 'artifactId': : <THEME_NAME>
Define value for property 'version':  1.0-SNAPSHOT: : 1.0-SNAPSHOT
Define value for property 'package':  com.couchbase.sg: : com.couchbase.sg
Y: : y
```



