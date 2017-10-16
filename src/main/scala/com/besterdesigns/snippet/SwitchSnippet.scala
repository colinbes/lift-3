package com.besterdesigns.snippet 

import scala.xml.NodeSeq

import com.besterdesigns.lib.EmptyRoundTrip

import net.liftweb.http.RoundTripHandlerFunc
import net.liftweb.http.RoundTripInfo
import net.liftweb.http.RoundTripInfo.handledBuilder
import net.liftweb.json.JString
import net.liftweb.json.JValue
import net.liftweb.json.parse
import net.liftweb.common.Full
import net.liftweb.http.DispatchSnippet
import scala.concurrent.duration._
import scala.xml.Text
import scala.util.Left
import net.liftweb.common.{ Box, Empty, Full }
import net.liftweb.common.Loggable
import net.liftweb.http.DispatchSnippet
import net.liftweb.http.S
import net.liftweb.http.SHtml
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.util.Helpers._
import net.liftmodules.ng.Angular.angular
import net.liftmodules.ng.Angular.jsObjFactory
import net.liftmodules.ng.Angular.renderIfNotAlreadyDefined
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmd
import net.liftweb.common.Failure
import net.liftweb.http.js.JsExp
import net.liftweb.http.js.JE
import net.liftweb.json._
import scala.util.Try
import scala.concurrent.Await
import net.liftweb.util.Props

class SwitchSnippet extends DispatchSnippet { 
  
  private val whence = S.referer openOr "/"
  
  override def dispatch = {
    case "edit" => edit
  }

  def edit = {
    println("switch.edit snippet")
    def clientQuery: JsExp = JE.JsRaw("""getScope().category.name""")
    def renameQuery: JsExp = JE.JsRaw("""getScope().rename.category""")
    
    def handleClick(categories: JValue): JsCmd = {
      S.notice(S.?(s"categories $categories"))
    }
    
    def handleRename(renameTag: JValue): JsCmd = {
      S.notice(S.?(s"rename $renameTag"))      
    }
    
    "#renameTag [onclick]" #> SHtml.jsonCall(renameQuery, (s: JValue) => handleRename(s))._2.cmd &
    "#saveCategoriesOutsideSwitch [onclick]" #> SHtml.jsonCall(clientQuery, (s: JValue) => handleClick(s))._2.cmd &
    "#saveCategories [onclick]" #> SHtml.jsonCall(clientQuery, (s: JValue) => handleClick(s))._2.cmd andThen SHtml.makeFormsAjax
  } 
}