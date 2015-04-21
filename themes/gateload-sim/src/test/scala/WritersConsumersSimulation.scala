import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class WritersConsumersSimulation extends Simulation {

  val httpConf = http
    .baseURL(java.lang.System.getProperty("baseURL","http://localhost:4985/db")) // Here is the root for all relative URLs
    .wsBaseURL(java.lang.System.getProperty("baseURL","http://localhost:4985/db"))
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-us")
    .connection("keep-alive")
    .contentTypeHeader("application/json")
    .userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")

    

    val writers = scenario("SG Writers").exec(Write.write)
    val consumers = scenario("SG Consumers").exec(Consume.consume)

    val rampUserCount = Integer.getInteger("users", 1)
    val rampUpTime  = java.lang.Long.getLong("ramp", 0L)

    setUp(
      writers.inject(rampUsers(rampUserCount) over (rampUpTime seconds)),
      consumers.inject(rampUsers(rampUserCount) over (rampUpTime seconds))
    ).protocols(httpConf)
}

object Write {

  val hostname = java.net.InetAddress.getLocalHost().getHostName()
  val timestamp = java.lang.System.currentTimeMillis()

  val post_headers = Map("Content-Type" -> "application/json")
  val feeder = Iterator.continually(Map("userId" -> (Random.alphanumeric.take(16).mkString)))

  val write = exec(feed(feeder)).repeat(10000, "n") {
    exec(http("Create New Doc")
      .put("/doc${userId}-${timestamp}")
      .headers(post_headers)
      .body(RawFileBody("create_doc_request.txt")))
  }
}

object Consume {
  val consume = exec(ws("Get continuous changes").open("/_changes?feed=websockets"))
  val check = exec(ws("Check Incoming Changes").check(wsAwait.until(10000).regex([A-Z]?-(.*)")))
}
