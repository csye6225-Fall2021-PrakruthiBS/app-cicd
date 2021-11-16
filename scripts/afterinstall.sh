#!/bin/bash
cd /home/ubuntu/
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/CloudWatchAgentConfig.json -s
