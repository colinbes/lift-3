package com.besterdesigns.lib

import net.liftweb.common._
import net.liftweb.http.LiftRules
import net.liftweb.http.PlainTextResponse
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.LiftResponse
import scala.io.Source
import java.io.ByteArrayInputStream
import net.liftweb.http.StreamingResponse

object BackendServer extends RestHelper {
  def usersEula(userId: String): Box[LiftResponse] = {
      val msg = s"yay $userId that worked"
      val headers = ("Content-type" -> "text/html") :: ("X-myheader" -> "ABT") :: ("Content-length" -> msg.length().toString) :: Nil
      val stream = new ByteArrayInputStream(msg.getBytes)
      
      val res = StreamingResponse(stream, 
                        () => stream.close,
                        msg.length(),
                        headers = headers,
                        cookies = Nil,
                        200)
      Full(res)
  }
  
  def init() {
    LiftRules.statelessDispatch.append(BackendServer)
  }

  serve {
    case "api" :: "eula" :: userId :: Nil Get _ => usersEula(userId)
  }
}