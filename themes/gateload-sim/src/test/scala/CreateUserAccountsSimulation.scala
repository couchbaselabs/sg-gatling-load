import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class CreateUserAccountsSimulation extends Simulation {

  val targetHost=java.lang.System.getProperty("targetHost","localhost")
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

  val httpConf = http
    .baseURL("http://"+targetHost+":4985/"+database) // Here is the root for all relative URLs
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-us")
    .connection("keep-alive")
    .contentTypeHeader("application/json")
    .userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")
    .maxConnectionsPerHost(1)

    val writers = scenario("Create Test Users").exec(Create.write)

    setUp(
      writers.inject(rampUsers(numPullers + numPushers) over (rampUpIntervalMs milliseconds))
    ).protocols(httpConf)
}

object Create {

  val post_headers = Map("Content-Type" -> "application/json")

  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  // first, let's build a Feeder that set an numeric id:
  val userIdFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val channelActiveUsers=scala.Int.unbox(java.lang.Integer.getInteger("channelActiveUsers",40))

  val write = feed(userIdFeeder).exec( session => session.set("hostname", hostname))
    .exec( session => session.set("userChannel", "channel-"+hostname+"-"+scala.Int.unbox(session("userId").as[Integer])/channelActiveUsers))
    .exec(http("Create New User")
    .put("/_user/user-${hostname}-${userId}")
    .headers(post_headers)
    .body(ELFileBody("create_user_request.txt")).asJSON)
}
