# Incluindo envs
export $(grep -v '^#' .env | xargs)


# docker compose up --build drone-norte drone-sul drone-leste drone-oeste drone-central drone-local gateway
<<<<<<< HEAD
docker compose up --build
=======
# docker compose up --build rabbitmq
docker compose up --build drone-sul
>>>>>>> 17527ba0f11b43cb7f1c5b9bcd9e98c856226fe3
