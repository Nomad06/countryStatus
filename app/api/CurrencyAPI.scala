package api

import com.google.inject.ImplementedBy
import exceptions.OpenExchangeRatesApiException
import play.api.{Configuration, Logger}
import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OpenExchangeRatesAPI])
trait CurrencyAPI {
  def getCurrencyRates: Future[JsValue]
}

@Singleton
class OpenExchangeRatesAPI @Inject()(wsClient: WSClient, config: Configuration)(implicit exec: ExecutionContext) extends CurrencyAPI {

  val logger: Logger = Logger("currency_api")

  def getCurrencyRates: Future[JsValue] = {
    val url = config.get[String]("openexchangerates.url")
    val api_key = config.get[String]("openexchangerates.api_key")
    wsClient.url(url)
      .addQueryStringParameters("app_id" -> api_key)
      .get()
      .map {
        response => {
          if (response.status == OK) {
            response.json
          } else {
            val errorMessage = response.statusText
            logger.error(s"OpenExchangeRates API get request caused error: $errorMessage")
            throw new OpenExchangeRatesApiException(response.statusText)
          }
        }
      }
  }

}
