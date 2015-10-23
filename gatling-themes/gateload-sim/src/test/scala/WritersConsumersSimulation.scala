import com.github.couchbaselabs.sggatlingload.utils._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class WritersConsumersSimulation extends Simulation {

  //Generate a list of target URL's from the list of target hosts
  val targetURLs = TestConfig.testParamTargetHosts.split(",").map(_.trim.replaceFirst("^", TestConfig.httpParamRestApiProtocol+"://").concat(":"+TestConfig.httpParamAdminRestApiPort+"/"+TestConfig.testParamDatabase)).toList

  System.err.println("targetURL's = "+targetURLs)

  val httpConf = http
    .baseURLs(targetURLs)
    //.acceptHeader("application/json")
    //.acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-us")
    .connection("keep-alive")
    .contentTypeHeader("application/json")
    .userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")
    .extraInfoExtractor(extraInfo => List(extraInfo.session.attributes.get("latency_values")))
    //.extraInfoExtractor(extraInfo => List(extraInfo.session.attributes.get("userChannel"),extraInfo.session.attributes.get("hostname"),extraInfo.session.attributes.get("userId")))
    //.extraInfoExtractor(extraInfo => List(extraInfo.session.attributes.get("userChannel")))
    //   .extraInfoExtractor(extraInfo => List(extraInfo.session.attributes.get("hostname")))
    //.extraInfoExtractor(extraInfo => List(extraInfo.session.attributes.get("userId")))
  //.extraInfoExtractor(extraInfo => List(extraInfo.response.latencyInMillis))


    val writers = scenario("SG Writers").exec(Write.write)
    val consumers = scenario("SG Consumers").exec(Consume.consume)

    setUp(
      writers.inject(rampUsers(TestConfig.testParamNumPushers) over (TestConfig.testParamRampUpIntervalMs milliseconds)),
      consumers.inject(rampUsers(TestConfig.testParamNumPullers) over (TestConfig.testParamRampUpIntervalMs milliseconds))
    ).protocols(httpConf)


}

/*
object Users {

  val post_headers = Map("Content-Type" -> "application/json")

  // local hostname is used to uniquely identify users created from this test client
  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  // feeder that is called once per test user and generates a unique user Id
  val userIdFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val channelActiveUsers=scala.Int.unbox(java.lang.Integer.getInteger("testParamChannelActiveUsers",40))

  val usercreate = feed(userIdFeeder).exec( session => session.set("hostname", hostname))
    .exec( session => session.set("userChannel", "channel-"+hostname+"-"+scala.Int.unbox(session("userId").as[Integer])/channelActiveUsers))
    .exec(http("Create New User")
    .put("/_user/user-${hostname}-${userId}")
    .headers(post_headers)
    .body(ELFileBody("create_user_request.txt")).asJSON)
}
*/

object Write {

  // local hostname is used to uniquely identify users created from this test client
  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  // feeder that is called once per test user and generates a unique user Id
  val userIdFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val post_headers = Map("Content-Type" -> "application/json")

  val payload = Utils.generateDocumentPayload(100,1000,2000)

  val write = feed(userIdFeeder).exec( session => session.set("hostname", hostname)).exec( session => session.set("userChannel", "channel-"+hostname+"-"+scala.Int.unbox(session("userId").as[Integer])/TestConfig.testParamChannelActiveUsers)).repeat(10000, "n") {
      exec( session => session.set("timestamp", java.lang.System.currentTimeMillis()))
      .exec(http("Create New Doc")
      .put("/doc${userId}-${timestamp}")
      .headers(post_headers)
      .body(ELFileBody("create_doc_request.txt")))
      .pause(1)
  }
}

object Consume {
  // local hostname is used to uniquely identify users created from this test client
  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  // feeder that is called once per test user and generates a unique user Id
  val userIdFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val consume = feed(userIdFeeder).exec( session => session.set("hostname", hostname)).exec(PullReplicator.pullreplication)
}

/*
object Create2 {

  //REST API request Parameters
  val queryParamFeed=java.lang.System.getProperty("queryParamFeed","continuous")
  val queryParamSince=scala.Int.unbox(java.lang.Integer.getInteger("queryParamSince",0))
  val queryParamHeartbeat=scala.Int.unbox(java.lang.Integer.getInteger("queryParamHeartbeat",60000))

  val changes = forever(exec(http("Start blocking longpoll _changes request")
    .get("/_changes?since="+queryParamSince+"&heartbeat="+queryParamHeartbeat+"&feed="+queryParamFeed))
    .pause(2 seconds))

}
*/