package services

import api.CountryIoAPI
import com.google.inject.ImplementedBy
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.api.libs.json.JsValue

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[CountryServiceImpl])
trait CountryService {
  def getCountryByCode(countryCode: String): Future[String]

  def getCapitalByCode(countryCode: String): Future[String]

  def getCurrencyByCode(countryCode: String): Future[String]
}

@Singleton
class CountryServiceImpl @Inject()(val config: Configuration,
                                   cache: AsyncCacheApi,
                                   val countryAPI: CountryIoAPI)(implicit exec: ExecutionContext) extends CountryService {

  val cacheExpiry: Duration = config.get[Duration]("countryCache.expiry")

  override def getCountryByCode(countryCode: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("countries", cacheExpiry) {
      countryAPI.getCountries
    }.map{
      response => (response \countryCode).as[String]
    }
  }

  override def getCapitalByCode(countryCode: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("capitals", cacheExpiry) {
      countryAPI.getCapitals
    }.map{
      response => (response \countryCode).as[String]
    }
  }

  override def getCurrencyByCode(countryCode: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("currency", cacheExpiry) {
      countryAPI.getCurrencies
    }.map{
      response => (response \countryCode).as[String]
    }
  }

}


