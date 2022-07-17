package api

import com.google.inject.ImplementedBy
import play.api.Configuration
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
            throw new RuntimeException();
          }
        }
      }
  }

}
