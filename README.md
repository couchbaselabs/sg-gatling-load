# sg-gatling-load
Sync Gateway workload generator using gatling.io

The solution uses [ansible](www.ansible.com) to orchestrate the installation, configuration and running of [gatling](gatling.io) onto a group of servers (or VM's).

If you are using the [perfcluster-aws](https://github.com/couchbaselabs/perfcluster-aws.git) project to deploy your test clusters, use the ansible scripts provided in that repo.

This repo includes two ansible scripts that can be used to orchestrate the load test against an arbitrary set of hosts.

## Pre Requisites

  1. A desktop/server instance that will be used to run the ansible orchestration scripts
  2. A single server instance that will act as the gatling.io test client
  2. One or more server instances with Sync Gateway deployed
  3. A named Sync Gateway DB with a sync function that matches the scenario that is being load tested.

## Setup

  Login to the ansible orchestration host

  This host must have [ansible](http://www.ansible.com/home) installed
  
  Follow the onsite instructions for your hosts OS
  
  Install a git client on the this host
  
  Clone this git repo onto this host using the collowing command:
  
  ```
  git clone https://github.com/couchbaselabs/sg-gatling-load.git
  ```
  
  Setup the ansible hosts file, that will be used to connect 
  
  The path to the hosts file is:
  
  ```
  /etc/ansible/hosts
  ```
  
  Add the following sections, in each section add the server names or IP addresses:
  
  ```
  [gatling-clients]
  gatling-client.example.com

  [gatling-sync-gateway-servers]
  sg-server1.example.com
  sg-server2.example.com
  ```
  Change directory to ./ansible/playbooks in the cloned repo

  ```
  $ cd sg-gatling-load/ansible/playbooks
  ```
  
  Run the ansible playbook to install and configure gatling.io
  
  ```
  $ ansible-playbook configure-gatling.yml
  ```
  
  ## Run performance tests:
  
  Edit run-gatling-theme.yml and modify the 'remote_user' property to the user account to login to the remote servers as:
  
  If you are using credentials to login add the --ask-pass and --ask-sudo-pass parameter and provide the passwords when requested.
  
  ```
  $ ansible-playbook --ask-pass --ask-sudo-pass run-gatling-theme.yml
  ```
  
  If you have setup a private key for access then run the playbook using:
 ```
  $ ansible-playbook run-gatling-theme.yml
  ```
  To run a different theme, copy the playbook to a new name and edit the file:
  
  Change the line beginning with:
  ```
  shell: mvn test -e -P ..... > somelog.txt
  ```
  Set the name of the theme you want to run and add override any default parameters you want to change e.g.
  ```
  shell: mvn test -e -P todolite-sim -DhttpParamUserRestApiPort=80 > somelog.txt
  ```
  
## Adding a new theme

Change directory to

```
./themes
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



