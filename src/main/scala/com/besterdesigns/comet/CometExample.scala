package com.besterdesigns.comet

import net.liftweb.util.Helpers._
import net.liftweb.util.Schedule
import net.liftweb.common.Full
import net.liftweb.http.CometActor
import net.liftweb.json.{DefaultFormats, Formats}
import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JE

case class NewMessageEvent(message: String) extends JsCmd {
  override val toJsCmd = JE.JsRaw(""" $(document).trigger('new-message', %s)""".format( encJs( message ) ) ).toJsCmd
}

class CometExample extends CometActor {
  private var counter = 0;
  private var stopped = false;
  override def lifespan = Full(30.seconds)
  override def localShutdown() = {
    println("CometExample: local shutdown")
  }
  
  def render = NodeSeq.Empty
  
  override def localSetup() = {
    println("CometExample: local setup")
  } 
  
  override def lowPriority = {
    case ("test", msg:String) => {
      println("emit "+msg)
      counter += 1    
      partialUpdate(NewMessageEvent(msg))
      Schedule.schedule(this, ("test", s"abc-$counter"), 2.seconds)
    }
  }
  
  Schedule.schedule(this, ("test", "Startup"), 2.seconds)
}