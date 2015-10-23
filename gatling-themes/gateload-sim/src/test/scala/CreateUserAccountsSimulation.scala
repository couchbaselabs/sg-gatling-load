import com.github.couchbaselabs.sggatlingload.utils.TestConfig
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

/*
 * This simulation creates a set of user accounts for use with the gateload simulation
 * The number of user accounts created is the sum of all pullers and pushers
 * Each user is allocated
 */
class CreateUserAccountsSimulation extends Simulation {

  //Generate a list of target URL's from the list of target hosts
  val targetURLs = TestConfig.testParamTargetHosts.split(",").map(_.trim.replaceFirst("^", TestConfig.httpParamRestApiProtocol+"://").concat(":"+TestConfig.httpParamAdminRestApiPort+"/"+TestConfig.testParamDatabase)).toList

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

    val creators = scenario("Create Test Users").exec(Create.write)

    setUp(
      creators.inject(rampUsers(TestConfig.testParamNumPushers+TestConfig.testParamNumPullers) over(TestConfig.testParamRampUpIntervalMs milliseconds))
    ).protocols(httpConf)
}



object Create {

  val post_headers = Map("Content-Type" -> "application/json")

  // local hostname is used to uniquely identify users created from this test client
  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  // feeder that is called once per test user and generates a unique user Id
  val userIdFeeder = Iterator.from(0).map(i => Map("userId" -> i))

  val write = feed(userIdFeeder).exec( session => session.set("hostname", hostname))
    .exec( session => session.set("userChannel", "channel-"+hostname+"-"+scala.Int.unbox(session("userId").as[Integer])/TestConfig.testParamChannelActiveUsers))
    .exec(http("Create New User")
    .put("/_user/user-${hostname}-${userId}")
    .headers(post_headers)
    .body(ELFileBody("create_user_request.txt")).asJSON)
}
