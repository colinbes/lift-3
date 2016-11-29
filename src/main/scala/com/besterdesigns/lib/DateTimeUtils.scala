package com.besterdesigns.lib

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import net.liftweb.util.DateTimeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {
	private val fmt = ISODateTimeFormat.dateTime()
	private val defaultTimezone = DateTimeZone.UTC
	/**
	 * Parse ISODate formatted to YYYY-MM-DDTHH:MM:SS.sssZ
	 */
	def parse(isodate: String): DateTime = fmt.withZone(defaultTimezone).parseDateTime(isodate)
	def print(date: DateTime, tz:DateTimeZone = defaultTimezone): String = fmt.withZone(tz).print(date)	
  def printLocal(date: DateTime): String = fmt.print(date) 
	
	implicit class DateTime2String(date: DateTime) {
		def asString(utc: Boolean = true): String = utc match {
      case true => print(date)
      case false => printLocal(date)
    }
	} 
	
	implicit class String2DateTime(isodate: String) {
		def toDateTime(): DateTime = parse(isodate)
	}
}