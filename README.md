# pad-lab-1


The implementation of broker is presented here. Working language is **Java**. 
T
his project has 2 modules:
- client-module;
- message-module;

Message broker (`message-module/src/`);

Client (`client-module/src/`);

See [protocol-specs](https://github.com/jackcucu/pad-lab-1/blob/master/docs/protocol-specs.md) for more info about client-broker communication.

### Build

In order to build you should have installed maven.

**Client** (`client-module/`);

In terminal type `mvn clean package` that will produce in `target/` folder a jar.

**Message broker** (`message-module/`);

In terminal type `mvn clean package` that will produce in `target/` folder a jar.

### Run

You can run the following jars:

**Message broker** (`message-module/`);

Verify if **BIND_PORT**(`see constants below`) is not already in use then
`cd` in `target` folder and type `java -jar message-broker.jar`

**Client** (`client-module/`);

Program is running if broker is running on (`localhost:BIND_PORT(see constants below)`), run jar with arguments:
arg0 : 
- s(for subscriber)
- p(for publisher)

`cd` in `target` folder and type `java -jar client-jar-with-dependecies.jar [p|s]`.

**Constants**

These constants are used in project:
- BIND_PORT = 7979 *(Number of tcp port)*
- HOST = "127.0.0.1" *(host ip for clients)*