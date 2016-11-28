package com.besterdesigns.snippet 

import net.liftweb.http.RoundTripHandlerFunc
import net.liftweb.json._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.http.RoundTripInfo
import net.liftweb.http._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.RoundTripInfo.handledBuilder
import com.besterdesigns.lib._
import java.util.Date
import org.joda.time.DateTime
import com.besterdesigns.lib.code.lib.DependencyFactory

trait MyRT extends EmptyRoundTrip {
  
  protected def doSimpleRT(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    func.send(JString("There and back again!!"))
  }

  protected def doSomething(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    val response = """{"name":"Index page"}"""
    val json = parse(response);
    func.send(json)
  }   

  private val roundtrips:List[RoundTripInfo] = List("doSimpleRT" -> doSimpleRT _, "doSomething" -> doSomething _)
  override def getRoundTrips = super.getRoundTrips ++ roundtrips    
}

class IndexSnippet extends MyRT  {  
 
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date
  
  def render() = {
    addServices("myRTFunctions");
    
    val mk :DateTimeConverter = LiftRules.dateTimeConverter.vend
    val dt = mk.formatDateTime(new Date())
    
    "#time *" #> dt
  } 
}