compile: Client/*.java Server/*.java
	javac Client/*.java
	javac Server/*.java

startServer:
	java Server.Server REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993"
	
clientExampleSmall:
	java Client.Client case=1 REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993"

clientExampleBig:
	java Client.Client case=2 REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993"

clean:
	rm Client/*.class
	rm Server/*.class




