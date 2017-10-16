# Liftweb 3 ng-switch csssel binding issue demonstration
Added page 'switch' to demonstrate issue with csssel binding to button when button is within ng-switch statement.

On startup, point browser to http://localhost:8082 and click on 'switch' in navigation bar.

Page will show radio buttons to toggle ng-switch to display categories and rename tags. To right of buttons there is 'Save' button which is bound via csssel.

Right below the radio buttons is a 'Save' button that is only there to demonstrate that binding via csssel to button outside of ng-switch does work.

Note that csssel binding in lift 2.6 does work and is being used in production project.

# Liftweb 3.0, Angular and Bootstrap Starter Template

- build system: MVN
- IDE: Eclipse (Scala IDE) 
- less file *mystyles.less* is used to manage and build the mystyles.css file 
  ```
  cd <project>/src/main/webapp/assets/css
  lessc ../less/mystyles.less mystyles.css
  ```
Stylesheet could be automated with MVN if required.

#Internationalisation

Shows use of using resource **MessageResource.properties** for global properties as well as **_resources.html**

## Utilises Liftweb's roundtrip for angular/server communication.

In order to add a roundtrip function in your snippet, create a trait extending EmptyRoundTrip and then in turn have your snippet extend this trait.

In your snippet extending trait EmptyRoundTrip call `addServices("myRTFunctions");` where **myRTFunctions** is the name of RoundTrip functions for access in client side controller/service.

Example trait for RoundTrip providing server calls **doSimpleRT** and **doSomething**

```
trait MyRT extends EmptyRoundTrip {
  
  protected def doSimpleRT(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    func.send(JString("There and back again!!"))
  }

  protected def doSomething(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    val response = """{"name":"Index page"}"""
    val json = parse(response);
    func.send(json)
  }   

  private val roundtrips:List[RoundTripInfo] = {
    List("doSimpleRT" -> doSimpleRT _, "doSomething" -> doSomething _)
  }

  override def getRoundTrips = super.getRoundTrips ++ roundtrips    
}

class IndexSnippet extends MyRT {
  ...
}
```

Note, this template is mix of various different examples found on the web with my own edits and spin on a Liftweb template that I typically use.

To login, use a valid email address and password of *password*, **Note** email is not used anywhere, it's simply a placeholder.