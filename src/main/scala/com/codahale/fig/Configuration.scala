package com.codahale.config

import io.Source
import java.io.File
import net.liftweb.json._

/**
 * A JSON-based configuration file. Line comments (i.e., //) are allowed.
 *
 * val config = new Configuration("config.json")
 * config("rabbitmq.queue.name").as[String]
 *
 * @author coda
 */
class Configuration(filename: String) {
  case class Value(value: JsonAST.JValue) {
    def as[A](implicit mf: Manifest[A]) = value.extract[A](DefaultFormats, mf)
    def asOption[A](implicit mf: Manifest[A]) = value.extractOpt[A](DefaultFormats, mf)
    def or[A](default: A)(implicit mf: Manifest[A]) = asOption[A](mf).getOrElse(default)
  }

  private val json = JsonParser.parse(Source.fromFile(new File(filename))
          .mkString.replaceAll("""(^//.*|[\s]+//.*)""", ""))

  /**
   * Given a dot-notation JSON path (e.g., "parent.child.fieldname"), returns
   * a Value which can be converted into a specific type or Option thereof.
   */
  def apply(path: String) = Value(path.split('.').foldLeft(json) { _ \ _ })
}
