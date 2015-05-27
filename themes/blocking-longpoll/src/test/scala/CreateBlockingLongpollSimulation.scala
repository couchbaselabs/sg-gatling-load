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
  val feedType=java.lang.System.getProperty("feedType","continuous")
  val channelActiveUsers=scala.Int.unbox(java.lang.Integer.getInteger("channelActiveUsers",40))
  val channelConcurrentUsers=java.lang.Integer.getInteger("channelConcurrentUsers",8)
  val minUserOffTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("minUserOffTimeMs",10000))
  val maxUserOffTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("minUserOffTimeMs",60000))

  //Generate a list of target URL's from the list of target hosts
  val targetURLs = targetHosts.split(",").map(_.trim.replaceFirst("^", "http://").concat(":4985/"+database)).toList

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

  val changes = exec(http("Start blocking longpoll _changes request")
    .get("/_changes?since=100&heartbeat=40000&feed=longpoll"))
}
