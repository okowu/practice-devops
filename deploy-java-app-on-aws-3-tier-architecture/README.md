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

Connect to the EC2 instance (SSH) and install nginx

1. Install Nginx 
      1. sudo yum update
      2. sudo yum install nginx
   1. Create a cloudwatch metric to monitor memory usage
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
2. Install Apache Tomcat
   1. Install JDK
      1. `yum search corretto`
      2. `sudo yum install java-22-amazon-corretto`
      3. Download Apache Tomcat [TAR](https://tomcat.apache.org/download-11.cgi)
      4. Unzip the tar into /opt/ folder `sudo tar -xvzf apache-tomcat-<version>.tar.gz -C /opt/`
      5. Create a symlink to /opt/tomcat `sudo ln -s /opt/apache-tomcat-<version> /opt/tomcat`
      6. Configure tomcat as a systemd service `sudo nano /etc/systemd/system/tomcat.service`
         ```
            [Unit]
            Description=Apache Tomcat Web Application Container
            After=network.target

            [Service]
            Type=forking

            Environment=JAVA_HOME=/usr/lib/jvm/jre
            Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
            Environment=CATALINA_HOME=/opt/tomcat
            Environment=CATALINA_BASE=/opt/tomcat
            Environment='CATALINA_OPTS=-Xms512M -Xmx1024M -server -XX:+UseParallelGC'
            Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'

            ExecStart=/opt/tomcat/bin/startup.sh
            ExecStop=/opt/tomcat/bin/shutdown.sh

            User=tomcat
            Group=tomcat
            UMask=0007
            RestartSec=10
            Restart=always

            [Install]
            WantedBy=multi-user.target
         ``` 
      7. Ensure tomcat user is created `id tomcat`
         1. If not, create tomcat user `sudo useradd -r -m -U -d /opt/tomcat -s /bin/false tomcat`
         2. Give ownership of the /opt/tomcat/ folder to tomcat user `sudo chown -R tomcat:tomcat /opt/tomcat/`
      8. Enable and start the tomcat service
         1. `sudo systemctl daemon-reload`
         2. `sudo systemctl start tomcat`
         3. `sudo systemctl enable tomcat`

Create another EC2 instance
1. Install JDK `sudo yum install java-22-amazon-corretto`
2. Install Git `sudo yum install git`
3. Download Maven [TAR](https://maven.apache.org/download.cgi)
   1. Unzip the tar into /opt/ folder `sudo tar -xvzf apache-maven-<version>.tar.gz -C /opt/`
   2. Create a symlink to /opt/maven `sudo ln -s /opt/apache-maven-<version> /opt/maven`
   3. Update system PATH
      ```sh
         echo "export M2_HOME=/opt/maven" | sudo tee -a /etc/profile.d/maven.sh
         echo "export PATH=\$M2_HOME/bin:\$PATH" | sudo tee -a /etc/profile.d/maven.sh
         source /etc/profile.d/maven.sh
      ``` 
