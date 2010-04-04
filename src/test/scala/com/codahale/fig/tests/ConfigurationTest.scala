package com.codahale.fig.tests

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import com.codahale.fig.{ConfigurationException, Configuration}

class ConfigurationTest extends Spec with MustMatchers {
  describe("a configuration file") {
    val config = new Configuration("src/test/resources/example.json")

    it("has specific values") {
      config("parent.child.count").as[Int] must equal(100)
    }

    it("has optional values") {
      config("parent.child.count").asOption[Int] must equal(Some(100))
      config("parent.child.woof").asOption[Int] must equal(None)
    }

    it("can have default values") {
      config("parent.dingo").or("yay") must equal("yay")
    }

    it("can have required values") {
      config("parent.child.count").asRequired[Int] must equal(100)
    }

    it("throws an informative error when a required value is missing") {
      val thrown = evaluating { config("parent.child.age").asRequired[Int] } must produce [ConfigurationException]
      thrown.getMessage must equal("int property parent.child.age not found")
    }

    it("has lists of items") {
      config("parent.child.names").asList[String] must equal(List("One", "Two", "Three"))
    }
  }
}
