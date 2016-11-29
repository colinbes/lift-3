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