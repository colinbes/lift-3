package bootstrap.liftweb

import java.util.Date
import scala.language.postfixOps
import scala.xml.NodeSeq
import scala.xml.Text
import com.besterdesigns.model.User
import net.liftmodules.FoBoBsAPI
import net.liftweb.common.Empty
import net.liftweb.common.Full
import net.liftweb.http.ContentSecurityPolicy
import net.liftweb.http.ContentSourceRestriction
import net.liftweb.http.GetRequest
import net.liftweb.http.Html5Properties
import net.liftweb.http.LiftRules
import net.liftweb.http.LiftRulesMocker.toLiftRules
import net.liftweb.http.NoticeType
import net.liftweb.http.ParsePath
import net.liftweb.http.RedirectResponse
import net.liftweb.http.Req
import net.liftweb.http.RewriteRequest
import net.liftweb.http.RewriteResponse
import net.liftweb.http.S
import net.liftweb.http.SecurityRules
import net.liftweb.sitemap.Loc
import net.liftweb.sitemap.Loc.Hidden
import net.liftweb.sitemap.Loc.If
import net.liftweb.sitemap.Loc.LocGroup
import net.liftweb.sitemap.Loc.PlaceHolder
import net.liftweb.sitemap.Loc.Unless
import net.liftweb.sitemap.Menu
import net.liftweb.sitemap.SiteMap
import net.liftweb.util
import net.liftweb.util.DefaultDateTimeConverter
import net.liftweb.util.Helpers
import net.liftweb.util.Helpers.intToTimeSpanBuilder
import net.liftweb.util.Props
import net.liftweb.util.Vendor.funcToVendor
import net.liftweb.util.Vendor.valToVendor
import scala.concurrent.ExecutionContext
import net.liftweb.http.LiftSession

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends BiochargerLogMenu {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.besterdesigns")
    LiftRules.resourceNames = "MessageResource" :: Nil
    LiftRules.setSiteMap(sitemap());      
     
    //Init the FoBo - Front-End Toolkit module
//    FoBo.Toolkit.Init=FoBo.Toolkit.JQuery310
//    FoBo.Toolkit.Init=FoBo.Toolkit.Bootstrap337
//    FoBo.Toolkit.Init=FoBo.Toolkit.FontAwesome463
//    FoBo.Toolkit.Init=FoBo.Toolkit.AngularJS153
//    FoBo.Toolkit.Init=FoBo.Toolkit.AJSUIBootstrap0100
//    FoBo.API.Init=FoBo.API.FoBo1
    
    FoBoBsAPI.API.Init = FoBoBsAPI.API.Bootstrap3
   
    net.liftmodules.ng.Angular.init(
      futures = false,
      appSelector = "[ng-app]",
      includeJsScript = true)   
    
    //val context:ExecutionContext = scala.concurrent.ExecutionContext.global // Create context
    //net.liftmodules.ng.AngularExecutionContext(context) // Tell lift-ng to use it
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

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
        defaultSources = List(ContentSourceRestriction.Self,
                          ContentSourceRestriction.Host("""https://cdnjs.cloudflare.com""")),
        scriptSources = List(ContentSourceRestriction.Self,
                             ContentSourceRestriction.UnsafeInline,
                             ContentSourceRestriction.UnsafeEval,
                             ContentSourceRestriction.Host("""https://cdnjs.cloudflare.com""")),
        styleSources = List(ContentSourceRestriction.UnsafeInline,
                            ContentSourceRestriction.Self,
                            ContentSourceRestriction.Host("""https://cdnjs.cloudflare.com"""))
        )))
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
      
      landing match {
        case Full(page) => page :: Nil
        case _ => "login" :: Nil
      }
    }    
    
    def logout(): List[String] = {
      User.logoutCurrentUser
      "login" :: Nil
    }    

    //catch 'logout', log out user and send to login page
    LiftRules.statefulRewrite.append {
      case RewriteRequest(ParsePath("home" :: Nil, "", true, false), GetRequest, _)   => RewriteResponse(destination)
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
    //S.addAround(DB.buildLoanWrapper)
  }
}

trait BiochargerLogMenu {
  val loggedIn = If(() => User.loggedIn_?, () => RedirectResponse("/login"))
  val loggedOut = Unless(() => User.loggedIn_?, () => RedirectResponse("/index"))
  
  def getLinkText(reference: String): NodeSeq = {
    S.loc(reference, Text(reference))
  }
  val _user = Loc("User", "userMenu" :: Nil, "User", LocGroup("user"), PlaceHolder)
 
  val test = Loc("page2", "page2" :: Nil, getLinkText("menu.page2"), LocGroup("main"))//, loggedIn)
  val homeLoc = Loc("homePage", "index" :: Nil, "Home")//, loggedIn)

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
