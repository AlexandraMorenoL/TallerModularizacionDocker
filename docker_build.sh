#!/usr/bin/env bash
set -e
mvn -q -DskipTests package
docker build -t microframework-eci:latest .
