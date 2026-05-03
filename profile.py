import geni.portal as portal
import geni.rspec.pg as rspec

request = portal.context.makeRequestRSpec()

node = request.RawPC("docker-node")
node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:UBUNTU22-64-STD"

node.addService(rspec.Execute(
    shell="/bin/bash",
    command="cd /local/repository && sed -i 's/\r$//' startup.sh && chmod +x startup.sh && /bin/bash /local/repository/startup.sh"
))

portal.context.printRequestRSpec()
