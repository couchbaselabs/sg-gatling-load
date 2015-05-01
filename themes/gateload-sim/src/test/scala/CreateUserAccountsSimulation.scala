import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class CreateUserAccountsSimulation extends Simulation {

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

    val creators = scenario("Create Test Users").exec(Create.write)

    setUp(
      //creators.inject(rampUsers(numPullers + numPushers) over (rampUpIntervalMs milliseconds))
      creators.inject(constantUsersPerSec((numPullers + numPushers)*1000/rampUpIntervalMs) during(rampUpIntervalMs milliseconds))
      
    ).protocols(httpConf)
}

object Create {

  val post_headers = Map("Content-Type" -> "application/json")

  // local hostname is used to uniquely identify users created from this test client
  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  // feeder that is called once per test user and generates a unique user Id
  val userIdFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val channelActiveUsers=scala.Int.unbox(java.lang.Integer.getInteger("channelActiveUsers",40))

  val write = feed(userIdFeeder).exec( session => session.set("hostname", hostname))
    .exec( session => session.set("userChannel", "channel-"+hostname+"-"+scala.Int.unbox(session("userId").as[Integer])/channelActiveUsers))
    .exec(http("Create New User")
    .put("/_user/user-${hostname}-${userId}")
    .headers(post_headers)
    .body(ELFileBody("create_user_request.txt")).asJSON)
}
