Fig
===

*You probably don't need another data format, right?*

Fig is a very small Scala library which makes it easy to read data out of
JSON-based configuration files, with limited support for C-style line comments.
(We're not barbarians here.)


Requirements
------------

* Java SE 6
* Scala 2.8.0 or 2.8.1


Why JSON?
---------

* enough structure for most purposes
* fairly human-readable
* very well supported
* easy to use with configuration management systems like Chef or Puppet


How To Use
----------

**First**, specify Fig as a dependency:
    
    val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
    val fig = "com.codahale" %% "fig" % "1.0.7" withSources()

**Second**, write your config file:
    
    {
      // Fig will strip C-style line comments from the JSON.
      "http": {
        "port": 8080, // Also works for line endings.
        "uri": "http://example.com",
        // But you'll need to escape double-slashes if there's whitespace in
        // front of them.
        "server": "one \/\/ two",
        "numbers": [1, 2, 3],
        "resource": {
          "name": "Contacts",
          "uri": "/contacts"
        },
        "more-numbers": {
          "some": [1, 2, 3],
          "more": [4, 5, 6]
        }
      }
    }

**Third**, load your config file:
    
    import com.codahale.fig.Configuration
    
    val config = new Configuration("myapp/config.json")

You can also create a new `Configuration` instance from a `Source` or an `InputStream`.

**Fourth**, read values from it:
    
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


License
-------

Copyright (c) 2010 Coda Hale

with contributions from

* Bryan J Swift

Published under The MIT License, see LICENSE