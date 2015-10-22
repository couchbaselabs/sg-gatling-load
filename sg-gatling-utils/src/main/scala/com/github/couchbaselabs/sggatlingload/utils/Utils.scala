package com.github.couchbaselabs.sggatlingload.utils

import java.security.MessageDigest
import io.gatling.core.util.StringHelper

object Utils {

  val utiltestParamPayloadSize=scala.Int.unbox(java.lang.Integer.getInteger("testParamPayloadSize",100))

  def generateDocumentPayload(Prob: Int, MinSize: Int, MaxSize: Int): String = {
    val testParamPayloadSize=scala.Int.unbox(java.lang.Integer.getInteger("testParamPayloadSize",100))
    val testParamNumPayloads=scala.Int.unbox(java.lang.Integer.getInteger("testParamNumPayloads",1000))
    // Random generator
    val random = new scala.util.Random

    // Generate a random string of length n from the given alphabet
    def randomString(alphabet: String)(n: Int): String =
      Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

    // Generate a random alphabnumeric string of length n
    def randomAlphanumericString(n: Int) =
      randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)

    val payloadString = randomAlphanumericString(testParamPayloadSize)


    val sb = new StringBuilder();

    for (i <- 1 to testParamNumPayloads) {
      sb.append("\"property").append(i).append("\":\"").append(payloadString).append(i).append("\"");
      if(i < testParamNumPayloads) {
        sb.append(",");
      }
    }

    sb.toString();
  }

  def hash(bytes: Array[Byte]) = StringHelper.bytes2Hex(MessageDigest.getInstance("SHA-1").digest(bytes))
}
