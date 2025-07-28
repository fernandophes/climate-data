# Incluindo envs
export $(grep -v '^#' .env | xargs)

# mvn clean install spring-boot:run
mvn clean package
java -jar target/RabbitMQLink-1.0-SNAPSHOT-jar-with-dependencies.jar