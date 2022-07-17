package controllers

import akka.actor.ActorSystem
import models.CountryStatus
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables
import play.api.mvc.{AbstractController, AnyContent, BaseController, ControllerComponents, Request}
import services.CountryFacade

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryController @Inject()(val countryFacade: CountryFacade, val cc: ControllerComponents)(implicit exec: ExecutionContext) extends AbstractController(cc) {


  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def getCountries() = Action.async {
      countryFacade.getCountryStatusByCode("RU").map { msg => {
        val countryStatus = CountryStatus(msg._1, msg._2, msg._3, msg._4, msg._5)
        Ok(Json.toJson(countryStatus.toString()))
      }}
  }
}
