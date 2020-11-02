package TwitterRestAPI

import TwitterRestCall.calculateSentiment

object Twitter {


  def main(args: Array[String]) {

    val filterSentiment = calculateSentiment("Corona")
    val filterName = "Stock Analysis"
    println(filterName + " " + filterSentiment)

  }

}
