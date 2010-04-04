package com.codahale.config.tests

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import com.codahale.config.Configuration

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
  }
}
