# sg-gatling-load
Sync Gateway workload generator using gatling.io

The solution uses [ansible](www.ansible.com) to orchestrate the installation and configuration of docker and [gatling](gatling.io) onto a group of Ubuntu and Centos servers (or VM's).

The ansible playbook steps are as follows:

1) Install docker onto each server
2) Call docker to install a gatling container
3) Start the container
4) Configure gatling with the load test scenario
5) Run the load test
6) Retrieve the run logs
7) Merge the logs and generate the gatling reports
