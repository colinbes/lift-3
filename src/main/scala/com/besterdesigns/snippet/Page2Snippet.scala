package com.besterdesigns.snippet 

import scala.xml.NodeSeq

import com.besterdesigns.lib.EmptyRoundTrip

import net.liftweb.http.RoundTripHandlerFunc
import net.liftweb.http.RoundTripInfo
import net.liftweb.http.RoundTripInfo.handledBuilder
import net.liftweb.json.JString
import net.liftweb.json.JValue
import net.liftweb.json.parse

//Showing using two roundtrip traits.
trait Page2RT1 extends EmptyRoundTrip {
  
  protected def doRT1(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    func.send(JString("Page 2 Rountrip 1"))
  }

  private val roundtrips:List[RoundTripInfo] = List("page2RT1" -> doRT1 _)
  override def getRoundTrips = super.getRoundTrips ++ roundtrips    
}

trait Page2RT2 extends EmptyRoundTrip {
  protected def doRT2(value :JValue, func :RoundTripHandlerFunc) :Unit = {
    val response = """{"name":"Page2"}"""
    val json = parse(response);
    func.send(json)
  }   
  
  private val roundtrips:List[RoundTripInfo] = List("page2RT2"-> doRT2 _)
  override def getRoundTrips = super.getRoundTrips ++ roundtrips
}

class Page2Snippet extends Page2RT1 with Page2RT2 { 
  def render(in:NodeSeq):NodeSeq = {
    addServices("modelRTFunctions")
    in //no processing so return untouched.
  } 
}