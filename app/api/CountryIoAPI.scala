package api

import com.google.inject.ImplementedBy
import exceptions.CountryApiException
import play.api.{Configuration, Logger}
import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[OpenExchangeRatesAPI])
trait CountryAPI {
  def getCurrencies: Future[JsValue]
  def getCountries: Future[JsValue]
  def getCapitals: Future[JsValue]
}

@Singleton
class CountryIoAPI @Inject()(config: Configuration, wsClient: WSClient)(implicit exec: ExecutionContext) extends CountryAPI {

  val logger: Logger = Logger("country_api")

  override def getCurrencies: Future[JsValue] = {
    val url = config.get[String]("countries.currency.url")
    getRequest(url, Map.empty, Map.empty)
  }

  override def getCountries: Future[JsValue] = {
    val url = config.get[String]("countries.url")
    getRequest(url, Map.empty, Map.empty)
  }

  override def getCapitals: Future[JsValue] = {
    val url = config.get[String]("countries.capitals.url")
    getRequest(url, Map.empty, Map.empty)
  }

  private def getRequest(url: String, queryParams: Map[String, String], headers: Map[String, String]): Future[JsValue] = {
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
            val errorMessage = response.statusText
            logger.error(s"Country.io API get request caused error: $errorMessage")
            throw new CountryApiException(response.statusText)
          }
        }
      }
  }

}
