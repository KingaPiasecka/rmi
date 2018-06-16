compile: Client/*.java Server/*.java
	javac Client/*.java
	javac Server/*.java

runserver:
	java Server.Server ${REGISTRY_IP} ${PORTS}
	
runclient:
	java Client.Client ${TESTCASE} ${REGISTRY_IP} ${PORTS}

clean:
	rm Client/*.class
	rm Server/*.class




