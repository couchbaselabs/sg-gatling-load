import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

/*
 * Simple test to generate blocking long poll requests against a sync_gateway
 * to see
 */
class CreateBlockingLongpollSimulation extends Simulation {

  //Get properties from jVM arguments
  val targetHosts=java.lang.System.getProperty("targetHosts","localhost")
  val database=java.lang.System.getProperty("database","sync_gateway")
  val docSize=scala.Int.unbox(java.lang.Integer.getInteger("docSize",1024))
  val rampUpIntervalMs=scala.Int.unbox(java.lang.Integer.getInteger("rampUpIntervalMs",3600000))
  val runTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("rampUpIntervalMs",7200000))
  val sleepTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("sleepTimeMs",10000))
  val numPullers=scala.Int.unbox(java.lang.Integer.getInteger("numPullers",700))
  val numPushers=scala.Int.unbox(java.lang.Integer.getInteger("numPushers",300))

  val channelActiveUsers=scala.Int.unbox(java.lang.Integer.getInteger("channelActiveUsers",40))
  val channelConcurrentUsers=java.lang.Integer.getInteger("channelConcurrentUsers",8)
  val minUserOffTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("minUserOffTimeMs",10000))
  val maxUserOffTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("minUserOffTimeMs",60000))

  //HTTP parameters
  val restApiProtocol=java.lang.System.getProperty("restApiProtocol","http") //http or https
  val adminRestApiPort=scala.Int.unbox(java.lang.Integer.getInteger("adminRestApiPort",4985))
  val userRestApiPort=scala.Int.unbox(java.lang.Integer.getInteger("userRestApiPort",4984))


  //REST API request Parameters
  val queryParamFeed=java.lang.System.getProperty("queryParamFeed","continuous")
  val queryParamSince=scala.Int.unbox(java.lang.Integer.getInteger("queryParamSince",0))
  val querytParamHeartbeat=scala.Int.unbox(java.lang.Integer.getInteger("queryParamHeartbeat",60000))
  val querytParamConflicts=java.lang.Boolean.getBoolean("querytParamConflicts")
  val querytParamDescending=java.lang.Boolean.getBoolean("querytParamDescending")
  val queryParamEndkey=java.lang.System.getProperty("queryParamEndkey","")
  val queryParamEnd_Key=java.lang.System.getProperty("queryParamEnd_key","")
  val queryParamEndkey_docid=java.lang.System.getProperty("queryParamEndkey_docid","")
  val queryParamEnd_Key_doc_id=java.lang.System.getProperty("queryParamEnd_Key_doc_id","")
  val querytParamInclude_docs=java.lang.Boolean.getBoolean("querytParamInclude_docs")
  val querytParamInclusive_end=java.lang.Boolean.getBoolean("querytParamInclusive_end")

  //Generate a list of target URL's from the list of target hosts
  val targetURLs = targetHosts.split(",").map(_.trim.replaceFirst("^", restApiProtocol+"://").concat(":"+adminRestApiPort+"/"+database)).toList

  System.err.println("targetURL's = "+targetURLs)

  val httpConf = http
    .baseURLs(targetURLs)
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-us")
    .connection("keep-alive")
    .contentTypeHeader("application/json")
    .userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")
    .extraInfoExtractor(extraInfo => List(extraInfo.response.bodyLength))

    val creators = scenario("Create blocking longpoll _changes requests").exec(Create.changes)

    setUp(
      creators.inject(rampUsers(numPushers) over(rampUpIntervalMs milliseconds))
    ).protocols(httpConf)
}

object Create {

  //REST API request Parameters
  val queryParamFeed=java.lang.System.getProperty("requestParamFeed","continuous")
  val queryParamSince=scala.Int.unbox(java.lang.Integer.getInteger("requestParamSince",0))
  val queryParamHeartbeat=scala.Int.unbox(java.lang.Integer.getInteger("requestParamHeartbeat",60000))

  val changes = forever(exec(http("Start blocking longpoll _changes request")
    .get("/_changes?since="+queryParamSince+"&heartbeat="+queryParamHeartbeat+"&feed="+queryParamFeed))
    .pause(2 seconds))

}
