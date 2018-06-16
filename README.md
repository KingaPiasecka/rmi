# Java RMI Dijkstra's graph search algorithm

Yet another Dijkstra graph search algorithm implemented using Java RMI to distribute work that can be parallelized...

Written for parallel and distributed systems course on AGH University of Science and Technology.

**NOTE:** This is just an university project. Probably some of its parts could have been done better. Don't be scared of commits messages, I didn't do any git cleanup (e.g. `git rebase`) in here as this won't be maintained anymore.

In current state, the workload can't be distributed to different machines. It is easy to fix that - just change the `Client` and `DijkstraClient` classes to support it (btw. those classes could have had a better name).

# Running

To compile everything just fire `make`.

To run server fire:

```
make runserver REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993"
```

To run client fire:
```
make runclient REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993" TESTCASE=1
```

Of course you can set proper parameters (`REGISTY_IP`, `PORTS` and `TESTCASE`).


# Directories

* **Client** - contains client implementation

* **Server** - contains server implementation

* **Shared** - contains stuff that is shared in Client & Server

* **testcases** - contains map examples
