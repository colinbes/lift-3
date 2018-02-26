package com.besterdesigns.model

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

import org.joda.time.DateTime

import net.liftweb.common.Full
import net.liftweb.json.DefaultFormats

case class TestData(tag: String, other: String)

trait FetcherJsonSerializers {
  implicit val liftJsonFormats = DefaultFormats
}

object IndexService extends FetcherJsonSerializers{

  def getTestDataFuture(tag: String)(implicit ec: ExecutionContext) = Future {
    s"$tag ${DateTime.now()}"
  }

  def getTestData(tag: String)(implicit ec: ExecutionContext) =  {
    Full(Await.result(getTestDataFuture(tag), 2.seconds))
  }
}
