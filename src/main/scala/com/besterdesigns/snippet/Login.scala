package com.besterdesigns.snippet

import net.liftweb.json._
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmds
import org.apache.commons.codec.binary.Base64
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.http.SHtml.ElemAttr.pairToBasic
import net.liftweb.http.js.JsCmd.unitToJsCmd
import com.besterdesigns.lib._
import com.besterdesigns.model.User
import com.besterdesigns.model.UserInfo

class LoginSnippet {
  private val cookieName = "RememberMe1298"
  private val TimeToLive = 24 * 60 * 60;
  private var save = false;
  private var email: String = "";
  private var password: String = ""
  
  private def loginUser(user: UserInfo, save: Boolean) {
    User.logUserIn(user)
    if (save) {
      val email = user.email
      val value = email + ":" + save
      val enc = Base64.encodeBase64String(value.getBytes());
      val ck = HTTPCookie(cookieName, enc).setMaxAge(TimeToLive).setPath("/")
      S.addCookie(ck)
    } else {
      //Calling S.delete on it's own doesn't remove cookie so force by setting timeout.
      val ck = HTTPCookie(cookieName, Base64.encodeBase64String("force timeout".getBytes())).setMaxAge(0).setPath("/")
      S.addCookie(ck)
      S.deleteCookie(ck)
    }
  }
  
  private def setEmail(em: String) {
      email = em
  }

  def render() = {
    var focusedId = "email"
    save = false
    email = ""
    password = ""

    def authenticateUser() = {
      if (EmailHelpers.isValid(email) && password=="password") { //simple check, just placeholder
        val user = UserInfo(email)
        loginUser(user, save)
        S.redirectTo("/index")      
      } else {
        S.error(S.?("login.fail.authentication"))
      }
    }
    
    //Check cookie to populate form
    for {
      cookie1 <- S.findCookie(cookieName)
      enc <- cookie1.value
      values = new String(Base64.decodeBase64(enc)).split(":") if values.length == 2
    } {
      if (EmailHelpers.isValid(values(0))) email = values(0)
      save = values(1).toBoolean
      focusedId = "password"
    }
    
    "name=save" #> SHtml.checkbox(save, save = _, "id" -> "remember") &
    "#email" #> SHtml.email(email, setEmail _) &
    "#password" #> SHtml.password("", password = _) &
    "type=submit" #> SHtml.ajaxOnSubmit(authenticateUser) &
    "#setfocus" #> JsCmds.Script(JsCmds.Focus(focusedId)) andThen SHtml.makeFormsAjax
  }
}