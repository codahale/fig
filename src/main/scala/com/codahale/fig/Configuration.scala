package com.codahale.fig

import io.Source
import java.io.File
import net.liftweb.json._

/**
 * An exception class thrown when there is a configuration error.
 */
class ConfigurationException(message: String) extends Exception(message)

/**
 * A JSON-based configuration file. Line comments (i.e., //) are allowed.
 *
 * val config = new Configuration("config.json")
 * config("rabbitmq.queue.name").as[String]
 *
 * @author coda
 */
class Configuration(filename: String) {
  case class Value(path: String, value: JsonAST.JValue) {
    def as[A](implicit mf: Manifest[A]) = value.extract[A](DefaultFormats, mf)
    def asOption[A](implicit mf: Manifest[A]) = value.extractOpt[A](DefaultFormats, mf)
    def or[A](default: => A)(implicit mf: Manifest[A]) = asOption[A](mf).getOrElse(default)
    def asRequired[A](implicit mf: Manifest[A]) = asOption[A] match {
      case Some(v) => v
      case None => throw new ConfigurationException(
        "%s property %s not found".format(mf.erasure.getSimpleName, path)
      )
    }
  }

  private val json = JsonParser.parse(Source.fromFile(new File(filename))
          .mkString.replaceAll("""(^//.*|[\s]+//.*)""", ""))

  /**
   * Given a dot-notation JSON path (e.g., "parent.child.fieldname"), returns
   * a Value which can be converted into a specific type or Option thereof.
   */
  def apply(path: String) = Value(path, path.split('.').foldLeft(json) { _ \ _ })
}
