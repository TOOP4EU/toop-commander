# toop-commander

A simple java command line app (with auto complete) that creates a dp and dc endpoint for receiving messages from the connector. It also provides means for sending toop requests from command line. 

# Workflow

To compile the entire project, run "mvn verify".

To run the application, go to `./target` dir run `java -jar toop-commander-1.0.0.jar` and open `http://localhost:10101` to verify that it is working.

After deploying the `/to-dc` and `/to-dp` endpoints, it will enter command line mode for sending messages to the configured connector.

```
2018-09-12 16:11:32,181 INFO e.t.c.Main - main - Starting toop-commander on port 10101
2018-09-12 16:11:32,264 INFO e.t.c.Main - main - Registering root servlet
2018-09-12 16:11:32,268 INFO e.t.c.Main - main - Registering the to-dc endpoint on /to-dc
2018-09-12 16:11:32,268 INFO e.t.c.Main - main - Registering the to-dp endpoint on /to-dp
2018-09-12 16:11:32,271 INFO e.t.c.Main - main - Starting server

2018-09-12 16:11:32,349 INFO e.t.c.Main - main - Entering CLI mode
toop-commander>

```

type `help` for description of possible commands.

**Commands:**
```
1. help                     print help message
2. cat                      print contents of a file
3. send-dc-request   file   Send the request  file to the configured connectors /from-dc endpoint
4. send-dp-response  file   Send the response file to the configured connectors /from-dp endpoint
5. quit                     exit toop-commander
```

all the commands can be auto completed with `tab`. Also the commands `cat`, `send-d..` support file name completion with tab.

## Sending dc request to the toop-connector

In the command line interface type `send-dc-request FILE_NAME` , where `FILE_NAME` points to an absolute or relative roop request xml or asic file.

example:
```
toop-commander> send-dc-request request/TOOPRequest
toop-commander> send-dc-request request/request_last.asic
```

if the file is an asic, toop-commander will send it directly. For an xml file, it will try to unmarshall the xml to a `eu.toop.commons.dataexchange.TDETOOPRequestType` and create an asic with the provided keys and then send the asic to the connector. 

## Sending dp response to the toop-connector

In the command line interface type `send-dp-response FILE_NAME` , where `FILE_NAME` points to an absolute or relative roop request xml or asic file.

example:
```
toop-commander> send-dp-response response/TOOPResponse
toop-commander> send-dp-response response/response_last.asic
```

#Configuration
When you run toop-commander, you need to provide a key store and `toop-commander.conf` file. These files are already in the repository. You can use them as an example and then modify them.

`toop-commander.conf` is a `HOCON` configuration file. (See https://github.com/lightbend/config/blob/master/HOCON.md) 

The configuration keys in the `toop-commander.conf` are all commented and you shouldn't have a problem understaing it.





