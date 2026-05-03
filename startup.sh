#!/bin/bash
set -e

exec > /local/repository/startup-run.log 2>&1

LOGIN_NOTICE_FILE="/etc/profile.d/cloud468-status.sh"

sudo tee "$LOGIN_NOTICE_FILE" >/dev/null <<'EOF'
#!/bin/bash
HOST_FQDN=$(hostname -f 2>/dev/null || hostname)

if [ -f /local/repository/startup-run.log ]; then
  if grep -q "FINISHED: Software Deployment Registry is built and running" /local/repository/startup-run.log; then
    echo "Startup already finished. Latest log output:"
    tail -n 35 /local/repository/startup-run.log
  else
    echo "Startup is currently building. Live status:"
    while ! grep -q "FINISHED: Software Deployment Registry is built and running" /local/repository/startup-run.log; do
      clear
      echo "Project directory: /local/repository"
      echo "App URL: http://$HOST_FQDN:8080/  (or use SSH tunnel to localhost:8080)"
      echo "Status: BUILDING"
      echo
      tail -n 35 /local/repository/startup-run.log
      sleep 3
    done
    clear
    echo "Project directory: /local/repository"
    echo "App URL: http://$HOST_FQDN:8080/"
    echo "Status: DONE"
    echo
    tail -n 35 /local/repository/startup-run.log
  fi
else
  echo "Startup log not found yet: /local/repository/startup-run.log"
fi
EOF

sudo chmod +x "$LOGIN_NOTICE_FILE"

echo "=========================================="
echo "STARTUP: Cloud-468 automatic setup has begun"
echo "Docker and application build are starting"
echo "=========================================="
echo

export DEBIAN_FRONTEND=noninteractive

echo "[1/7] Updating package lists..."
sudo apt-get update
echo "[1/7] Done"
echo

echo "[2/7] Installing required system packages..."
sudo apt-get install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common tmux sudo apt gnupg2 pass
echo "[2/7] Done"
echo

if ! command -v docker >/dev/null 2>&1; then
  echo "[3/7] Installing Docker..."
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
  echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
  sudo apt-get update
  sudo apt-get install -y docker-ce docker-ce-cli containerd.io
  echo "[3/7] Docker install complete"
else
  echo "[3/7] Docker already installed"
fi
echo

echo "[4/7] Installing helper tools..."
sudo apt-get install -y httping jq
echo "[4/7] Done"
echo

if ! command -v docker-compose >/dev/null 2>&1; then
  echo "[5/7] Installing standalone docker-compose..."
  sudo curl -L https://github.com/docker/compose/releases/download/v2.32.4/docker-compose-linux-x86_64 -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  echo "[5/7] docker-compose install complete"
else
  echo "[5/7] docker-compose already installed"
fi
echo

echo "[6/7] Starting Docker service..."
sudo systemctl start docker
sudo systemctl enable docker
echo "[6/7] Docker service started"
echo

echo "[7/7] Building and launching application..."
cd /local/repository
sudo docker-compose down || true
sudo docker-compose up --build -d
echo "[7/7] Application build and launch complete"
echo

HOST_FQDN=$(hostname -f 2>/dev/null || hostname)

echo "=========================================="
echo "FINISHED: Software Deployment Registry is built and running"
echo "Project directory: /local/repository"
echo "App URL: http://$HOST_FQDN:8080/  (or use SSH tunnel to localhost:8080)"
echo "Useful commands:"
echo "  cd /local/repository"
echo "  sudo docker-compose ps"
echo "  tail -f /local/repository/startup-run.log"
echo "Website access with GUI:"
echo "  Direct URL: http://$HOST_FQDN:8080/"
echo "  If direct access does not work, use an SSH tunnel from your local machine:"
echo "    ssh -L 8080:localhost:8080 (YOUR CLOUD LOGIN)@$HOST_FQDN"
echo "    Example : ssh -L 8080:localhost:8080 agiacoio@clnode002.clemson.cloudlab.us
echo "  Then open: http://localhost:8080/"
echo "=========================================="
