#!/bin/bash
sudo systemctl start amazon-cloudwatch-agent.service
cd /home/ubuntu/target
sudo nohup java -jar *.jar > /dev/null 2> /dev/null < /dev/null &
