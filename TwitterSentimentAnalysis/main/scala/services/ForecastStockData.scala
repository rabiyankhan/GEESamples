package services

import java.time.{ZoneId, ZonedDateTime}

import com.cloudera.sparkts.models.ARIMA
import com.cloudera.sparkts.{DateTimeIndex, DayFrequency, TimeSeriesRDD}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.sql.functions.to_timestamp
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}
import services.TrainStockData.getData

import scala.collection.mutable.ListBuffer

object ForecastStockData extends App{

    override def main(args: Array[String]) ={
        trainAndPredictPrice()
    }

    lazy val conf = {
        new SparkConf().setAppName("twitter-stream-sentiment").setMaster("local[2]")
    }

    //Create spark session only when required
    lazy val spark = SparkSession.builder().appName("twitter-stream-sentiment").master("local[2]").getOrCreate();

    def trainAndPredictPrice():Array[String]={

        //Combine all companies data into single frame
        import spark.implicits._

        //Create an array of actual values of stock prices of all companies
        val actualPrices = getActualPrice()

        val data = getData()
        val formattedData = data
          .flatMap{
              row =>
                  Array(
                      (row.getString(row.fieldIndex("date")), "apple", row.getString(row.fieldIndex("closingpriceApple"))),
                      (row.getString(row.fieldIndex("date")), "amazon", row.getString(row.fieldIndex("closingpriceAmazon"))),
                      (row.getString(row.fieldIndex("date")), "google", row.getString(row.fieldIndex("closingpriceGoogle"))),
                      (row.getString(row.fieldIndex("date")), "microsoft", row.getString(row.fieldIndex("closingpriceMicrosoft")))
                  )
          }.toDF("date","symbol","closingPrice")

        //Convert the dataframe into the format required by the ARIMA model
        val finalDf = formattedData
          .withColumn("timestamp",to_timestamp(formattedData("date")))
          .withColumn("price", formattedData("closingPrice").cast(DoubleType))
          .drop("date","closingPrice").sort("timestamp")
        finalDf.registerTempTable("preData")

        val minDate = finalDf.selectExpr("min(timestamp)").collect()(0).getTimestamp(0)
        val maxDate = finalDf.selectExpr("max(timestamp)").collect()(0).getTimestamp(0)
        val zone = ZoneId.systemDefault()
        val dtIndex = DateTimeIndex.uniformFromInterval(
            ZonedDateTime.of(minDate.toLocalDateTime, zone), ZonedDateTime.of(maxDate.toLocalDateTime, zone), new DayFrequency(1)
        )
        val tsRdd = TimeSeriesRDD.timeSeriesRDDFromObservations(dtIndex, finalDf, "timestamp", "symbol", "price")
        val noOfDays = 30
        val df = tsRdd.mapSeries{vector => {
            val newVec = new org.apache.spark.mllib.linalg.DenseVector(vector.toArray.map(x => if(x.equals(Double.NaN)) 0 else x))

            //Train the values
            val arimaModel = ARIMA.fitModel(1, 0, 0, newVec)

            val forecasted = arimaModel.forecast(newVec, noOfDays)

            new org.apache.spark.mllib.linalg.DenseVector(forecasted.toArray.slice(forecasted.size-(noOfDays+1), forecasted.size-1))
        }}

        val companyList:List[String] = df.collectAsTimeSeries().keys.toList
        val multipleCompanyValues = createMultipleCompanyValues(noOfDays,companyList)
        val priceList = df.collectAsTimeSeries().data.values
        val priceForecast = df.collect()
        calculateRMSE(actualPrices, priceForecast, 8)
        saveCompanyPredictionValues(multipleCompanyValues, priceList)
        val companies = df.collect().map(_._1)
        calculateAccuracy(actualPrices, priceForecast, 30)
        companies
    }

    //Get multiple rows of a single company to match the number of forecast
    def createMultipleCompanyValues[String](n: Int, l: List[String]):List[String] = {
        l flatMap {e => List.fill(n)(e) }
    }

    //Save the predicted data to CSV
    def saveCompanyPredictionValues(name:List[String], price: Array[Double]): Unit ={

        val zip = (name,price).zipped.toArray

        val schema = StructType(
            StructField("Names", StringType, false) ::
              StructField("Price", DoubleType, false) :: Nil)

        val sc = SparkContext.getOrCreate(conf)
        val sqlContext = new SQLContext(sc)
        val rdd = sc.parallelize (zip).map (x => Row(x._1, x._2.asInstanceOf[Number].doubleValue()))
        val df1 = sqlContext.createDataFrame(rdd, schema).coalesce(1).write.partitionBy("Names").format("com.databricks.spark.csv").mode(SaveMode.Overwrite).save("CompanyPredict")


    }

    def calculateRMSE(actualPrice: Array[Array[Double]],priceForecast: Array[(String, Vector)], noOfDays: Int ):Unit={

        val priceForecast1 = priceForecast.map(_._2)
        val totalErrorSquare: ListBuffer[Double] = ListBuffer()
        val accuracy: ListBuffer[Double] = ListBuffer()
        for (j <- 0 until actualPrice.length; i <- 0 until noOfDays) {
            val errorSquare = Math.pow(priceForecast1(j)(i) - actualPrice(j)(i), 2)
            println(priceForecast1(j)(i) + "\t should be \t" + actualPrice(j)(i) + "\t Error Square = " + errorSquare)
            totalErrorSquare += errorSquare

        }

        println("Root Mean Square Error: " + (Math.sqrt(totalErrorSquare.sum/(noOfDays*9))))


    }

    //Calculate Accuracy and RMSE values
    def calculateAccuracy(actualPrice: Array[Array[Double]],priceForecast: Array[(String, Vector)], noOfDays: Int ):Unit={

        val priceForecast1 = priceForecast.map(_._2)
        val accuracy: ListBuffer[Double] = ListBuffer()
        for (j <- 0 until actualPrice.length; i <- 0 until noOfDays) {

            val errorSquare1 = Math.abs(priceForecast1(j)(i) - actualPrice(j)(i))/actualPrice(j)(i)
            println(priceForecast1(j)(i) + "\t should be \t" + actualPrice(j)(i) + "\t Error Square = " + errorSquare1)

            accuracy += errorSquare1
        }
        println("Accuracy: " + (100-((accuracy.sum/(noOfDays*9)) * 1000)) + "%")
    }

    //Get the actual price for 30 days to compare with the forecasted values of all companies
    def getActualPrice(): Array[Array[Double]] = {

        val appleDf1: DataFrame = createDataFrame("AAPL_Actual")
        val apple1 = appleDf1.select(appleDf1("date").as("appleDate1"), appleDf1("close").as("closingpriceApple1"))
        apple1.show(30)
        val applePriceActual1: Array[Double] = apple1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val amazonDf1: DataFrame = createDataFrame("AMZN_Actual")
        val amazon1 = amazonDf1.select(amazonDf1("date").as("amazonDate1"), amazonDf1("close").as("closingpriceAmazon"))
        val amazonPriceActual1: Array[Double] = amazon1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val googleDf1: DataFrame = createDataFrame("GOOGL_Actual")
        val google1 = googleDf1.select(googleDf1("date").as("googleDate1"), googleDf1("close").as("closingpriceGoogle1"))
        val googlePriceActual1: Array[Double] = google1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val microsoftDf1: DataFrame = createDataFrame("MSFT_Actual")
        val microsoft1 = microsoftDf1.select(microsoftDf1("date").as("microsoftDate1"), microsoftDf1("close").as("closingpriceMicrosoft1"))
        val microsoftPriceActual1: Array[Double] = microsoft1.collect().flatMap((row: Row) => Array(try{row.getString(1).toDouble} catch {case _ : Throwable => 0.0}))

        val actualPrices =Array(applePriceActual1,amazonPriceActual1,googlePriceActual1,microsoftPriceActual1)

        actualPrices
    }

    def createDataFrame(name: String):DataFrame = {
        return spark.read.option("header", "true").csv(s"file:///Users/Rabiya/Freelancer/Atul/src/main/resources/$name.csv")
    }

}
