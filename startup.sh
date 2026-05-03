#!/bin/bash
set -e

exec > /local/repository/startup-run.log 2>&1

STATUS_FILE="/local/repository/startup-status.txt"
BASHRC_BLOCK_START="# CLOUD468_STATUS_START"
BASHRC_BLOCK_END="# CLOUD468_STATUS_END"

cat > /tmp/cloud468_bashrc_block <<'EOF'
# CLOUD468_STATUS_START
if [ -f /local/repository/startup-status.txt ]; then
  cat /local/repository/startup-status.txt
fi
HOST_FQDN=$(hostname -f 2>/dev/null || hostname)
echo "Project directory: /local/repository"
echo "App URL: http://$HOST_FQDN:8080/  (or use SSH tunnel to localhost:8080)"
echo "Useful commands:"
echo "  cd /local/repository"
echo "  sudo docker-compose ps"
echo "  tail -f /local/repository/startup-run.log"
# CLOUD468_STATUS_END
EOF

if [ -f /users/agiacoio/.bashrc ]; then
  sed -i "/$BASHRC_BLOCK_START/,/$BASHRC_BLOCK_END/d" /users/agiacoio/.bashrc
fi
cat /tmp/cloud468_bashrc_block >> /users/agiacoio/.bashrc

cat <<'EOF' | sudo tee "$STATUS_FILE" >/dev/null
==========================================
Cloud-468 startup script has begun
Downloads and setup are now in progress
This may take several minutes
==========================================
EOF

echo "=========================================="
echo "Cloud-468 startup script has begun"
echo "Downloads and setup are now in progress"
echo "This may take several minutes"
echo "=========================================="
echo

date
echo "User: $(whoami)"
echo "Working directory: $(pwd)"
echo

export DEBIAN_FRONTEND=noninteractive

echo "[1/7] Updating package lists..."
sudo apt-get update
echo "[1/7] Package list update complete"
echo

echo "[2/7] Installing required system packages..."
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common tmux sudo apt gnupg2 pass
echo "[2/7] Required system packages installed"
echo

if ! command -v docker >/dev/null 2>&1; then
  echo "[3/7] Docker not found"
  echo "[3/7] Adding Docker repository and installing Docker..."
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
  sudo apt-get update
  sudo apt-get install -y docker-ce docker-ce-cli containerd.io
  echo "[3/7] Docker installation complete"
else
  echo "[3/7] Docker is already installed"
fi
echo

echo "[4/7] Installing helper tools..."
sudo apt-get install -y httping jq
echo "[4/7] Helper tools installed"
echo

if ! command -v docker-compose >/dev/null 2>&1; then
  echo "[5/7] docker-compose not found"
  echo "[5/7] Installing standalone docker-compose..."
  sudo curl -L https://github.com/docker/compose/releases/download/v2.32.4/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  echo "[5/7] docker-compose installation complete"
else
  echo "[5/7] docker-compose is already installed"
fi
echo

echo "[6/7] Starting Docker service..."
sudo systemctl start docker
sudo systemctl enable docker
echo "[6/7] Docker service is running"
echo

echo "[7/7] Building and launching application containers..."
cd /local/repository
sudo docker-compose down || true
sudo docker-compose up --build -d
echo "[7/7] Docker containers launched"
echo

echo "=========================================="
echo "Software Deployment Registry setup is complete"
echo "The application should now be available"
echo "Check container status with: sudo docker-compose ps"
echo "=========================================="

cat <<'EOF' | sudo tee "$STATUS_FILE" >/dev/null
==========================================
Software Deployment Registry setup is complete
The application should now be available
Check container status with: sudo docker-compose ps
==========================================
EOF
