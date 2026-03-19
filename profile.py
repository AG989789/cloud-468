import geni.portal as portal
import geni.rspec.pg as pg
import geni.rspec.igext as IG

pc = portal.Context()
request = pc.makeRequestRSpec()

tourDescription = """
This profile provides a compute node with Docker installed on Ubuntu.
"""

tour = IG.Tour()
tour.Description(IG.Tour.TEXT, tourDescription)
request.addTour(tour)

node = request.XenVM("docker")
node.hardware_type = "pcvm"
node.routable_control_ip = "true"
node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:UBUNTU22-64-STD"

node.addService(pg.Execute(shell="sh", command="sudo bash /local/repository/install_docker.sh"))

pc.printRequestRSpec(request)
