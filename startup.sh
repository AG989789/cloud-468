#!/bin/bash
set -e

exec > /tmp/cloud-468-startup.log 2>&1

echo "startup script began"
date
whoami
pwd
echo "HOME is $HOME"

sudo apt-get update
sudo apt-get install -y git

cd "$HOME"

echo "current directory after cd:"
pwd
ls || true

if [ ! -d "cloud-468" ]; then
  echo "cloning repo"
  git clone https://github.com/AG989789/cloud-468.git
else
  echo "repo already exists, pulling latest"
  cd cloud-468
  git pull
  cd ..
fi

echo "final directory contents:"
ls
echo "startup script finished"
