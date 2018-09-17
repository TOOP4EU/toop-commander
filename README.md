# toop-commander

A simple java command line app (with auto complete) that creates a dp and dc endpoint for receiving messages from the connector. It also provides means for sending toop requests from command line. 

## Workflow

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
Commands:
  help                         print help message

  cat file1 file2 file3 ...    print contents of the provided files

  send-dc-request   -f <request file>
                               Send the request  file (asic or xml) to the configured connectors /from-dc endpoint

  send-dp-response  -f <response file>
                               Send the response file (asic or xml) to the configured connectors /from-dp endpoint

  send-dc-request -new -i <Data Subject Identifier> -c <Data Subject Country> -m <metadata file>
                               Create a new toop request message using the provided data and send it to the /from-dc endpoint
                               if the options -i and -c are provided, then they override the DataSubjectIdentifier
                               and DataSubjectCountry values in the metadata file.
                               The option '-m' is optional and if it is absent, the default metadata.conf file
                               is used.
                               For the details of the metadata file, please read the README.

  send-dp-response -new -i <Data Subject Identifier> -c <Data Subject Country> -m <metadata file>
                               Create a new toop response message using the provided data and send it to the /from-dp endpoint
                               if the options -i and -c are provided, then they override the DataSubjectIdentifier
                               and DataSubjectCountry values in the metadata file.
                               The option '-m' is optional and if it is absent, the default metadata.conf file
                               is used.
                               For the details of the metadata file, please read the README.


  quit                         exit toop-commander

```

All the commands can be auto completed with `tab`. Also the commands `cat`, `send-d..` support file name completion with `tab`. Presing `tab` multiple times will allow traversal between options.

## Sending an existing dc request to the toop-connector

In the command line interface type `send-dc-request FILE_NAME` , where `FILE_NAME` points to an absolute or relative roop request xml or asic file.

example:
```
toop-commander> send-dc-request -f request/TOOPRequest
toop-commander> send-dc-request -f request/request_last.asic
```

if the file is an asic, toop-commander will send it directly. For an xml file, it will try to unmarshall the xml to a `eu.toop.commons.dataexchange.TDETOOPRequestType` and create an asic with the provided keys and then send the asic to the connector. 

## Sending an existing dp response to the toop-connector

In the command line interface type `send-dp-response FILE_NAME` , where `FILE_NAME` points to an absolute or relative roop request xml or asic file.

example:
```
toop-commander> send-dp-response -f response/TOOPResponse
toop-commander> send-dp-response -f response/response_last.asic
```


## Sending a new dc request / dp response to the toop-connector
You can use the command `send-dc-request` or `send-dp-response` with the argument `-new` to allow creation of a new TOOP request and send it the the connector.
 
Additional arguments for `send-dc-request -new` and `send-dp-response -new` are 
* `-i <Document Subject Identifier>`, 
* `-c <Document Subject Country>`, 
* `-m metadata.conf`. 

These arguments are all optional. The default values are in the default metadata file `metadata.conf` (formatted as `HOCON`). 
If you provide arguments `-i` and `-c`, they will override the keys `ToopMessage.NaturalPerson.identifier` and `ToopMessage.DestinationCountryCode` that are defined in the 
metadata file (either the default `metadata.conf` or the one that you optionaly provide with `-m` argument)

You can create multiple metadata files for different test cases so that you can obtain variance between the data that is created during the request/response creation

Please inspect the file `metadata.conf` for the default values.

#Configuration
When you run toop-commander, you need to provide a key store and `toop-commander.conf` file. These files are already in the repository. You can use them as an example and then modify them.

`toop-commander.conf` is a `HOCON` configuration file. (See https://github.com/lightbend/config/blob/master/HOCON.md) 

The configuration keys in the `toop-commander.conf` are all commented and you shouldn't have a problem understaing it.


