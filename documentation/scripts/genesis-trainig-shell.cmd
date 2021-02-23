echo ! With Docker for Windows you only need to run docker-compose build on local docker without genesis-trainig-shell.cmd

SET DOCKER_TLS_VERIFY=1
SET DOCKER_HOST=tcp://192.168.99.101:2376
SET DOCKER_CERT_PATH=C:\Users\aspichakou\.docker\machine\machines\genesis-training
SET DOCKER_MACHINE_NAME=genesis-training
SET COMPOSE_CONVERT_WINDOWS_PATHS=true

"C:\Program Files\Git\bin\bash.exe" --login -i "C:\Program Files\Docker Toolbox\start.sh"
