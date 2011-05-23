package com.codahale.fig.tests

import com.codahale.simplespec.Spec
import com.codahale.fig.{ConfigurationException, Configuration}

object ConfigurationSpec extends Spec {
  class `A configuration file` {
    private val config = new Configuration("src/test/resources/example.json")

    def `should have specific values` = {
      config("parent.child.count").as[Int] must beEqualTo(100)
    }

    def `should have optional values` = {
      config("parent.child.count").asOption[Int] must beSome(100)
      config("parent.child.woof").asOption[Int] must beNone
    }

    def `should optionally have default values` = {
      config("parent.dingo").or("yay") must beEqualTo("yay")
    }

    def `should optionally have required values` = {
      config("parent.child.count").asRequired[Int] must beEqualTo(100)
    }

    def `should throw an informative exception when a required value is missing` = {
      config("parent.child.age").asRequired[Int] must
              throwA(new ConfigurationException("int property parent.child.age not found"))
    }

    def `should have lists of items` = {
      config("parent.child.names").asList[String] must beEqualTo(List("One", "Two", "Three"))
    }

    def `should have maps of items` = {
      config("parent.child.mapped").asMap[Int] must beEqualTo(Map("1" -> 1, "2" -> 2, "3" -> 3))
    }

    def `should have maps of complicated items` = {
      config("parent.child.doubly-mapped").asMap[List[Int]] must
              beEqualTo(Map("1" -> List(1, 2, 3),
                            "2" -> List(2, 3, 4),
                            "3" -> List(3, 4, 5)))
    }
    def `should be able to parse case classes with booleans` = {
      config("simpleCaseClass").as[SimpleCase].withBool must beTrue
    }
  }
}

case class SimpleCase(withBool: Boolean)
