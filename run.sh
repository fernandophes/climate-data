# Incluindo envs
export $(grep -v '^#' .env | xargs)


# docker compose up --build drone-norte drone-sul drone-leste drone-oeste drone-central drone-local gateway
# docker compose up --build rabbitmq
docker compose up --build drone-sul