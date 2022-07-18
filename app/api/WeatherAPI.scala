package api

import com.google.inject.ImplementedBy
import exceptions.OpenWeatherApiException
import play.api.{Configuration, Logger}
import play.api.http.Status.OK
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OpenWeatherAPI])
trait WeatherAPI {
  def getCityWeather(cityName: String): Future[JsValue]
}

@Singleton
class OpenWeatherAPI @Inject()(val wsClient: WSClient,
                               val config: Configuration)(implicit exec: ExecutionContext) extends WeatherAPI {

  val logger: Logger = Logger("open_weather_api")

  override def getCityWeather(cityName: String): Future[JsValue] = {
    val url = config.get[String]("openweathermap.url")
    val api_key = config.get[String]("openweathermap.api_key")
    wsClient.url(url)
      .addQueryStringParameters("appid" -> api_key)
      .addQueryStringParameters("q" -> cityName)
      .get()
      .map {
        response => {
          if (response.status == OK) {
            response.json
          } else {
            val errorMessage = response.statusText
            logger.error(s"OpenWeatherAPI get request caused error: $errorMessage")
            throw new OpenWeatherApiException(response.statusText)
          }
        }
      }
  }

}
