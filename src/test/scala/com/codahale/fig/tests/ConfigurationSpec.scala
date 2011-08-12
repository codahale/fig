package com.codahale.fig.tests

import com.codahale.simplespec.Spec
import com.codahale.simplespec.annotation.test
import com.codahale.fig.{ConfigurationException, Configuration}

class ConfigurationSpec extends Spec {
  class `A configuration file` {
    val config = new Configuration("src/test/resources/example.json")

    @test def `has specific values` = {
      config("parent.child.count").as[Int] must beEqualTo(100)
    }

    @test def `has optional values` = {
      config("parent.child.count").asOption[Int] must beSome(100)
      config("parent.child.woof").asOption[Int] must beNone
    }

    @test def `optionally has default values` = {
      config("parent.dingo").or("yay") must beEqualTo("yay")
    }

    @test def `optionally has required values` = {
      config("parent.child.count").asRequired[Int] must beEqualTo(100)
    }

    @test def `throws an informative exception when a required value is missing` = {
      config("parent.child.age").asRequired[Int] must
              throwA(new ConfigurationException("int property parent.child.age not found"))
    }

    @test def `has lists of items` = {
      config("parent.child.names").asList[String] must beEqualTo(List("One", "Two", "Three"))
    }

    @test def `has maps of items` = {
      config("parent.child.mapped").asMap[Int] must beEqualTo(Map("1" -> 1, "2" -> 2, "3" -> 3))
    }

    @test def `has maps of complicated items` = {
      config("parent.child.doubly-mapped").asMap[List[Int]] must
              beEqualTo(Map("1" -> List(1, 2, 3),
                            "2" -> List(2, 3, 4),
                            "3" -> List(3, 4, 5)))
    }
  }
}
