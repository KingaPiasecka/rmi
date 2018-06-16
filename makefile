compile: Client/*.java Server/*.java
	javac Client/*.java
	javac Server/*.java

startServer:
	java Server.Server 127.0.0.1 9991 9992 9993
	
clientExampleSmall:
	java Client.Client exampleSmall REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993"

clientExampleBig:
	java Client.Client exampleBig REGISTRY_IP=127.0.0.1 PORTS="9991 9992 9993"

clean:
	rm Client/*.class
	rm Server/*.class




