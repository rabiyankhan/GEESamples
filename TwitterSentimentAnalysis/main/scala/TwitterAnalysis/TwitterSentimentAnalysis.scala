package TwitterAnalysis

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import SentimentAnalyser.detectSentiment
import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.StaticDateTimeFormat
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import com.github.nscala_time.time.Imports._
import org.apache.spark.SparkContext


object TwitterSentimentAnalysis {

  val consumerKey = "rorfwH0pCGcSLSjNg6M75z9xg"
  val consumerSecret = "7u73pW8uHU75BvdFPrQ99oj6TUfTRPScO4gY2kB6Xd2RqhXE9R"
  val accessToken = "1239490200654364672-lAjEGaE9OUHmDTqHS2cZ1tZl0NWasr"
  val accessTokenSecret = "a8easIKAMUE8urHYfDFE2ZXLXPbqIcvjI4rh0IgcxWRkR"


  def main(args: Array[String]): Unit = {
    val config = new SparkConf().setAppName("twitter-stream-sentiment").setMaster("local[2]")
    val sc = new SparkContext(config)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5))



    System.setProperty("twitter4j.oauth.consumerKey", "rorfwH0pCGcSLSjNg6M75z9xg")
    System.setProperty("twitter4j.oauth.consumerSecret", "7u73pW8uHU75BvdFPrQ99oj6TUfTRPScO4gY2kB6Xd2RqhXE9R")
    System.setProperty("twitter4j.oauth.accessToken", "1239490200654364672-lAjEGaE9OUHmDTqHS2cZ1tZl0NWasr")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "a8easIKAMUE8urHYfDFE2ZXLXPbqIcvjI4rh0IgcxWRkR")

    val cb = new ConfigurationBuilder
    cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey)
      .setOAuthConsumerSecret(consumerSecret)
      .setOAuthAccessToken(accessToken)
      .setOAuthAccessTokenSecret(accessTokenSecret)

    val filters: Seq[String] = Seq("MSFT","GOOG","Apple Inc stock", "MICROSOFT","GOOGLE","APPLE")
    val auth = new OAuthAuthorization(cb.build)
    val tweets = TwitterUtils.createStream(ssc, Some(auth), filters)
    tweets.print()

    tweets.foreachRDD{(rdd) =>
      rdd.map(t => {
        Map(
          "author"-> t.getUser.getScreenName,
          "date" -> t.getCreatedAt.getTime.toString,
          "message" -> t.getText,
          "sentiment" -> detectSentiment(t.getText).toString
        )
      }).saveAsTextFile("tweets.json")
    }

    ssc.start()
    ssc.awaitTermination()
  }


  def getFromKeywordSingleDay(i: DateTime,k: String, count: Int): String = {
    val consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret)
    consumer.setTokenWithSecret(accessToken, accessTokenSecret)
    val url = "https://api.twitter.com/1.1/search/tweets.json?q=" + k + "&count=" + count + "&until=" + i.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd"))
    //println(url)
    val request = new HttpGet(url)
    consumer.sign(request)
    val client = HttpClientBuilder.create().build()
    val response = client.execute(request)
    IOUtils.toString(response.getEntity().getContent())
  }


  def getFromKeyword(k: String, count: Int = 90): String = {
    val today= DateTime.now
    //println(today.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd")))
    val ss = for (i <- 1 to 7) yield getFromKeywordSingleDay(today-i.days,k,count)
    ss.mkString("\n")
  }


}
