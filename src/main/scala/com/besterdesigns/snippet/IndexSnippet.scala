package com.besterdesigns.snippet 

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import com.besterdesigns.lib.DateTimeUtils
import com.besterdesigns.lib.EmptyRoundTrip
import net.liftweb.common.Box
import net.liftweb.http.RoundTripHandlerFunc
import net.liftweb.http.RoundTripInfo
import net.liftweb.http.RoundTripInfo.handledBuilder
import net.liftweb.json.JString
import net.liftweb.json.JValue
import net.liftweb.json.parse
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.actor.LiftActor
import net.liftweb.http.S
import net.liftmodules.ng.AngularActor
import net.liftweb.common.Full
import net.liftweb.http.LiftSession
import net.liftweb.http.SessionVar
import net.liftweb.common.Empty

object SessionHeatbeat extends SessionVar[Box[Long]](Empty)

trait MyRT extends EmptyRoundTrip {
  
  private var heartbeat: Box[Long] = Full(System.currentTimeMillis)
  
  protected def heartBeat(value :JValue, func :RoundTripHandlerFunc): Unit = {
    heartbeat = Full(System.currentTimeMillis) 
    println("heartbeat "+heartbeat)
    func.send(JString("OK"))
  }
  
  protected def doSimpleRT(value :JValue, func :RoundTripHandlerFunc): Unit = {
    func.send(JString("There and back again!!"))
  }

  protected def doSomething(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    val response = """{"name":"Index page"}"""
    val json = parse(response);
    func.send(json)
  }
  
  protected def doClock(value: JValue, onChange: RoundTripHandlerFunc):Unit = {
    var count = 0
    def doIt() {
      
      val tick = System.currentTimeMillis()
      
      count += 1 
      heartbeat match {
        case Full(ts) if (tick < ts+5000) => {
          onChange.send(JString(""+count))
          println("doIt clock "+count)
          Schedule.schedule(doIt, 1.second)          
        }
        case _ => {
          onChange.send(JString("failed "+count))          
          println("failed doIt clock "+count)
        }
      }
    }    
    doIt()
  }
  
  private val roundtrips:List[RoundTripInfo] = List("heartbeat" -> heartBeat _, "doClock" -> doClock _, "doSimpleRT" -> doSimpleRT _, "doSomething" -> doSomething _)
  override def getRoundTrips: List[RoundTripInfo] = super.getRoundTrips ++ roundtrips  
}

class IndexSnippet extends MyRT {
//  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date
  println("instantiate IndexSnippet")  
  
  def render = {
    addServices("myRTFunctions");
//    val mk :DateTimeConverter = LiftRules.dateTimeConverter.vend
//    val dt = mk.formatDateTime(new Date())
    val now = DateTime.now()
    val Austin = DateTimeZone.forID("America/Chicago")
    val dt1 = DateTimeUtils.print(now)
    val dt2 = DateTimeUtils.print(now, Austin)
    val dt = s"$dt1 $dt2"
    "#time *" #> dt
  } 
}