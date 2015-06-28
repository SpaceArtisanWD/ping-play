package controllers

import com.ybrikman.ping.scalaapi.compose.Compose
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits._

/**
 * This controller shows an example of composing together the results of two other controllers togheter: Wvyp and Wvyu.
 */
class Aggregator(wvyp: Wvyp, wvyu: Wvyu) extends Controller {

  def index = Action.async { request =>
    val wvypFuture = wvyp.index(embed = true)(request)
    val wvyuFuture = wvyu.index(embed = true)(request)

    for {
      wvyp <- wvypFuture
      wvyu <- wvyuFuture

      wvypBody <- Compose.readBody(wvyp)
      wvyuBody <- Compose.readBody(wvyu)
    } yield {
      Ok(views.html.aggregator.aggregator(wvypBody, wvyuBody)).withCookies(Compose.mergeCookies(wvyp, wvyu):_*)
    }
  }
}