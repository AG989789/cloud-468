import geni.portal as portal
import geni.rspec.pg as rspec

request = portal.context.makeRequestRSpec()

node = request.RawPC("docker-node")
node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:UBUNTU22-64-STD"

node.addService(rspec.Execute(
    shell="sh",
    command="cd /local/repository && echo profile-started > profile-ran-2.txt && chmod +x startup.sh && sh startup.sh > startup-run.log 2>&1"
))

portal.context.printRequestRSpec()
