Fig
===

*You probably don't need another data format, right?*

Fig is a very small Scala library which makes it easy to read data out of
JSON-based configuration files, with full support for Javascript-style comments. 
(We're not barbarians here.)


Requirements
------------

* Scala 2.8.1 or 2.9.0-1
* Jerkson 0.3.1-SNAPSHOT


Why JSON?
---------

* enough structure for most purposes
* fairly human-readable
* very well supported
* easy to use with configuration management systems like Chef or Puppet


How To Use
----------

**First**, specify Fig as a dependency:

```xml
<repositories>
  <repository>
    <id>repo.codahale.com</id>
    <url>http://repo.codahale.com</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.codahale</groupId>
    <artifactId>fig_${scala.version}</artifactId>
    <version>1.1.5-SNAPSHOT</version>
  </dependency>
</dependencies>
```

**Second**, write your config file:

```javascript
{
    /* Config allows you to use C-style comments.
     * This, for example, is a block comment.
     */
    "parent": { // Comments can terminate a line, too.
        "child": {
            "url": "http://example.com",
            // We don't care about comments inside string literals.
            "splody-string": "An-a one an-a two an-a // /*wah*/ YAY",
            "count": 100,
            "names": ["One", "Two", "Three"],
            "mapped": {
                "1": 1,
                "2": 2,
                "3": 3
            },
            "doubly-mapped": {
                "1": [1, 2, 3],
                "2": [2, 3, 4],
                "3": [3, 4, 5]
            }
        }
    }
}
```

**Third**, load your config file:

```scala
import com.codahale.fig.Configuration

val config = new Configuration("myapp/config.json")
```

You can also create a new `Configuration` instance from a `Source` or an
`InputStream`.

**Fourth**, read values from it:

 ```scala
// Load a value directly.
val port = config("http.port").as[Int]

// Load a value as an option.
val uri = config("http.uri").asOption[Int]

// Load a value or a default value.
var server = config("http.server").or("Fig-Powered")

// Load a list of values.
val numbers = config("http.numbers").asList[Int]

// Or a map of values (key are required to be strings)
val properties = config("http.resource").asMap[String]

// Hell, even load a case class.
case class ResourceConfig(name: String, uri: String)
val resource = config("http.resource").as[ResourceConfig]

// Defy type erasure for lists!
val numbers = config("http.more-numbers").asMap[List[Int]]
```

License
-------

Copyright (c) 2010-2011 Coda Hale

with contributions from

* Bryan J Swift

Published under The MIT License, see LICENSE
