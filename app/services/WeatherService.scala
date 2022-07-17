package services

import api.{ExternalWebAPI, WeatherAPI}
import com.google.inject.ImplementedBy
import play.api.Configuration
import play.api.cache.AsyncCacheApi
import play.api.libs.json.JsValue

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[WeatherServiceImpl])
trait WeatherService {
  def getWeatherByCityName(cityName: String): Future[String]
}

@Singleton
class WeatherServiceImpl @Inject()(val config: Configuration,
                                   val cache: AsyncCacheApi,
                                   val weatherApi: WeatherAPI)(implicit exec: ExecutionContext) extends WeatherService {

  val cacheExpiry: Duration = config.get[Duration]("weatherCache.expiry")

  override def getWeatherByCityName(cityName: String): Future[String] = {
    cache.getOrElseUpdate[JsValue]("weather", cacheExpiry) {
      weatherApi.getCityWeather(cityName)
    }.map{
      response => {
        val res = response \"main" \"temp"
        res.get.toString()
      }
    }
  }
}


