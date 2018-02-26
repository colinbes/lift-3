package com.besterdesigns.snippet

import net.liftweb.http.DispatchSnippet

import net.liftmodules.ng.Angular.angular
import net.liftmodules.ng.Angular.jsObjFactory
import net.liftmodules.ng.Angular.renderIfNotAlreadyDefined
import net.liftweb.common.Failure
import net.liftweb.common.Full
import net.liftweb.common.Loggable
import net.liftweb.http.DispatchSnippet
import net.liftweb.http.S
import net.liftweb.util.Helpers.StringToCssBindPromoter
import com.besterdesigns.model.IndexService
import scala.concurrent.ExecutionContext.Implicits.global
import com.besterdesigns.model.FetcherJsonSerializers

object IndexSnippet extends DispatchSnippet with FetcherJsonSerializers {
    override def dispatch = {
    case "index"  => index
  }

  def index = {
    def angularScript() = {
      renderIfNotAlreadyDefined(angular.module("bc.services")
      .factory("indexService", jsObjFactory()
        .defParamToFutureAny("getDataFuture", (tag:String) => IndexService.getTestDataFuture(tag))
        .defParamToAny("getData", (tag:String) => IndexService.getTestData(tag))
      )
    )}
    "#angularscript" #> angularScript()
  }
}
