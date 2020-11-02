{\rtf1\ansi\ansicpg1252\cocoartf2513
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 ArialMT;}
{\colortbl;\red255\green255\blue255;\red26\green26\blue26;\red255\green255\blue255;}
{\*\expandedcolortbl;;\cssrgb\c13333\c13333\c13333;\cssrgb\c100000\c100000\c100000;}
\paperw11900\paperh16840\margl1440\margr1440\vieww25400\viewh13520\viewkind0
\deftab720
\pard\pardeftab720\partightenfactor0

\f0\fs26 \cf2 \cb3 \expnd0\expndtw0\kerning0
package com.db.rcla;\cb1 \
\
\cb3 import org.apache.spark.SparkContext;\cb1 \
\cb3 import org.apache.spark.sql.Dataset;\cb1 \
\cb3 import org.apache.spark.sql.Row;\cb1 \
\cb3 import org.apache.spark.sql.SparkSession;\cb1 \
\cb3 import org.apache.spark.sql.functions;\cb1 \
\cb3 import org.apache.spark.sql.catalyst.expressions.aggregate.Collect;\cb1 \
\cb3 import org.apache.spark.sql.catalyst.expressions.aggregate.First;\cb1 \
\
\cb3 public class RCLTransformation \{\cb1 \
\cb3 	public static void main(String[] args) \{\cb1 \
\cb3 	\cb1 \
\cb3 		String master = "local[*]";\cb1 \
\cb3 		SparkSession sparkSession = SparkSession.builder().\cb1 \
\cb3 				appName(RCLTransformation.class.getName()).master(master).getOrCreate();\cb1 \
\cb3 		SparkContext context = sparkSession.sparkContext();\cb1 \
\
\cb3 		Dataset<Row> rawDataset = sparkSession.read().option("multiline","true")\cb1 \
\cb3 			.json("./src/main/resources/sampleJson.json");\cb1 \
\
\cb3 		Dataset<Row> splitData = rawDataset.select(functions.explode(rawDataset.col("data")).alias("data"));\cb1 \
\
\cb3 		Dataset<Row> splitMeasures = splitData.select(splitData.col("data.identifier"), splitData.col("data.adjustmentId"),\cb1 \
\cb3 				splitData.col("data.payload"),functions.explode(splitData.col("data.payload.measures")).alias("measures"));\cb1 \
\cb3 		\cb1 \
\cb3 		Dataset<Row> splitMeasurePts = splitMeasures.select(splitMeasures.col("identifier"), splitMeasures.col("adjustmentId"),\cb1 \
\cb3 				splitMeasures.col("measures.measureType"),\cb1 \
\cb3 				functions.explode(splitMeasures.col("measures.measurePoints")).alias("mp"));\cb1 \
\cb3 		\cb1 \
\cb3 		Dataset<Row> resultData = splitMeasurePts.select(splitMeasurePts.col("identifier"), splitMeasurePts.col("adjustmentId"),\cb1 \
\cb3 				splitMeasurePts.col("measureType"),\cb1 \
\cb3 				splitMeasurePts.col("mp.numericValue"),\cb1 \
\cb3 				splitMeasurePts.col("mp.numericValueType"),\cb1 \
\cb3 				splitMeasurePts.col("mp.numericValueCurrency"),\cb1 \
\cb3 				splitMeasurePts.col("mp.numericValueOriginalCurrency"),\cb1 \
\cb3 				splitMeasurePts.col("mp.stringValue"),\cb1 \
\cb3 				splitMeasurePts.col("mp.stringValueType"));\cb1 \
\cb3 				\cb1 \
\cb3 		Dataset<Row> pivotData = resultData.groupBy("adjustmentId").pivot("measureType").sum("numericValue");\cb1 \
\cb3 		\cb1 \
\cb3 		Dataset<Row> firstds = pivotData.select( pivotData.col("adjustmentId"), pivotData.col("BASASTSGR").alias(\'93alias1\'94), pivotData.col("EADCOLB2EPE").alias("cold_exps_pst_hrct"),\cb1 \
\cb3 				pivotData.col(\'93col1\'94).alias("ctry_rsk_utild_exps"));\cb1 \
\cb3 		\cb1 \
\cb3 		Dataset<Row> pivotCurrencyData = resultData.groupBy("adjustmentId").pivot("measureType").agg(functions.first("numericValueOriginalCurrency"));\cb1 \
\cb3 		\cb1 \
\cb3 		Dataset<Row> second = pivotCurrencyData.select(pivotCurrencyData.col("adjustmentId"),pivotCurrencyData.col(\'93col2\'94).alias("drv_orgl_notl_orgl_ccy"), pivotCurrencyData.col(\'93col3\'94).alias("cold_exps_pst_hrct_orgl_ccy"),\cb1 \
\cb3 				pivotCurrencyData.col(\'93col4\'94).alias("ctry_rsk_utild_exps_orgl_ccy"));\cb1 \
\cb3 				Dataset<Row> finalResult = firstds.join(second, firstds.col("adjustmentId").equalTo(second.col("adjustmentId")));\cb1 \
\cb3 		\cb1 \
\cb3 		finalResult.show(false);\cb1 \
\
\
\cb3 	\}\cb1 \
\cb3 	\cb1 \
\cb3 \}}