# Incluindo envs
export $(grep -v '^#' .env | xargs)

mvn clean install exec:java