Fig
===

*You probably don't need another data format, right?*

Fig is a very small Scala library which makes it easy to read data out of
JSON-based configuration files, with limited support for C-style line comments.
(We're not barbarians here.)


Requirements
------------

* Java SE 6
* Scala 2.8 Beta1


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
    val fig = "com.codahale" %% "fig" % "1.0.0" withSources()

**Second**, write your config file:
    
    {
      // Fig will strip C-style line comments from the JSON.
      "http": {
        "port": 8080, // Also works for line endings.
        "uri": "http://example.com",
        // But you'll need to escape double-slashes if there's whitespace in
        // front of them.
        "server": "one \/\/ two",
        "resource": {
          "name": "Contacts",
          "uri": "/contacts"
        }
      }
    }

**Third**, load your config file:
    
    import com.codahale.fig.Configuration
    
    val config = new Configuration("myapp/config.json")

**Fourth**, read values from it:
    
    // Load a value directly.
    val port = config("http.port").as[Int]
    
    // Load a value as an option.
    val uri = config("http.uri").asOption[Int]
    
    // Load a value or a default value.
    var server = config("http.server").or("Fig-Powered")
    
    // Hell, even load a case class.
    case class ResourceConfig(name: String, uri: String)
    val resource = config("http.resource").as[ResourceConfig]

License
-------

Copyright (c) 2010 Coda Hale
Published under The MIT License, see LICENSE