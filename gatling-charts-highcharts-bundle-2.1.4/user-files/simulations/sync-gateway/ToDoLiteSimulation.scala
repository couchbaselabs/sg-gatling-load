
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class ToDoLiteSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://andrews-macbook-pro.local:4984")
		.inferHtmlResources()
		.acceptHeader("application/json")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-us")
		.connection("keep-alive")
		.contentTypeHeader("application/json")
		.userAgentHeader("CouchbaseLite/1.0-Debug (iOS)")

	val headers_2 = Map("Content-Encoding" -> "gzip")

	val headers_12 = Map(
		"Accept-Encoding" -> "identity",
		"User-Agent" -> "DropboxDesktopClient/3.0.3 (Macintosh; 10.10; ('i32',); en_US)",
		"X-Dropbox-Locale" -> "en_US")

    val uri1 = "http://notify6.dropbox.com:80/subscribe"
    val uri2 = "http://andrews-macbook-pro.local:4984/todolite"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/todolite/_session")
			.resources(http("request_1")
			.get(uri2 + "/_session"),
            http("request_2")
			.post(uri2 + "/_facebook")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0002_request.txt")),
            http("request_3")
			.post(uri2 + "/_facebook")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0003_request.txt")),
            http("request_4")
			.get(uri2 + "/_local/2ff7de4237f576020adb6540ff41e06bcdc7621f")
			.check(status.is(404)),
            http("request_5")
			.get(uri2 + "/_local/c0583745d142c1bc8bb0ee1ec5a8fcaad0cc29d1")
			.check(status.is(404)),
            http("request_6")
			.post(uri2 + "/_revs_diff")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0006_request.txt"))))
		.pause(5)
		.exec(http("request_7")
			.put("/todolite/_local/c0583745d142c1bc8bb0ee1ec5a8fcaad0cc29d1")
			.body(RawFileBody("RecordedSimulation_0007_request.txt"))
			.resources(http("request_8")
			.put(uri2 + "/_local/2ff7de4237f576020adb6540ff41e06bcdc7621f")
			.body(RawFileBody("RecordedSimulation_0008_request.txt"))))
		.pause(20)
		.exec(http("request_9")
			.post("/todolite/_revs_diff")
			.body(RawFileBody("RecordedSimulation_0009_request.txt"))
			.resources(http("request_10")
			.post(uri2 + "/_bulk_docs")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0010_request.txt"))))
		.pause(5)
		.exec(http("request_11")
			.put("/todolite/_local/c0583745d142c1bc8bb0ee1ec5a8fcaad0cc29d1")
			.body(RawFileBody("RecordedSimulation_0011_request.txt"))
			.resources(http("request_12")
			.get(uri1 + "?host_int=2857095134&ns_map=723087578_9313022170%2C723074878_2586293387070&user_id=344404195&nid=0&ts=1423763271")
			.headers(headers_12)))
		.pause(5)
		.exec(http("request_13")
			.post("/todolite/_revs_diff")
			.body(RawFileBody("RecordedSimulation_0013_request.txt"))
			.resources(http("request_14")
			.post(uri2 + "/_bulk_docs")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0014_request.txt"))))
		.pause(5)
		.exec(http("request_15")
			.put("/todolite/_local/c0583745d142c1bc8bb0ee1ec5a8fcaad0cc29d1")
			.body(RawFileBody("RecordedSimulation_0015_request.txt"))
			.resources(http("request_16")
			.get(uri1 + "?host_int=2857095134&ns_map=723087578_9313022170%2C723074878_2586293387070&user_id=344404195&nid=0&ts=1423763325")
			.headers(headers_12)))
		.pause(25)
		.exec(http("request_17")
			.put("/todolite/_local/2ff7de4237f576020adb6540ff41e06bcdc7621f")
			.body(RawFileBody("RecordedSimulation_0017_request.txt"))
			.resources(http("request_18")
			.get(uri1 + "?host_int=2857095134&ns_map=723087578_9313022170%2C723074878_2586293387070&user_id=344404195&nid=0&ts=1423763363")
			.headers(headers_12),
            http("request_19")
			.get(uri1 + "?host_int=2857095134&ns_map=723087578_9313022170%2C723074878_2586293387070&user_id=344404195&nid=0&ts=1423763403")
			.headers(headers_12),
            http("request_20")
			.get(uri1 + "?host_int=2857095134&ns_map=723087578_9313022170%2C723074878_2586293387070&user_id=344404195&nid=0&ts=1423763450")
			.headers(headers_12)))
		.pause(11)
		.exec(http("request_21")
			.post("/todolite/_revs_diff")
			.body(RawFileBody("RecordedSimulation_0021_request.txt"))
			.resources(http("request_22")
			.post(uri2 + "/_bulk_docs")
			.headers(headers_2)
			.body(RawFileBody("RecordedSimulation_0022_request.txt"))))
		.pause(5)
		.exec(http("request_23")
			.put("/todolite/_local/c0583745d142c1bc8bb0ee1ec5a8fcaad0cc29d1")
			.body(RawFileBody("RecordedSimulation_0023_request.txt")))
		.pause(1)
		.exec(http("request_24")
			.put("/todolite/_local/2ff7de4237f576020adb6540ff41e06bcdc7621f")
			.body(RawFileBody("RecordedSimulation_0024_request.txt"))
			.resources(http("request_25")
			.get(uri1 + "?host_int=2857095134&ns_map=723087578_9313022170%2C723074878_2586293387070&user_id=344404195&nid=0&ts=1423763502")
			.headers(headers_12)))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}