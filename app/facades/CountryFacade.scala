package facades

import com.google.inject.ImplementedBy
import models.CountryStatus
import services.{CountryService, CurrencyService, WeatherService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@ImplementedBy(classOf[CountryFacadeImpl])
trait CountryFacade {
  def getCountryStatusByCode(countryCode: String): Future[CountryStatus]
}

@Singleton
class CountryFacadeImpl @Inject()(val countryService: CountryService,
                                  val weatherService: WeatherService,
                                  val currencyService: CurrencyService)(implicit exec: ExecutionContext) extends CountryFacade {

  override def getCountryStatusByCode(countryCode: String): Future[CountryStatus] = {
    val countryStatusFutures = for {
      country <- countryService.getCountryByCode(countryCode)
      capital <- countryService.getCapitalByCode(countryCode)
      temperature <- weatherService.getTemperatureByCityName(capital)
      currency <- countryService.getCurrencyByCode(countryCode)
      currencyRates <- currencyService.getExchangeRates(currency)
    } yield (country, capital, temperature, currency, currencyRates)

    countryStatusFutures.map(tuple => CountryStatus(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5))
  }

}
