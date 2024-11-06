#!/bin/bash

# Start MySQL service
service mysql start

# Wait for MySQL to fully start up
sleep 10

# Create the database if it doesn't exist
mysql -u root -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE IF NOT EXISTS $MYSQL_DATABASE;"

# Start the Spring app
java -jar /app/syncit.jar
