# export MQ_USERNAME=drones
# export MQ_PASSWORD=123456

# Incluindo envs
export $(grep -v '^#' .env | xargs)

# docker compose up --build drone-norte drone-sul drone-leste drone-oeste drone-central drone-local gateway
docker compose up --build drone-norte