package api

import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ExternalWebAPI @Inject()(wsClient: WSClient)(implicit exec: ExecutionContext) {

  def getRequest(url: String, queryParams: Map[String, String], headers: Map[String, String]): Future[JsValue] = {
    val wsRequest = wsClient.url(url)
    for (queryPair <- queryParams) {
      wsRequest.addQueryStringParameters(queryPair)
    }
    for (headersPair <- headers) {
      wsRequest.addHttpHeaders(headersPair)
    }
    wsRequest
      .get()
      .map {
        response => {
          if (response.status == OK) {
            response.json
          } else {
            throw new RuntimeException();
          }
        }
      }
  }

}
