package services

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[CountryFacadeImpl])
trait CountryFacade {
  def getCountryStatusByCode(countryCode: String): Future[(String, String, String, String, String)]
}

@Singleton
class CountryFacadeImpl @Inject()(val countryService: CountryService,
                                  val weatherService: WeatherService,
                                  val currencyService: CurrencyService)(implicit exec: ExecutionContext) extends CountryFacade {

  override def getCountryStatusByCode(countryCode: String): Future[(String, String, String, String, String)] = {
    val currencyInfoFutures = for {
      curCodeFuture <- countryService.getCurrencyByCode(countryCode)
      currencyFuture <- currencyService.getExchangeRates(curCodeFuture)
      capital <- countryService.getCapitalByCode(countryCode)
      weather <- weatherService.getWeatherByCityName(capital)
      country <- countryService.getCountryByCode(countryCode)
    } yield (curCodeFuture, currencyFuture, capital, weather, country)
    currencyInfoFutures
  }

}
