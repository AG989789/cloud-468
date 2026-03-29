import geni.portal as portal
import geni.rspec.pg as rspec

request = portal.context.makeRequestRSpec()

node = request.RawPC("docker-node")

node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:UBUNTU22-64-STD"

node.addService(rspec.Execute(shell="/bin/sh", command="sudo apt update && sudo apt install -y docker.io"))
node.addService(rspec.Execute(shell="/bin/sh", command="sudo systemctl start docker"))
node.addService(rspec.Execute(shell="/bin/sh", command="sudo systemctl enable docker"))
node.addService(rspec.Execute(shell="/bin/sh", command="sudo usermod -aG docker ubuntu"))

portal.context.printRequestRSpec()
