import com.github.couchbaselabs.sggatlingload.utils.TestConfig
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import io.gatling.jsonpath.JsonPath
import scala.concurrent.duration._

/**
 * Created by andy on 27/05/15.
 */
class SlowMemoryLeakSimulation  extends Simulation {

  //Generate a list of target URL's from the list of target hosts
  //val targetURLs = targetHosts.split(",").map(_.trim.replaceFirst("^", "http://").concat(":4984/"+database)).toList
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
    .extraInfoExtractor(extraInfo => List(extraInfo.response.bodyLength))

  val create = scenario("Generate slow memory leak data").exec(Create.init)
  val sync = scenario("longpoll sync data").exec(Sync.longpoll)

  setUp(create.inject(atOnceUsers(1))).protocols(httpConf)
  setUp(sync.inject(rampUsers(TestConfig.testParamNumPushers) over(TestConfig.testParamRampUpIntervalMs milliseconds))).protocols(httpConf)
}

object Create {

  val init = repeat(35, "i") {
    exec(http("Create Document").put("/doc${i}").body(StringBody("""{"foo":"bar"}""")).asJSON)
    .exec(http("Create Attachment1").put("/doc${i}/50k.png?rev=1-cd809becc169215072fd567eebd8b8de").body(RawFileBody("")).header("Content-Type", "image/png"))
    .exec(http("Create Attachment2").put("/doc${i}/100k.jpg2-41a030863cf5a27444471d6140e5b5da").body(RawFileBody("")).header("Content-Type", "image/jpeg"))
  }

}

object Sync {

  val longpoll = exec(http("Request_1")
    .post("/endPoint")
    .body(StringBody("""REQUEST_BODY""")).asJSON
    .check(jsonPath("$.result").is("SUCCESS"))
    .check(jsonPath("$.data[*]").findAll.saveAs("pList")))
    .exec(session => {
    println(session)
    session
  })
    .foreach("${pList}", "player"){
    exec(session => {
      val playerId = JsonPath.query("$.playerId", "${player}")
      session.set("playerId", playerId)
    })
      .exec(http("Request_1")
      .post("/endPoint")
      .body(StringBody("""{"playerId":"${playerId}"}""")).asJSON
      .check(jsonPath("$.result").is("SUCCESS")))

  }

}

