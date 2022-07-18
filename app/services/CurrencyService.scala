package services

import api.CurrencyAPI
import com.google.inject.ImplementedBy
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CurrencyServiceImpl])
trait CurrencyService {
  def getExchangeRates(currencyISO: String): Future[Float]
}

class CurrencyServiceImpl @Inject()(val config: Configuration,
                                    val cache: AsyncCacheApi,
                                    val wsClient: WSClient,
                                    val currencyAPI: CurrencyAPI)(implicit exec: ExecutionContext) extends CurrencyService {

  val cacheExpiry: Duration = config.get[Duration]("currencyCache.expiry")

  override def getExchangeRates(currencyISO: String): Future[Float] = {
    cache.getOrElseUpdate[JsValue]("currencyRate", cacheExpiry) {
      currencyAPI.getCurrencyRates
    }.map{
      response => {
        val res = response \"rates" \currencyISO
        res.as[Float]
      }
    }


  }

}
