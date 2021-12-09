#!/bin/bash
cd /home/ubuntu/
wget https://s3.amazonaws.com/rds-downloads/rds-ca-2019-us-east-1.pem
cd /home/ubuntu/target
sudo nohup java -jar *.jar > /dev/null 2> /dev/null < /dev/null &
