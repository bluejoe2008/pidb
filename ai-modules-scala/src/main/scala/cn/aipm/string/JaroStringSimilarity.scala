package cn.aipm.string

import cn.pidb.blob.{ValueComparator}
import cn.pidb.util.Config
import info.debatty.java.stringsimilarity.{Cosine, Jaccard, JaroWinkler}

/**
  * Created by bluejoe on 2019/2/17.
  */
class JaroWinklerStringSimilarity extends ValueComparator {
  def compare(str1: Any, str2: Any): Double = {
    val jw = new JaroWinkler();
    jw.similarity(str1.asInstanceOf[String], str2.asInstanceOf[String]);
  }

  override def initialize(conf: Config): Unit = {

  }
}

class JaccardStringSimilarity extends ValueComparator {
  def compare(str1: Any, str2: Any): Double = {
    val jw = new Jaccard();
    jw.similarity(str1.asInstanceOf[String], str2.asInstanceOf[String]);
  }

  override def initialize(conf: Config): Unit = {

  }
}

class CosineStringSimilarity extends ValueComparator {
  def compare(str1: Any, str2: Any): Double = {
    val jw = new Cosine();
    jw.similarity(str1.asInstanceOf[String], str2.asInstanceOf[String]);
  }

  override def initialize(conf: Config): Unit = {

  }
}