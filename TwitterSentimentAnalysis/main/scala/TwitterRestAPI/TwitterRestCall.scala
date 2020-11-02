package TwitterRestAPI

import TwitterAnalysis.SentimentAnalyser
import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.StaticDateTimeFormat
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.{HttpGet, HttpUriRequest}
import org.apache.http.impl.client.{DefaultHttpClient, HttpClientBuilder}
import com.github.nscala_time.time.Imports._
import org.apache.commons.httpclient.params.HttpConnectionParams
import java.io._
import java.io._
import org.apache.http.{HttpEntity, HttpResponse}
import org.apache.http.client._
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import scala.collection.mutable.StringBuilder
import scala.xml.XML
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams

import scala.io.{Codec, Source}


object TwitterRestCall {
  val consumerKey = "rorfwH0pCGcSLSjNg6M75z9xg"
  val consumerSecret = "7u73pW8uHU75BvdFPrQ99oj6TUfTRPScO4gY2kB6Xd2RqhXE9R"
  val accessToken = "1239490200654364672-lAjEGaE9OUHmDTqHS2cZ1tZl0NWasr"
  val accessTokenSecret = "a8easIKAMUE8urHYfDFE2ZXLXPbqIcvjI4rh0IgcxWRkR"

  System.setProperty("twitter4j.oauth.consumerKey", "rorfwH0pCGcSLSjNg6M75z9xg")
  System.setProperty("twitter4j.oauth.consumerSecret", "7u73pW8uHU75BvdFPrQ99oj6TUfTRPScO4gY2kB6Xd2RqhXE9R")
  System.setProperty("twitter4j.oauth.accessToken", "1239490200654364672-lAjEGaE9OUHmDTqHS2cZ1tZl0NWasr")
  System.setProperty("twitter4j.oauth.accessTokenSecret", "a8easIKAMUE8urHYfDFE2ZXLXPbqIcvjI4rh0IgcxWRkR")


  def restCall(dateTime: DateTime,filter: String, tweetCount: Int): String = {

    val consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret)
    consumer.setTokenWithSecret(accessToken, accessTokenSecret)
    val url = "https://api.twitter.com/1.1/search/tweets.json?q=" + filter + "&count=" + tweetCount + "&until=" + dateTime.toString(StaticDateTimeFormat.forPattern("yyyy-MM-dd"))
    val request = new HttpGet(url)
    consumer.sign(request)
    val client = HttpClientBuilder.create().build()
    val response = client.execute(request)
    IOUtils.toString(response.getEntity().getContent())
  }


  def getTweets(filter: String, tweetCount: Int = 30): String = {
    val currentDate= DateTime.now
    val ss = for (i <- 1 to 5) yield restCall(currentDate-i.days,filter,tweetCount)
    ss.mkString("\n")
  }

  def calculateSentiment(filter: String = "", tweetCount: Int = 30, catchlog: Boolean = true): String = {
    val ingest = new Ingest[Response]()
    implicit val codec = Codec.UTF8
    val source = Source.fromString(getTweets(filter.replaceAll(" ","%20"),tweetCount))
    val realTweet = for (tweet <- ingest(source).toSeq) yield tweet
    val processedTweet = realTweet.flatMap(_.toOption)
    val sentiment = processedTweet.map(r => r.statuses).flatten
    val totalSentiment = sentiment.par.map(s => SentimentAnalyser.detectSentimentScore(s.text,catchlog))
    val finalScore = totalSentiment.sum/totalSentiment.size
    finalScore match {
      case sentiment if sentiment <= 0.0 => "NOT_UNDERSTOOD"
      case sentiment if sentiment < 1.0 => "VERY_NEGATIVE"
      case sentiment if sentiment < 2.0 => "NEGATIVE"
      case sentiment if sentiment < 3.0 => "NEUTRAL"
      case sentiment if sentiment < 4.0 => "POSITIVE"
      case sentiment if sentiment < 5.0 => "VERY_POSITIVE"
      case sentiment if sentiment > 5.0 => "NOT_UNDERSTOOD"
    }

  }



}
