# play-prerender

Library to use prerender.io with Play Framework

## Installation

``` scala
  val appDependencies = Seq(
    "net.kaliber" %% "play-prerender" % "0.10"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "Kaliber Release Repository" at "https://jars.kaliber.io/artifactory/libs-release-local"
  )
```

## Configuration
-------------

Configured programmatically. You can use the following config settings if you use the sample code from the `Usage` section.

```
prerender.enabled = true
# Probably 'http://localhost:3000/ if you run prerender.io locally, or 'http://service.prerender.io/' if you use the hosted prerender.io service
prerender.service = "http://localhost:3000/"
# Token for prerender.io. Not needed if you run prerender.io server locally.
# prerender.token =
# Maximum attempts done in the case Prerender fails (default: 1)
prerender.maximumAttempts = 2
# If you require a user agent which is not present in the default list it can be added through through this configuration option.
prerender.additionalAgents = []
```

### Default user agents

If you feel like a useragent should be into the default list please create a new issue with a statement to why you feel it should be added to the list.

```
 "googlebot",
 "yahoo",
 "bingbot",
 "baiduspider"
 "facebookexternalhit",
 "twitterbot",
 "rogerbot",
 "linkedinbot",
 "embedly",
 "mediapartners-google",
 "mediapartners",
 "adsbot-google"
```



## Usage

To be fleshed out. In short, create an instance of `PrerenderActionBuilders` instance:

``` scala
  import nl.rhinofly.play.prerender.{ PrerenderActionBuilders, PrerenderConfig }

  override lazy val prerenderActionBuilders = {
    val config = PrerenderConfig(
      enabled = current.configuration.getBoolean("prerender.enabled").getOrElse(false),
      service = current.configuration.getString("prerender.service").getOrElse(""),
      ssl = current.configuration.getBoolean("prerender.ssl").getOrElse(false),
      token = current.configuration.getString("prerender.token"),
      maximumAttempts = current.configuration.getInt("prerender.maximumAttempts").getOrElse(1))

    PrerenderActionBuilders(config)
  }
```

Then use this to create an action, for example:

``` scala
  def index = prerenderActionBuilders.async { request =>
    Assets.at("/public", "index.html")(request)
  }
```

## Licence
MIT License.
