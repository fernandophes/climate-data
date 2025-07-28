# export MQ_USERNAME=drones
# export MQ_PASSWORD=123456

# Incluindo envs
export $(grep -v '^#' .env | xargs)

mvn clean install spring-boot:run