package com.besterdesigns.snippet 

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import com.besterdesigns.lib.DateTimeUtils
import com.besterdesigns.lib.EmptyRoundTrip
import com.besterdesigns.lib.code.lib.DependencyFactory

import net.liftweb.common.Box
import net.liftweb.http.RoundTripHandlerFunc
import net.liftweb.http.RoundTripInfo
import net.liftweb.http.RoundTripInfo.handledBuilder
import net.liftweb.json.JString
import net.liftweb.json.JValue
import net.liftweb.json.parse
import net.liftweb.util.Helpers.StringToCssBindPromoter

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