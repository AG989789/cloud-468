#!/bin/bash
set -e

exec > /local/repository/startup-run.log 2>&1

echo "=========================================="
echo "Cloud-468 startup script has begun."
echo "This may take several minutes."
echo "Docker and application setup are loading..."
echo "=========================================="

date
whoami
pwd

echo "[1/6] Updating apt packages..."
sudo apt-get update

echo "[2/6] Installing required system packages..."
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common tmux sudo apt gnupg2 pass

if ! command -v docker >/dev/null 2>&1; then
  echo "[3/6] Docker not found. Installing Docker..."
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
  sudo apt-get update
  sudo apt-get install -y docker-ce docker-ce-cli containerd.io
else
  echo "[3/6] Docker is already installed."
fi

echo "[4/6] Installing helper tools..."
sudo apt-get install -y httping jq

if ! command -v docker-compose >/dev/null 2>&1; then
  echo "[5/6] docker-compose not found. Installing docker-compose..."
  sudo curl -L https://github.com/docker/compose/releases/download/v2.32.4/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
else
  echo "[5/6] docker-compose is already installed."
fi

echo "[6/6] Starting Docker service..."
sudo systemctl start docker
sudo systemctl enable docker

cd /local/repository

echo "=========================================="
echo "Docker setup is complete."
echo "Building and launching the application..."
echo "=========================================="

sudo docker-compose down || true
sudo docker-compose up --build -d

echo "=========================================="
echo "Software Deployment Registry is now running."
echo "Setup is complete."
echo "Use 'sudo docker-compose ps' to check containers."
echo "=========================================="
