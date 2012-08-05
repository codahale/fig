package com.codahale.fig

import annotation.tailrec
import io.Source
import java.io.{File, InputStream}

import com.codahale.jerkson.Json._

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode

/**
 * An exception class thrown when there is a configuration error.
 */
class ConfigurationException(message: String) extends Exception(message)

/**
 * A JSON-based configuration file. Java/C++ style comments (i.e., // or /\*)
 * are allowed.
 *
 * val config = new Configuration("config.json") // or an io.Source or an InputStream
 * config("rabbitmq.queue.name").as[String]
 *
 * @author coda
 */
class Configuration private(root: JsonNode) {
  case class Value(path: String, value: JsonNode) {
    /**
     * Returns the value as an instance of type A.
     */
    def as[A](implicit mf: Manifest[A]): A = parse[A](value)

    /**
     * Returns the value as an instance of type Option[A]. If the value exists,
     * Some(v: A) is returned; otherwise, None.
     */
    def asOption[A](implicit mf: Manifest[A]): Option[A] = as[Option[A]]

    /**
     * Returns the value as an instance of type A, or if the value does not
     * exist, the result of the provided function.
     */
    def or[A](default: => A)(implicit mf: Manifest[A]): A = asOption[A](mf).getOrElse(default)

    /**
     * Returns the value as an instance of type A, or if it cannot be converted,
     * throws a ConfigurationException with an information error message.
     */
    def asRequired[A](implicit mf: Manifest[A]): A = asOption[A] match {
      case Some(v) => v
      case None => throw new ConfigurationException(
        "%s property %s not found".format(mf.erasure.getSimpleName, path)
      )
    }

    /**
     * Returns the value as a instance of List[A], or if the value is not a JSON
     * array, an empty list.
     */
    def asList[A](implicit mf: Manifest[A]): List[A] = if (value.isNull) {
      Nil
    } else as[List[A]]

    /**
     * Returns the value as an instance of Map[String, A], or if the value is
     * not a simple JSON object, an empty map.
     */
    def asMap[A](implicit mf: Manifest[A]): Map[String, A] = if (value.isNull) {
      Map()
    } else as[Map[String, A]]
  }

  /**
   * Read a configuration file.
   */
  def this(filename: String) = this(parse[JsonNode](new File(filename)))
 
  /**
   * Read configuration from an input stream.
   */
  def this(stream: InputStream) = this( parse[JsonNode](stream) )

  /**
   * Read configuration from a source.a
   */
  def this(source: Source) = this( parse[JsonNode](source) )

  /**
   * Given a dot-notation JSON path (e.g., "parent.child.fieldname"), returns
   * a Value which can be converted into a specific type or Option thereof.
   */
  def apply(path: String) = Value(path, traverse(root, path.split('.').toList))

  @tailrec
  private def traverse(root: JsonNode, path: Seq[String]): JsonNode = {
    if (path.isEmpty) {
      root
    } else if (root.has(path.head)) {
      traverse(root.get(path.head), path.tail)
    } else NullNode.getInstance
  }
}
