compile: Client/*.java Server/*.java
	javac Client/*.java
	javac Server/*.java

startServer:
	java Server.Server 127.0.0.1 9911 9912 9913
	
clientExampleSmall:
	java Client.Main exampleSmall 127.0.0.1 9911 9912 9913

clientExampleBig:
	java Client.Main exampleBig 127.0.0.1 9911 9912 9913

clean:
	rm Client/*.class
	rm Server/*.class
