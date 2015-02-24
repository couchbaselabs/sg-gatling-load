# sg-gatling-load
Sync Gateway workload generator using gatling.io

The solution uses [ansible](www.ansible.com) to orchestrate the installation and configuration of docker and [gatling](gatling.io) onto a group of Ubuntu and Centos servers (or VM's).

The ansible playbook steps are as follows:
  1. Install docker onto each server
  2. Call docker to install a gatling container
  3. Start the container
  4. Configure gatling with the load test scenario
  5. Run the load test
  6. Retrieve the run logs
  7. Merge the logs and generate the gatling reports

# Running a load test

## Setup

  1. A host that will manage the gatling test clients

  This host must have [ansible](http://www.ansible.com/home) installed
  
  Clone this repo onto the management host using the collowing command:
  
  ```
  git clone https://github.com/couchbaselabs/sg-gatling-load.git
  ```

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
