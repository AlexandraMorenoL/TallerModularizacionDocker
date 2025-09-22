#!/usr/bin/env bash
set -e
docker run -d -p 34001:6000 --name microeci microframework-eci:latest
