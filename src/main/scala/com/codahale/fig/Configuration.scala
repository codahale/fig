package com.codahale.fig

import annotation.tailrec
import io.Source
import java.io.{File, InputStream}
import com.codahale.jerkson.Json._
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.node.NullNode

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
class Configuration private(path: Seq[String], root: JsonNode) {
  /**
   * Returns the root as an instance of type A.
   */
  def as[A](implicit mf: Manifest[A]): A = parse[A](root)

  /**
   * Returns the root as an instance of type Option[A]. If the root exists,
   * Some(v: A) is returned; otherwise, None.
   */
  def asOption[A](implicit mf: Manifest[A]): Option[A] = as[Option[A]]

  /**
   * Returns the root as an instance of type A, or if the root does not
   * exist, the result of the provided function.
   */
  def or[A](default: => A)(implicit mf: Manifest[A]): A = asOption[A](mf).getOrElse(default)

  /**
   * Returns the root as an instance of type A, or if it cannot be converted,
   * throws a ConfigurationException with an information error message.
   */
  def asRequired[A](implicit mf: Manifest[A]): A = asOption[A] match {
    case Some(v) => v
    case None => throw new ConfigurationException("%s property %s not found".format(
      mf.erasure.getSimpleName,
      if (path.isEmpty) "[root]" else path mkString "."
    ))
  }

  /**
   * Returns the root as a instance of List[A], or if the root is not a JSON
   * array, an empty list.
   */
  def asList[A](implicit mf: Manifest[A]): List[A] = if (root.isNull) {
    Nil
  } else as[List[A]]

  /**
   * Returns the root as an instance of Map[String, A], or if the root is
   * not a simple JSON object, an empty map.
   */
  def asMap[A](implicit mf: Manifest[A]): Map[String, A] = if (root.isNull) {
    Map()
  } else as[Map[String, A]]

  /**
   * Read a configuration file.
   */
  def this(filename: String) = this(Seq(), parse[JsonNode](new File(filename)))

  /**
   * Read configuration from an input stream.
   */
  def this(stream: InputStream) = this(Seq(), parse[JsonNode](stream))

  /**
   * Read configuration from a source.a
   */
  def this(source: Source) = this(Seq(), parse[JsonNode](source))

  /**
   * Given a dot-notation JSON path (e.g., "parent.child.fieldname"), returns
   * a Configuration which can be converted into a specific type or Option thereof.
   */
  def apply(path: String) = {
    val pathList = path.split('.').toList
    new Configuration(pathList, traverse(root, pathList))
  }

  @tailrec
  private def traverse(root: JsonNode, path: Seq[String]): JsonNode = {
    if (path.isEmpty) {
      root
    } else if (root.has(path.head)) {
      traverse(root.get(path.head), path.tail)
    } else NullNode.getInstance
  }
}
