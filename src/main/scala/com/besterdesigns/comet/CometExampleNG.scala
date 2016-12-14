package com.besterdesigns.comet

import net.liftmodules.ng.AngularActor
import net.liftweb.util.Helpers._
import net.liftweb.util.Schedule
import net.liftweb.common.Full
import net.liftweb.http.CometActor
import net.liftweb.json.{DefaultFormats, Formats}
import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.JsRaw

case class Test(name: String)

class CometExampleNG extends AngularActor {
  private var counter = 0;
  private var stopped = false;
  override def lifespan = Full(30.seconds)

  override def localShutdown() = {
    println("CometExampleNG: local shutdown")
    //stopped = true
  }
  
  override def localSetup() = {
    println("CometExampleNG: local setup")
    //stopped = false
  }
  
  override def lowPriority = {
    case ("emit", msg:String) => rootScope.emit("emit-message", msg)

    case ("broadcast", msg:String) => scope.broadcast("emit-message", msg)
    case ("broadcast", obj:AnyRef) => scope.broadcast("emit-object",  obj)
    case ("emit", obj:AnyRef) => {
      scope.emit("emit-object",  obj)
      println("emit "+obj)
      counter += 1    
//      if (!stopped) {
        val data = Test(s"abc-$counter")
        Schedule.schedule(this, ("emit", data), 2.seconds)
//      } else {
//        println("shutdown")
//      }
    }

    case ("assign", msg:String) => rootScope.assign("my.str.field", msg)
    case ("assign", obj:AnyRef) => scope.assign("myObj", obj) 
    case s => println("CometActor " + this + " got unexpected message " + s)
  }
  
  Schedule.schedule(this, ("emit", Test("Emit")), 5.seconds)
}