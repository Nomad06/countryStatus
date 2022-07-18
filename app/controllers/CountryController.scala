package controllers

import facades.CountryFacade
import models.CountryStatus
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CountryController @Inject()(val countryFacade: CountryFacade, val cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {

  implicit val countryStatusWrites: Writes[CountryStatus] = (countryStatus: CountryStatus) => Json.obj(
    "country" -> countryStatus.country,
    "capital" -> countryStatus.capital,
    "temperature" -> countryStatus.temperature,
    "currency" -> countryStatus.currency,
    "currencyRate" -> countryStatus.currencyRate
  )

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def getCountries(country: String): Action[AnyContent] = Action.async {
      countryFacade.getCountryStatusByCode(country).map { status => {
        Ok(Json.toJson(status))
      }}
  }
}
