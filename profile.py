import geni.portal as portal
import geni.rspec.pg as rspec

request = portal.context.makeRequestRSpec()

node = request.RawPC("docker-node")
node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:UBUNTU22-64-STD"

node.addService(rspec.Execute(
    shell="/bin/bash",
    command="echo profile-ran | sudo tee /tmp/profile-ran.txt"
))

portal.context.printRequestRSpec()
