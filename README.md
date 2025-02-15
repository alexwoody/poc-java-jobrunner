
# Jobrunner Aggregation POC

## Assumptions of what is included in the POC

* POC to cover both a Server and Serverless deployment environment
* Need to be able to support multiple instances.
* A degree of shared state is required for monitoring and failover
* Order of items processed not guaranteed, but aggrgation will ensure result has maintained order in output.
* Irresecptive of Error result will be returned for each line item processed
* An Event Look in the Aggregating process will be used rather than event arrival, as it cannot be guaranteed that all events will complete.
* Time depending external aspects of the application will be mocked, and potentially implemented based upon time.
* For speed will use Java with as few dependencies as possible.
* Will not cover validating data types using message schema.
# poc-java-jobrunner
