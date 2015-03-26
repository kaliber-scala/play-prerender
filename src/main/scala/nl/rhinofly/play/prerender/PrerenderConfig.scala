package nl.rhinofly.play.prerender

case class PrerenderConfig(enabled: Boolean, service: String, ssl: Boolean, token: Option[String], maximumAttempts: Int = 1)
