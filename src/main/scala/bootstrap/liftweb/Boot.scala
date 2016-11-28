package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import com.besterdesigns.model._
import net.liftmodules.{FoBo,FoBoBs}

import scala.language.postfixOps

import net.liftweb.http.ContentSourceRestriction._
import net.liftweb.util.DefaultDateTimeConverter
import scala.xml.NodeSeq
import scala.xml.Text
import com.besterdesigns.model.User
import java.util.Date


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends BiochargerLogMenu {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      sys.props.put("h2.implicitRelativePath", "true")
      val vendor = new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
        Props.get("db.url") openOr
        "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        Props.get("db.user"), Props.get("db.password"))
      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
      DB.defineConnectionManager(util.DefaultConnectionIdentifier, vendor)
    }

    // where to search snippet
    LiftRules.addToPackages("com.besterdesigns")
    LiftRules.resourceNames = "MessageResource" :: Nil
    
    LiftRules.setSiteMap(sitemap());

    //Init the FoBo - Front-End Toolkit module,
    //see http://liftweb.net/lift_modules for more info
    FoBo.Toolkit.Init=FoBo.Toolkit.JQuery310
    FoBo.Toolkit.Init=FoBo.Toolkit.Bootstrap337
    FoBo.Toolkit.Init=FoBo.Toolkit.FontAwesome463
    FoBo.Toolkit.Init=FoBo.Toolkit.AngularJS153
    FoBo.Toolkit.Init=FoBo.Toolkit.AJSUIBootstrap0100
    FoBo.API.Init=FoBo.API.FoBo1

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))
    
    LiftRules.noticesAutoFadeOut.default.set((notices: NoticeType.Value) => {
      notices match {
        case NoticeType.Notice  => Full((5.seconds, 5.seconds))
        case NoticeType.Error   => Full((5.seconds, 5.seconds))
        case NoticeType.Warning => Full((5.seconds, 5.seconds))
        case _                  => Empty
      }
    })    

    //Lift CSP settings see http://content-security-policy.com/ and
    //Lift API for more information.
    //Like the name indicates, using UnsafeInline is ... not safe. It is used here as an example.
    LiftRules.securityRules = () => {
      SecurityRules(content = Some(ContentSecurityPolicy(
        scriptSources = List(ContentSourceRestriction.Self, ContentSourceRestriction.UnsafeInline, ContentSourceRestriction.UnsafeEval),
        styleSources = List(ContentSourceRestriction.UnsafeInline,ContentSourceRestriction.Self)
        )))
    }
    
    def logout(): List[String] = {
      User.logoutCurrentUser
      "index" :: Nil
    }    
    
    /**
     * Map 'home' destination to role based destination.
     */
    def destination(): List[String] = {

      val landing = for {
        userInfo <- User.currentUser
        //do whatever to determine landing page
        landing = "index"
      } yield landing
      
      val dest = landing match {
        case Full(page) => page :: Nil
        case _ => "login" :: Nil
      }
      
      println("destination "+dest)
      dest
    }    
    
    LiftRules.statefulRewrite.append {
        case RewriteRequest(ParsePath("logout" :: Nil, "", true, false), GetRequest, _) => RewriteResponse(logout)
    }  

    val dateTimeConverter = new net.liftweb.util.DateTimeConverter {
      def sdfDateTime = {
//        val sdf = new java.text.SimpleDateFormat("YYYY-MM-DD'T'HH:MM:SS.sssZ")
        val sdf = new java.text.SimpleDateFormat("MM/dd/yyyy h:mm:ss a")
        sdf.setTimeZone(S.timeZone)
        sdf.setLenient(true)
        sdf
      }
      def sdfDate = {
        val sdf = new java.text.SimpleDateFormat("MM/dd/yyyy")
        sdf.setTimeZone(S.timeZone)
        sdf.setLenient(true)
        sdf
      }
      def formatDateTime(d: Date) = sdfDateTime.format(d)
      def formatDate(d: Date) = sdfDate.format(d)
      def formatTime(d: Date) = DefaultDateTimeConverter.formatTime(d)

      def parseDateTime(s: String) = Helpers.tryo { sdfDateTime.parse(s) }
      def parseDate(s: String) = Helpers.tryo { sdfDate.parse(s) }
      def parseTime(s: String) = DefaultDateTimeConverter.parseTime(s)
    }
    LiftRules.dateTimeConverter.default.set(() => dateTimeConverter)
  
    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}

trait BiochargerLogMenu {
  val loggedIn = If(() => User.loggedIn_?, () => RedirectResponse("/login"))
  val loggedOut = Unless(() => User.loggedIn_?, () => RedirectResponse("/index"))
  
  def getLinkText(reference: String): NodeSeq = {
    S.loc(reference, Text(reference))
  }
  val _user = Loc("User", "userMenu" :: Nil, "User", LocGroup("user"), PlaceHolder)
 
  val test = Loc("page2", "page2" :: Nil, getLinkText("menu.page2"), LocGroup("main"), loggedIn)
  val homeLoc = Loc("homePage", "index" :: Nil, "Home", loggedIn)

  val loginLoc = Loc("Login", "login" :: Nil, getLinkText("menu.user.login"), loggedOut)
  val logoutLoc = Loc("Logout", "logout" :: Nil, getLinkText("menu.user.logout"), loggedIn)
  val internalServerErrorLoc = Loc("500", "500" :: Nil, "Server Error", Hidden)
 
  val userMenus = List(Menu(loginLoc), Menu(logoutLoc))  

  // Build SiteMap
  def sitemap() =
    SiteMap(
      Menu(homeLoc), 
      Menu(test),
      Menu(_user, userMenus: _*))
}
