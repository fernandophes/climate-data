# export MQ_USERNAME=drones
# export MQ_PASSWORD=123456

# Incluindo envs
export $(grep -v '^#' .env | xargs)

mvn clean install exec:java
# java -jar target/gateway-1.0-SNAPSHOT-jar-with-dependencies.jar