package com.github.couchbaselabs.sggatlingload.utils

import java.util

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.json.JSON
import org.boon.core.value.{ValueMap, ValueList}

import scala.collection.mutable.ListBuffer

object PullReplicator {

  val pullinit = exec( session => session.set("lastSequence", 0))

  val dbhash = exec (session => session.set("dbhash",Utils.hash(session("userId").as[Integer].toString().getBytes())))

  /*
   * Call _changes,
   * From response store lastSequence value and the list of changes
   */
  val changes = exec(http("Get remote checkpoint")
    .get("/_local/${dbhash}")
    .check(jsonPath("$.lastSequence").exists, jsonPath("$.lastSequence").saveAs("lastSequence"))
    .check(jsonPath("$._rev").exists, jsonPath("$._rev").saveAs("_checkpoint_rev")))
    .exec(http("One shot _changes request")
    .post("/_changes")
    .body(StringBody("""{"style":"all_docs","heartbeat":300000,"feed":"${feedtype}","since":"${lastSequence}" }""")).asJSON
    .check(jsonPath("$.last_seq").exists, jsonPath("$.last_seq").saveAs("lastSequence"))
    .check(jsonPath("$.results").exists, jsonPath("$.results[*]").ofType[Map[String,Any]].findAll.saveAs("changeList")))


  /*
   * Parse the response from a _changes call and build the _bulk_docs 'docs' property pauyload
   */
  val build_bulk_get_payload =
    exec(session => { session.set("bulk_get_docs", new scala.collection.mutable.ListBuffer[Map[String,Any]]()) })
      .exec(foreach("${changeList}", "change") {
        exec(session => {
          val changeMap = session("change").as[Map[String, Any]]
          val docId = changeMap("id")
          val revs = changeMap("changes").asInstanceOf[ValueList]
          val rev0 = revs.get(0).asInstanceOf[ValueMap[String, Any]]
          val revId = rev0.get("rev")
          val docsList = session("bulk_get_docs").as[ListBuffer[Map[String,Any]]]
          docsList += Map("id" -> docId, "rev" -> revId)
          session.set("bulk_get_docs", docsList)
        })
      })
  .exec (session => { //Marshall to String as gatling does not do this
      val docsList = session("bulk_get_docs").as[ListBuffer[Map[String,Any]]]
      session.set("bulk_get_docs_string", JSON.stringify(docsList,false))
    })

  val generate_latency_values =
    exec(session => { session.set("latency_values", new scala.collection.mutable.ListBuffer[Long]()) })
      .exec(foreach("${changeList}", "change") {
        exec(session => {
          val changeMap = session("change").as[Map[String, Any]]
          val docId = changeMap("id").asInstanceOf[String]
          val docTimestamp = docId.substring(docId.lastIndexOf("-") + 1).toLong
          val latency = System.currentTimeMillis - docTimestamp
          if (latency < 3600000) {
            val latencyValuesList = session("latency_values").as[ListBuffer[Long]]
            latencyValuesList += latency
            session.set("bulk_get_docs", latencyValuesList)
          } else {
            session
          }
        })
      })
  /*
   * Executes a _bulk_get using the list of id's and rev's stored in the 'bulk_get_docs'
   * session property
   */
  val bulkget = exec(http("Bulk get changed revisions")
    .post("/_bulk_get?revs=true&attachments=true")
    .header("Accept", "multipart/related")
    .body(StringBody("""{"docs":${bulk_get_docs_string}}""")))
    .exec(generate_latency_values)
    .doIfOrElse(session => session.contains("_checkpoint_rev")) {
      //This is a new revision of the checkpoint doc so pass current _rev value
      exec(http("Put remote checkpoint")
      .put("/_local/${dbhash}")
      .body(StringBody("""{"_rev":"${_checkpoint_rev}","lastSequence":"${lastSequence}" }""")).asJSON)
    } {
      //This is the first revision of the checkpoint doc so don't pass "_rev" property
      exec(http("Put remote checkpoint")
      .put("/_local/${dbhash}")
      .body(StringBody("""{"lastSequence":"${lastSequence}" }""")).asJSON)
    }

  /*
   * Combines the three steps for a single pull
   * Call changes, build the _bulk_get docs payload from the _changes response, then call _bulk_get
   */
  val onepull = exec(changes).exec(build_bulk_get_payload).exec(bulkget)

  //Run a pull replication
  val pullreplication = exec(pullinit)
    .exec(dbhash)
    .exec(session => session.set("feedtype", "normal"))
    .exec(onepull)
    .exec(session => session.set("feedtype", "longpoll"))
    .repeat(1000) {exec(onepull)}

}
