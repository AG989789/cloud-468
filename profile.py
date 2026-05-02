import geni.portal as portal
import geni.rspec.pg as rspec

request = portal.context.makeRequestRSpec()

node = request.RawPC("docker-node")
node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:UBUNTU22-64-STD"

node.addService(rspec.Execute(
    shell="/bin/bash",
    command="""sudo apt update &&
sudo apt install -y git &&
cd /users/agiacoio &&
if [ ! -d cloud-468 ]; then
  git clone https://github.com/AG989789/cloud-468.git;
else
  cd cloud-468 && git pull;
fi &&
cd /users/agiacoio/cloud-468 &&
chmod +x startup.sh &&
bash startup.sh"""
))

portal.context.printRequestRSpec()
