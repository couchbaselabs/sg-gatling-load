import com.github.couchbaselabs.sggatlingload.utils.TestConfig
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/*
 * Simple test to generate blocking long poll requests against a sync_gateway
 * to see
 */
class CreateBlockingLongpollSimulation extends Simulation {

  //Generate a list of target URL's from the list of target hosts
  val targetURLs = TestConfig.testParamTargetHosts.split(",").map(_.trim.replaceFirst("^", TestConfig.httpParamRestApiProtocol+"://").concat(":"+TestConfig.httpParamUserRestApiPort+"/"+TestConfig.testParamDatabase)).toList

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
      creators.inject(rampUsers(TestConfig.testParamNumPushers) over(TestConfig.testParamRampUpIntervalMs milliseconds))
    ).protocols(httpConf)
}

object Create {

  //REST API request Parameters
  val queryParamFeed=java.lang.System.getProperty("queryParamFeed","continuous")
  val queryParamSince=scala.Int.unbox(java.lang.Integer.getInteger("queryParamSince",0))
  val queryParamHeartbeat=scala.Int.unbox(java.lang.Integer.getInteger("queryParamHeartbeat",60000))

  val changes = forever(exec(http("Start blocking longpoll _changes request")
    .get("/_changes?since="+queryParamSince+"&heartbeat="+queryParamHeartbeat+"&feed="+queryParamFeed))
    .pause(2 seconds))

}
