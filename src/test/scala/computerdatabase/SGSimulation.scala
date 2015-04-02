package computerdatabase

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SGimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:4985/db") // Here is the root for all relative URLs
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-us")
    .connection("keep-alive")
    .contentTypeHeader("application/json")
    .userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")

  

    val writers = scenario("SG Writers").exec(Write.write)
    val consumers = scenario("SG Consumers").exec(Consume.consume)

    setUp(
      writers.inject(rampUsers(10) over (100 seconds)),
      consumers.inject(rampUsers(10) over (100 seconds))
    ).protocols(httpConf)
}

object Write {

  val post_headers = Map("Content-Type" -> "application/json")

  val session: Session = ???
  val sessionid = exec({session => session("sessionId").as[String]})


  val attribute: SessionAttribute = session("sessionId")

  val write = repeat(100, "n") {
    exec(http("Create New Doc")
      .put("/doc${attribute}${n}")
      .headers(post_headers)
      .body(RawFileBody("create_doc_request.txt")))
      .pause(1)
  }
}

object Consume {
  val consume = exec(http("Get Changes").get("/_changes"))
}
