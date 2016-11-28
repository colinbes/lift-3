package com.besterdesigns.lib

import net.liftweb.util.Mailer
import net.liftweb.util.Mailer.{ From, Subject, To, MailBodyType }
import net.liftweb.common.{Box, Full, Failure}
import net.liftweb.http.S
import scala.util.Try
import net.liftweb.util.Props

trait EmailException extends Exception

object EmailException {
  def apply(message: String) = new Exception(message) with EmailException
  def apply(cause: Throwable) = new Exception(cause) with EmailException
  def apply(message: String, cause: Throwable) = new Exception(message, cause) with EmailException
}

object EmailHelpers {

  lazy val Pattern = """^(?i)([A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,5})$""".r

  def isValid(email: String) = {
    email match {
      case Pattern(n) => true
      case _ => false
    }
  }
  
  def url = S.hostAndPath

  /**
   * Fetch current logged in user and outfit name and return as Box[From] object
   */
//  private def fromAddress: Box[From] = {
  val fromAddress: Box[From] = {
    val fromEMail: String = Props.get("email.from", "mycloud@biocharger.com")
    val fromName: String = Props.get("email.name", "BioChargerNG")
    Full(From(fromEMail, Full(fromName)))
    //Full(From("info@advancedbiotechnologiesllc.com", Full("Advanced Biotechnologies")))
  }

  private def sendEmail(to: To, subject: Subject, body: MailBodyType): Try[Unit] = Try {
    fromAddress match {
      case Full(from) => {
        sendEmail(to, from, subject, body);
      }
      case Failure(x, _, _) => {
        throw EmailException(x)
      }
      case _ => {
        throw EmailException("Unknown error fetching 'From'email address")
      }
    }
  }
  

  /**
   * Send email using specified from user
   */
  private def sendEmail(to: To, from: From, subject: Subject, body: MailBodyType): Try[Unit] = {
    Try(Mailer.sendMail(from,
      subject,
      body,
      to));
  }

  def newService(to: To, body: MailBodyType) = {
    sendEmail(to, Subject(S.?("email.welcome")), body);
  }

//  def resetUserPassword(to: To, from: From, body: MailBodyType) = {
  def resetUserPassword(to: To, body: MailBodyType) = {
//    sendEmail(to, from, Subject(S.?("email.password.reset")), body)
    sendEmail(to, Subject(S.?("email.password.reset")), body)
  }

  def changeUserPassword(to: To, body: MailBodyType) = {
    sendEmail(to, Subject(S.?("email.password.change")), body)
  }
  
  def changeUserProfile(to: To, body: MailBodyType) = {
    sendEmail(to, Subject(S.?("email.profile.changed")), body)
  }
}