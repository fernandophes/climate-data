# Incluindo envs
export $(grep -v '^#' .env | xargs)

mvn clean package
java -jar target/publisher-1.0-SNAPSHOT-jar-with-dependencies.jar

# mvn clean install exec:java
