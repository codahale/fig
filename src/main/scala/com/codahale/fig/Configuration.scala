package com.codahale.fig

import io.Source
import java.io.File
import net.liftweb.json._
import net.liftweb.json.JsonAST._

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
    /**
     * Returns the value as an instance of type A.
     */
    def as[A](implicit mf: Manifest[A]) = value.extract[A](DefaultFormats, mf)

    /**
     * Returns the value as an instance of type Option[A]. If the value exists,
     * Some(v: A) is returned; otherwise, None.
     */
    def asOption[A](implicit mf: Manifest[A]) = value.extractOpt[A](DefaultFormats, mf)

    /**
     * Returns the value as an instance of type A, or if the value does not
     * exist, the result of the provided function.
     */
    def or[A](default: => A)(implicit mf: Manifest[A]) = asOption[A](mf).getOrElse(default)

    /**
     * Returns the value as an instance of type A, or if it cannot be converted,
     * throws a ConfigurationException with an information error message.
     */
    def asRequired[A](implicit mf: Manifest[A]) = asOption[A] match {
      case Some(v) => v
      case None => throw new ConfigurationException(
        "%s property %s not found".format(mf.erasure.getSimpleName, path)
      )
    }

    /**
     * Returns the value as a instance of List[A], or if the value is not a JSON
     * array, an empty list.
     */
    def asList[A](implicit mf: Manifest[A]) = value match {
      case JField(_, JArray(list)) => list.map { _.extract[A](DefaultFormats, mf) }
      case other => List()
    }

    /**
     * Returns the value as an instance of Map[String, A], or if the value is
     * not a simple JSON object, an empty map.
     */
    def asMap[A](implicit mf: Manifest[A]) = value match {
      case JField(_, o: JObject) =>
        o.obj.map { f => f.name -> f.value.extract[A](DefaultFormats, mf) }.toMap
      case other => Map()
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
