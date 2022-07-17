package services

import api.ExternalWebAPI
import com.fasterxml.jackson.annotation.JsonValue
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
                                   val countryAPI: ExternalWebAPI)(implicit exec: ExecutionContext) extends CountryService {

  val cacheExpiry: Duration = config.get[Duration]("countryCache.expiry")

  override def getCountryByCode(countryCode: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("countries", cacheExpiry) {
      val url = config.get[String]("countries.url")
      countryAPI.getRequest(url, Map("q" -> "value"), Map.empty)
    }.map{
      response => (response \countryCode).as[String]
    }
  }

  override def getCapitalByCode(countryCode: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("capitals", cacheExpiry) {
      val url = config.get[String]("countries.capitals.url")
      countryAPI.getRequest(url, Map.empty, Map.empty)
    }.map{
      response => (response \countryCode).as[String]
    }
  }

  override def getCurrencyByCode(countryCode: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("currency", cacheExpiry) {
      val url = config.get[String]("countries.currency.url")
      countryAPI.getRequest(url, Map.empty, Map.empty)
    }.map{
      response => (response \countryCode).as[String]
    }
  }

}


