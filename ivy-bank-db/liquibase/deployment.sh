#!/bin/bash
set -e

# Execution of deployment
sh ./liquibase/src/liquibase --driver=org.postgresql.Driver --contexts="local" --classpath=${CLASSPATH} --changeLogFile="liquibase/changelog/changelog.xml" --url="jdbc:postgresql://localhost:6432/ivybankdb" --username="postgres" --password="postgres" --logLevel=INFO --defaultSchemaName=ivybank update