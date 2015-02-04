play-prerender
==============

Library to use prerender.io with Play Framework

Installation
------------

``` scala
  val appDependencies = Seq(
    "nl.rhinofly" %% "play-prerender" % "0.4"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "Rhinofly Internal Release Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local"
  )
```

Configuration
-------------

Configured programmatically. You can use the following config settings if you use the sample code from the `Usage` section.

```
prerender.enabled = true
# Probably 'http://localhost:3000/ if you run prerender.io locally, or 'http://service.prerender.io/' if you use the hosted prerender.io service
prerender.service = "http://localhost:3000/"
# Token for prerender.io. Will be shown in X-Prerender-Token response header on the index page. Not needed if you run prerender.io server locally.
# prerender.token =
```

Usage
-----

To be fleshed out. In short, create an instance of `PrerenderActionBuilders` instance:

``` scala
  import nl.rhinofly.play.prerender.{ PrerenderActionBuilders, PrerenderConfig }

  override lazy val prerenderActionBuilders = {
    val prerenderConfigOpt = if(current.configuration.underlying.getBoolean("prerender.enabled")) {
      Some(PrerenderConfig(
        service = current.configuration.underlying.getString("prerender.service"),
        token = current.configuration.getString("prerender.token")))
    } else None

    PrerenderActionBuilders(prerenderConfigOpt)
  }
```

Then use this to create an action, for example:

``` scala
  def index = prerenderActionBuilders.async { request =>
    Assets.at("/public", "index.html")(request)
  }
```

Licence
-------
MIT License.
