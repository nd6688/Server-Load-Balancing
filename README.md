Server-Load-Balancing
=====================

Problem Definition: Load balancing across servers can be done, but without generally agreed upon
principles on how to best accomplish this, it would not be possible to achieve best results.
Different techniques are available which balance the load on servers based on the requirements of
the application. Different techniques such as Round Robin, Least Connection, Random, etc do not
give best results for application where heavy computation power is required.


Solution Design: Our solution is to balance the load on servers using the information about recent
CPU usage of all the available servers with the load balancer. Whenever a load balancer receives
request from a client, all the servers are queried for their current system load average for the
last minute. We select the server which is least loaded and forward the request to it. Server serves
the request and sends result to the client. If client's data on the server is manipulated or new
data is added, server acknowledges data replicator about the change. This change in data is notified
to all the servers by data replicator.


Implementation steps:
1) Compile all the java files
2) Run PrimaryLB on first server
3) Run SecondaryLB on second server
4) Run Replicator on third server
5) Run any number of Server file on any server except the above three
6) Run client on any server and test the code
