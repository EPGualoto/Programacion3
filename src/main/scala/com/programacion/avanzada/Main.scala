package com.programacion.avanzada

import breeze.linalg.{max, min}
import breeze.plot.{Figure, plot}
import breeze.stats.hist
import com.programacion.avanzada.listas.Lista
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.sql.functions.{avg, col}
import org.apache.spark.sql.functions._

import scala.annotation.tailrec


object Main {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("grupal")
      .master("local[*]")
      .config("spark.driver.bindAddress", "127.0.0.1")
      .getOrCreate()

    val readCsv: DataFrame = spark.read
      .option("header", value = true)
      .option("inferSchema", value = true)
      .option("delimiter", ";")
      .csv("data/estadoCivil.csv")

    readCsv.show()
    readCsv.printSchema()

    // Renombrar la primera columna para eliminar cualquier carácter invisible si es necesario
    val cleanedDF = readCsv.withColumnRenamed("﻿I01", "I01")

    // Convertir la columna 'GEDAD' a tipo numérico (entero)
    val dfWithNumericAge = cleanedDF.withColumn("GEDAD", col("GEDAD").cast("int"))

    // Filtrar datos por una condición específica (por ejemplo, estado civil = "CASADO")
    val filteredDF = dfWithNumericAge.filter(col("P02") === "CASADO")
    filteredDF.show()

    // Agrupar y contar datos por estado civil

    val groupedByStateCivil = dfWithNumericAge.groupBy("P02").count()
    groupedByStateCivil.show()
    println("***************Agrupamiento por estado civil************")




    //Edad promedio por estado civil)
    val statsByStateCivil = dfWithNumericAge.groupBy("P02")
      .agg(
        avg("GEDAD").alias("EdadPromedio"),

      )

    statsByStateCivil.show()


    // Contar el número de registros por provincia (NOMPROV) y estado civil (P02)
    val countByProvinceAndStateCivil = dfWithNumericAge.groupBy("NOMPROV", "P02").count()
    countByProvinceAndStateCivil.show()

    // filtrar por edad mayor a 30
    val filteredByAge = dfWithNumericAge.filter(col("GEDAD") > 30)
    filteredByAge.show()

    // Crear una lista y utilizar foldLeft para contar los elementos
    //Utilizando los datos arrojados del CCV
    val lista = Lista(67,361,3048,1371,62,8,75,268,10,848)
    val count = lista.foldLeft(0)((count, _) => count + 1)
    println(s"Parentesco o relacion: $count")

    import spark.implicits._

    // Convertir el DataFrame a una lista de enteros
    val edadList = filteredByAge.select("P36")
      .as[Int]
      .collect()
      .toList
      .reverse //

    // Convertir la lista de enteros a tu clase Lista
    val lista1 = Lista(edadList: _*)
    val cuadrados = lista.map(x => x * x)
    println(s"Cuadrados de edades: $cuadrados")

    val sumaEdades = lista1.foldLeft(0)((acc, x) => acc + x)
    println(s"Suma de edades: $sumaEdades")

    val edadesConcatenadas = lista1.foldRight("")((x, acc) => s"$x, $acc").trim
    println(s"Edades concatenadas: $edadesConcatenadas")

    val primerasCinco = lista1.takeFold(5)
    println(s"Primeras 5 edades: $primerasCinco")

    // Función recursiva para calcular la suma de la lista
    @tailrec
    def sumList(lst: List[Int], acc: Int = 0): Int = lst match {
      case Nil => acc
      case head :: tail => sumList(tail, acc + head)
    }

    // Función recursiva para contar los elementos de la lista
    @tailrec
    def countList(lst: List[Int], acc: Int = 0): Int = lst match {
      case Nil => acc
      case _ :: tail => countList(tail, acc + 1)
    }

    // Calcular la suma y el promedio
    val totalSum = sumList(edadList)
    val totalCount = countList(edadList)
    val average = if (totalCount > 0) totalSum.toDouble / totalCount else 0.0

    println(s"Suma de edades Frecur: $totalSum")
    println(s"Cantidad de edades Frecur: $totalCount")
    println(s"Promedio de edades Frecur: $average")


    // Convertir solo los datos numéricos (EdadPromedio) a una lista
    val edadPromedioList: List[Double] = statsByStateCivil
      .select("EdadPromedio")        // Seleccionar solo la columna de edad promedio
      .as[Double]                     // Convertir a Dataset de Double
      .collect()                      // Convertir a Array de Double
      .toList                         // Convertir a List de Double

    // Imprimir la lista de edades promedio
    println(s"Lista de edades promedio: $edadPromedioList")


    val edadesConcatenadas1 = edadPromedioList.foldRight("")((x, acc) => s"$x, $acc").trim
    println(s"Promedio Concatenado: $edadesConcatenadas")


    // Detener la SparkSession
    spark.stop()
  }
}
