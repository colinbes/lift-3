package com.besterdesigns.model

import net.liftweb.http.SessionVar
import net.liftweb.common.{Box, Full, Empty}

trait BaseUserService {
  private object curUser extends SessionVar[Box[UserInfo]](Empty)
  def currentUser: Box[UserInfo] = curUser.is

  def loggedIn_? : Boolean = {
    currentUser.isDefined
  }
  
  def logUserIn(who: UserInfo) {
    //perform any auditing here
    curUser.remove()
    curUser(Full(who))
  }
   
  def logoutCurrentUser = {
    //perform any auditing here
    curUser.remove()
  }
}

object User extends BaseUserService  {
  
}

case class UserInfo(email:String)

