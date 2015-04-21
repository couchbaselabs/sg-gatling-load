import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class CreateUserAccountsSimulation extends Simulation {

  val httpConf = http
    .baseURL(java.lang.System.getProperty("baseURL","http://localhost:4985/db")) // Here is the root for all relative URLs
    .inferHtmlResources()
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-us")
    .connection("keep-alive")
    .contentTypeHeader("application/json")
    .userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")

    

    val writers = scenario("Create Users").exec(Create.write)

    val rampUserCount = Integer.getInteger("users", 1)
    val rampUpTime  = java.lang.Long.getLong("ramp", 0L)

    setUp(
      writers.inject(rampUsers(rampUserCount) over (rampUpTime seconds))
    ).protocols(httpConf)
}

object Create {

  val post_headers = Map("Content-Type" -> "application/json")

  val hostname = java.net.InetAddress.getLocalHost().getHostName();

  val write = {
    exec(http("Create New User")
      .put("/users/${hostname}${n}")
      .headers(post_headers)
      .body(RawFileBody("create_user_request.txt")))
  }
}
