#!/bin/bash

sudo killall java
sudo systemctl stop amazon-cloudwatch-agent.service
exit 0