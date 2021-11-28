This is the implementation for NAT traversal of UDP based traffic for P2P communication without using any dedicated server.
Please refer to [this](https://en.wikipedia.org/wiki/UDP_hole_punching) for the understanding of UDP hole punching works.
Assumptions for this piece of code to work-
- Both client A and client B know the public IP address of server S
- Port randomizatoin does not occur at NAT
- NAT firewalls at client A and B allow incoming UDP based traffic from outside the private network

Please run Server.java on a separate machine. Change the IP address of this rendezvous server (RENDEZVOUS_SERVER_IP) in Constants.java.
Please run Client.java on 2 separate clients which are aiming to establish a connection.
You can disconnect the server once the initial channel is established.
If messages are not sent for over a minute (approx.), then this UDP hole in NAT gets filled up automatically due to lack of use. 
