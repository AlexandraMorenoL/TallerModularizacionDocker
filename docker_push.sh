#!/usr/bin/env bash
set -e
if [ -z "$1" ]; then echo "Uso: ./docker_push.sh <usuario_dockerhub>"; exit 1; fi
docker tag microframework-eci:latest $1/microframework-eci:latest
docker push $1/microframework-eci:latest
