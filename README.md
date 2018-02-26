# Liftweb 3 defParamToFutureAny failure demo

Note, this template is mix of various different examples found on the web with my own edits and spin on a Liftweb template that I typically use.

To run use `mvn jetty:run`

- To call function using defParamToFutureAny click `Get Data Future` button
- To call function using defParamToAny clock `Get Data` button
- To call delayed sync call set timeout (less than 10 seconds) and click `Get Data Delayed` button

Testing done on Chrome browser and both functions work with network performance set to **online**.

To demonstrate failure set network performance to Fast 3G or Slow 3G
