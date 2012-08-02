package com.codahale.fig.tests

import com.codahale.simplespec.Spec
import org.junit.Test
import com.codahale.fig.{ConfigurationException, Configuration}

class ConfigurationSpec extends Spec {
  class `A configuration file` {
    val config = new Configuration("src/test/resources/example.json")

    @Test def `has specific values` = {
      config("parent.child.count").as[Int].must(be(100))
    }

    @Test def `has optional values` = {
      config("parent.child.count").asOption[Int].must(be(Some(100)))
      config("parent.child.woof").asOption[Int].must(be(None))
    }

    @Test def `optionally has default values` = {
      config("parent.dingo").or("yay").must(be("yay"))
    }

    @Test def `optionally has required values` = {
      config("parent.child.count").asRequired[Int].must(be(100))
    }

    @Test def `throws an informative exception when a required value is missing` = {
      evaluating {
        config("parent.child.age").asRequired[Int]
      }.must(throwA[ConfigurationException]("int property parent.child.age not found"))
    }

    @Test def `has lists of items` = {
      config("parent.child.names").asList[String].must(be(List("One", "Two", "Three")))
    }

    @Test def `has maps of items` = {
      config("parent.child.mapped").asMap[Int].must(be(Map("1" -> 1, "2" -> 2, "3" -> 3)))
    }

    @Test def `has maps of complicated items` = {
      config("parent.child.doubly-mapped").asMap[List[Int]].must(be(
        Map("1" -> List(1, 2, 3),
            "2" -> List(2, 3, 4),
            "3" -> List(3, 4, 5))
      ))
    }

    @Test def `is recursive` = {
      config("parent")("child")("count").as[Int].must(be(100))
      config("parent.child")("count").as[Int].must(be(100))
      config("parent")("child.count").as[Int].must(be(100))
      config("parent.child.count").as[Int].must(be(100))
    }
  }
}
