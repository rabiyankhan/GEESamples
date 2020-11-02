package services

import ForecastStockData.spark
import org.apache.spark.sql.{DataFrame}

object TrainStockData {



    //Get the data of all companies in a dataframe
    def getData(): DataFrame = {
        //val spark = SparkSession.builder().appName("Stock-prediction").master("local[*]").getOrCreate();
        //Create dataframe for all company csv and convert to the required format with different date and close price columns
        import spark.implicits._
        val appleDf: DataFrame = createDataFrame("AAPL_trainingset")
        val apple = appleDf.select(appleDf("date").as("appleDate"), appleDf("close").as("closingpriceApple"))

        val amazonDf: DataFrame = createDataFrame("AMZN_trainingset")
        val amazon = amazonDf.select(amazonDf("date").as("amazonDate"), amazonDf("close").as("closingpriceAmazon"))

        val googleDf: DataFrame = createDataFrame("GOOGL_trainingset")
        val google = googleDf.select(googleDf("date").as("googleDate"), googleDf("close").as("closingpriceGoogle"))

        val microsoftDf: DataFrame = createDataFrame("MSFT_trainingset")
        val microsoft = microsoftDf.select(microsoftDf("date").as("microsoftDate"), microsoftDf("close").as("closingpriceMicrosoft"))

        //Join all the different dataframes and convert into a single frame
        val data = apple
            .join(amazon, $"appleDate" === $"amazonDate").select($"appleDate", $"closingpriceApple", $"closingpriceAmazon")
            .join(google, $"appleDate" === $"googleDate").select($"appleDate", $"closingpriceApple", $"closingpriceAmazon", $"closingpriceGoogle")
            .join(microsoft, $"appleDate" === $"microsoftDate").select($"appleDate".as("date"), $"closingpriceApple", $"closingpriceAmazon", $"closingpriceGoogle", $"closingpriceMicrosoft")

        data
    }


    //To create dataframe based on CSV
    def createDataFrame(name: String):DataFrame = {
        return spark.read.option("header", "true").csv(s"file:///Users/Rabiya/Freelancer/Atul/src/main/resources/$name.csv")
    }

}
