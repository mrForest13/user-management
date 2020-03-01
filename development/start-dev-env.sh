#!/usr/bin/env bash

argument=$1

if [[ -n "$argument" ]]; then
  if [[ "$argument" == "--withApp" ]]; then
    echo "Starting dev environment with application"
    (
      cd ..
      sbt docker:publishLocal
    )
    docker-compose -f dev-all.yml up
  else
    echo "$argument: usage: --withApp"
  fi
else
  echo "Starting dev environment"
  docker-compose -f dev-environment.yml up
fi
