package net.kaliber.play.prerender

import java.net.ConnectException
import play.api.http.Status.SERVICE_UNAVAILABLE
import play.api.libs.ws.WSClient
import play.api.mvc.{ ActionBuilder, Request, RequestHeader, Result }
import play.api.mvc.Results.Status
import scala.concurrent.{ ExecutionContext, Future }

case class PrerenderActionBuilders(config: PrerenderConfig)(implicit ec: ExecutionContext, wsClient: WSClient) extends ActionBuilder[Request] {

  private val serviceUnavailable = Future.successful(Status(SERVICE_UNAVAILABLE))

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    if (shouldBePrerendered(request)) {
      prerender(request, 1)
    } else block(request)

  private def prerender(request: RequestHeader, attempt: Int): Future[Result] = {
    val prefix = if (config.ssl) "https://" else "http://"
    val url = config.service + prefix + request.host + request.uri
    val requestHolder = config.token.map { token =>
      wsClient.url(url).withHeaders("X-Prerender-Token" -> token)
    } getOrElse
      wsClient.url(url)

    requestHolder.withHeaders("User-Agent" -> request.headers.get("User-Agent").getOrElse("NING/1.0"))
      .get.map { response =>
        val headers = response.allHeaders.toSeq.flatMap { case (key, values) => values.map { key -> _ } }
        Status(response.status)(response.body).withHeaders(headers: _*)
      } recoverWith {
        case ce: ConnectException => {
          if (attempt <= config.maximumAttempts)
            prerender(request, attempt + 1)
          else
            serviceUnavailable
        }
        case other => serviceUnavailable
      }
  }

  private def shouldBePrerendered(request: RequestHeader) =
    config.enabled && isGetRequest(request) && isSearchEngineRequest(request) &&
    hasOnlyAllowedParams(request) && !isExcludedWithRegex(request)

  private def isGetRequest(request: RequestHeader) = request.method == "GET"

  private def isSearchEngineRequest(request: RequestHeader) =
    isEscapedFragmentUrl(request) || hasSearchEngineUserAgent(request)

  private def hasOnlyAllowedParams(request: RequestHeader) = {
    val requestParams = request.queryString.keys.toSet
    requestParams subsetOf config.limitedParams.getOrElse(requestParams)
  }

  private def isExcludedWithRegex(request: RequestHeader) = {
    config.excludeRegex.getOrElse("").r.findFirstMatchIn(request.uri) match {
      case None => false
      case Some(matchingSubstring) => !matchingSubstring.toString().isEmpty
    }
  }

  private def isEscapedFragmentUrl(request: RequestHeader) =
    request.queryString.contains("_escaped_fragment_")

  private def hasSearchEngineUserAgent(request: RequestHeader) =
    request.headers.get("User-Agent").map { userAgent =>
      val userAgentLower = userAgent.toLowerCase
      if (userAgents exists (userAgentLower contains _)) true
      else false
    } getOrElse false

  private val searchUserAgents = Seq(
    "googlebot",
    "yahoo",
    "bingbot",
    "baiduspider"
  )

  private val socialUserAgents = Seq(
    "facebookexternalhit",
    "twitterbot",
    "rogerbot",
    "linkedinbot",
    "embedly"
  )

  private val adUserAgents = Seq(
    "mediapartners-google",
    "mediapartners",
    "adsbot-google"
  )

  private val userAgents = searchUserAgents ++ socialUserAgents ++ adUserAgents ++ config.additionalAgents
}
