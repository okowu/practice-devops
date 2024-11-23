# Project Overview

## Goal

Install a Java application on AWS 3 Tier Architecture

## Pre-requisites

- [ ] AWS Free Tier Account
- [ ] GitHub Account
- [ ] SonarCloud
- [ ] JFrog

## Steps

### Pre-requisites

- Install AWS CLI

### Launch an EC2 instance on AWS

1. Install Nginx 
   1. Connect to the EC2 instance (SSH) and install nginx
      1. sudo yum update
      2. sudo yum install nginx
   2. Create a cloudwatch metric to monitor memory usage
      1. `free | grep Mem | awk '{print $3/$2 * 100.0}'`
         1. `free` displays information about memory usage
         2. `grep Mem` filters the output of free to only show the line that begin with Mem
         3. `awk` process the filtered line to calculate the percentage of used memory
      2. Create a script that push metric data to aws cloudwatch
         1. ([AWS Cloudwatch CLI reference](https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/publishingMetrics.html))
         ```bash
            #!/bin/bash
                while true; do
                memory_usage=$(free | grep Mem | awk '{print $3/$2 * 100.0}')
                aws cloudwatch put-metric-data --metric-name MemoryUsage --namespace Custom --value $memory_usage --dimensions InstanceId=$(curl http://169.254.169.254/latest/meta-data/instance-id)
                sleep 60
            done &
         ```

2. 