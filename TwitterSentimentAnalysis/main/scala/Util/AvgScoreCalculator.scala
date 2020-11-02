package Util

import twitter4j._

import scala.util._
import services.Response
import Util.Ingest
import java.util.Properties

import TwitterAnalysis.{SentimentAnalyser, TwitterSentimentAnalysis}

import scala.io.{Codec, Source}

object AvgScoreCalculator {

  System.setProperty("twitter4j.oauth.consumerKey", TwitterSentimentAnalysis.consumerKey)
  System.setProperty("twitter4j.oauth.consumerSecret", TwitterSentimentAnalysis.consumerSecret)
  System.setProperty("twitter4j.oauth.accessToken", TwitterSentimentAnalysis.accessToken)
  System.setProperty("twitter4j.oauth.accessTokenSecret", TwitterSentimentAnalysis.accessTokenSecret)

  def calcSentiment(k: String = "", count: Int = 90, catchlog: Boolean = true): Double = {
    val ingester = new Ingest[Response]()
    implicit val codec = Codec.UTF8
    val source = Source.fromString(TwitterSentimentAnalysis.getFromKeyword(k.replaceAll(" ","%20"),count))
    val realtimetweet = for (tweet <- ingester(source).toSeq) yield tweet
    val processed = realtimetweet.flatMap(_.toOption)
    val totalsentiment = processed.map(r => r.statuses)
    val finaltweet = totalsentiment.flatten
    val sumofsentiment = finaltweet.par.map(s => SentimentAnalyser.detectSentimentScore(s.text,catchlog))
    val score = sumofsentiment.sum/sumofsentiment.size
    score
  }

}
