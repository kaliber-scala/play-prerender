package nl.rhinofly.play.prerender

import play.api.mvc.ActionBuilder
import play.api.mvc.Request
import scala.concurrent.Future
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import play.api.libs.ws.WSClient
import play.api.mvc.RequestHeader
import play.api.mvc.Results.Status

case class PrerenderActionBuilders(config: Option[PrerenderConfig])(implicit ec: ExecutionContext, wsClient: WSClient) extends ActionBuilder[Request] {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    if(shouldBePrerendered(request)) {
      prerender(request)
    } else
      block(request).map(addPrerenderToken)

  private def addPrerenderToken(result: Result) = config.flatMap { _.token.map { token =>
    result.withHeaders("X-Prerender-Token" -> token)
  }} getOrElse result

  private def prerender(request: RequestHeader): Future[Result] = {
    val requestHolder = config.flatMap { config => config.token.map { token =>
      val prefix = if(config.ssl) "https://" else "http://"
      val url = config.service + prefix + request.host + request.uri + request.rawQueryString
        
      wsClient.url(url).withHeaders("X-Prerender-Token" -> token)
    }} getOrElse {
      val url = config.get.service + "http://" + request.host + request.uri + request.rawQueryString
      wsClient.url(url)
    }

    requestHolder.get.map { response =>
      val headers = response.allHeaders.toSeq.flatMap { case (key, values) => values.map { key -> _ } }
      Status(response.status)(response.body).withHeaders(headers:_*)
    }
  }

  private def shouldBePrerendered(request: RequestHeader) =
    config.isDefined && isGetRequest(request) && isSearchEngineRequest(request)

  private def isGetRequest(request: RequestHeader) = request.method == "GET"

  private def isSearchEngineRequest(request: RequestHeader) =
    isEscapedFragmentUrl(request) || hasSearchEngineUserAgent(request)

  private def isEscapedFragmentUrl(request: RequestHeader) =
    request.queryString.contains("_escaped_fragment_")

  private def hasSearchEngineUserAgent(request: RequestHeader) =
    request.headers.get("User-Agent").map { userAgent =>
      val userAgentLower = userAgent.toLowerCase
      if(userAgentStrings exists (userAgentLower contains _)) true
      else false
    } getOrElse false


  private val userAgentStrings = Seq(
      "googlebot", 
      "yahoo", 
      "bingbot", 
      "baiduspider", 
      "facebookexternalhit", 
      "twitterbot", 
      "rogerbot", 
      "linkedinbot", 
      "embedly", 
      "developer.google.com",
      "mediapartners-google",
      "mediapartners",
      "adsbot-google")


}
