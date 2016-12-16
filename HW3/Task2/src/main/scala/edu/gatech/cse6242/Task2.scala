package edu.gatech.cse6242

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object Task2 {
  def main(args: Array[String]) 
  {
    	val sc = new SparkContext(new SparkConf().setAppName("Task2"))
    
    // read the file
    	val file = sc.textFile("hdfs://localhost:8020" + args(0))
	
	/* TODO: Needs to be implemented */
	val line = file.map(_.split("\t").map(_.toInt))
	val mapper_filter = line.filter(a => a(2) != 1)
	val source = mapper_filter.map(a=>(a(0),-1.0*a(2)))
	val target = mapper_filter.map(a=>(a(1),1.0*a(2)))	
	val fuse = source.union(target)
	val reduce = fuse.reduceByKey(_+_)
	val output = reduce.map( a=>(a._1+"\t"+a._2) )
										 	
	// store output on given HDFS path.
    // YOU NEED TO CHANGE THIS
    	output.saveAsTextFile("hdfs://localhost:8020"+args(1))
  }
}
