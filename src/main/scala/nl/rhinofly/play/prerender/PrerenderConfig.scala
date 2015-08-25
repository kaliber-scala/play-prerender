package nl.rhinofly.play.prerender

case class PrerenderConfig(
  enabled: Boolean,
  service: String,
  ssl: Boolean,
  token: Option[String] = None,
  maximumAttempts: Int = 1,
  additionalAgents: Seq[String]
)
