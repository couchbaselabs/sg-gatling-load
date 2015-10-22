package com.github.couchbaselabs.sggatlingload.utils

object TestConfig {

  //Get properties from jVM arguments
  val testParamTargetHosts=java.lang.System.getProperty("testParamTargetHosts","localhost")
  val testParamDatabase=java.lang.System.getProperty("testParamDatabase","db")
  val testParamRampUpIntervalMs=scala.Int.unbox(java.lang.Integer.getInteger("testParamRampUpIntervalMs",3600000))
  val testParamRunTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("testParamRunTimeMs",7200000))
  val testParamSleepTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("testParamSleepTimeMs",10000))
  val testParamNumPullers=scala.Int.unbox(java.lang.Integer.getInteger("testParamNumPullers",700))
  val testParamNumPushers=scala.Int.unbox(java.lang.Integer.getInteger("testParamNumPushers",300))
  val testParamChannelActiveUsers=scala.Int.unbox(java.lang.Integer.getInteger("testParamChannelActiveUsers",40))
  val testParamChannelConcurrentUsers=java.lang.Integer.getInteger("testParamChannelConcurrentUsers",8)
  val testParamMinUserOffTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("testParamMinUserOffTimeMs",10000))
  val testParamMaxUserOffTimeMs=scala.Int.unbox(java.lang.Integer.getInteger("testParamMaxUserOffTimeMs",60000))
  val testParamDocSize=scala.Int.unbox(java.lang.Integer.getInteger("testParamDocSize",1024))
  val testParamPayloadSize=scala.Int.unbox(java.lang.Integer.getInteger("testParamPayloadSize",100))
  val testParamNumPayloads=scala.Int.unbox(java.lang.Integer.getInteger("testParamNumPayloads",1000))

  //HTTP parameters
  val httpParamRestApiProtocol=java.lang.System.getProperty("httpParamRestApiProtocol","http") //http or https
  val httpParamAdminRestApiPort=scala.Int.unbox(java.lang.Integer.getInteger("httpParamAdminRestApiPort",4985))
  val httpParamUserRestApiPort=scala.Int.unbox(java.lang.Integer.getInteger("httpParamUserRestApiPort",4984))


  //REST API request Parameters
  val queryParamFeed=java.lang.System.getProperty("queryParamFeed","continuous")
  //TODO: since can be any JSON value
  val queryParamSince=scala.Int.unbox(java.lang.Integer.getInteger("queryParamSince",0))
  val querytParamHeartbeat=scala.Int.unbox(java.lang.Integer.getInteger("queryParamHeartbeat",60000))
  val querytParamConflicts=java.lang.Boolean.getBoolean("querytParamConflicts")
  val querytParamDescending=java.lang.Boolean.getBoolean("querytParamDescending")
  val queryParamEndkey=java.lang.System.getProperty("queryParamEndkey","")
  val queryParamEnd_Key=java.lang.System.getProperty("queryParamEnd_key","")
  val queryParamEndkey_docid=java.lang.System.getProperty("queryParamEndkey_docid","")
  val queryParamEnd_Key_doc_id=java.lang.System.getProperty("queryParamEnd_Key_doc_id","")
  val queryParamInclude_docs=java.lang.Boolean.getBoolean("queryParamInclude_docs")
  val queryParamInclusive_end=java.lang.Boolean.getBoolean("queryParamInclusive_end")
  val queryParamKey=java.lang.System.getProperty("queryParamKey","")
  val querytParamLimit=scala.Int.unbox(java.lang.Integer.getInteger("querytParamLimit",0))
  val querytParamSkip=scala.Int.unbox(java.lang.Integer.getInteger("querytParamSkip",0))
  val queryParamStale=java.lang.System.getProperty("queryParamStale","ok")
  val queryParamStartkey=java.lang.System.getProperty("queryParamStartkey","")
  val queryParamStart_key=java.lang.System.getProperty("queryParamStart_key","")
  val queryParamStartkey_docid=java.lang.System.getProperty("queryParamStartkey_docid","")
  val queryParamStart_key_doc_id=java.lang.System.getProperty("queryParamStart_key_doc_id","")
  val querytParamUpdate_seq=java.lang.Boolean.getBoolean("querytParamUpdate_seq")
  val queryParamAttachments=java.lang.Boolean.getBoolean("queryParamAttachments")
  val queryParamAtt_encoding_info=java.lang.Boolean.getBoolean("queryParamAtt_encoding_info")
  val queryParamDeleted_conflicts=java.lang.Boolean.getBoolean("queryParamDeleted_conflicts")
  //atts_since
  val queryParamLatest=java.lang.Boolean.getBoolean("queryParamLatest")
  val queryParamLocal_seq=java.lang.Boolean.getBoolean("queryParamLocal_seq")
  val queryParamMeta=java.lang.Boolean.getBoolean("queryParamMeta")
  //open_revs
  val queryParamRev=java.lang.System.getProperty("queryParamRev","")
  val queryParamRevs=java.lang.Boolean.getBoolean("queryParamRevs")
  val queryParamRevs_info=java.lang.Boolean.getBoolean("queryParamRevs_info")
  //doc_ids
  val queryParamFilter=java.lang.System.getProperty("queryParamFilter","")
  val querytParamLast_event_id=scala.Int.unbox(java.lang.Integer.getInteger("querytParamLast_event_id",0))
  val queryParamStyle=java.lang.System.getProperty("queryParamStyle","")
  val querytParamTimeout=scala.Int.unbox(java.lang.Integer.getInteger("querytParamTimeout",0))
  val queryParamView=java.lang.System.getProperty("queryParamView","")

}
