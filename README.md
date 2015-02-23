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

Pre Requisites

One or more servers for running the load test clients, the servers may run either of the following OS's

  1. Ubuntu 14.04
  2. RedHat/Centos 6.5

These additonal steps must be run on Centos 6.5 to allow docker to run:
```
    $ sudo yum-config-manager --enable public_ol6_latest
    $ sudo yum install device-mapper-event-libs)
```

