/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.declaration

import controllers.actions.{AuthAction, JourneyAction}
import controllers.declaration.routes.TransportCountryController
import controllers.navigation.Navigator
import forms.declaration.BorderTransport
import models.DeclarationType._
import models.ExportsDeclaration
import models.requests.JourneyRequest
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TransportCodeService
import services.cache.ExportsCacheService
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.declaration.border_transport

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BorderTransportController @Inject() (
  authenticate: AuthAction,
  journeyType: JourneyAction,
  navigator: Navigator,
  override val exportsCacheService: ExportsCacheService,
  mcc: MessagesControllerComponents,
  implicit val transportCodeService: TransportCodeService,
  borderTransport: border_transport
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with ModelCacheable with SubmissionErrors with WithUnsafeDefaultFormBinding {

  private val validTypes = Seq(STANDARD, OCCASIONAL, SUPPLEMENTARY, SIMPLIFIED)

  def displayPage: Action[AnyContent] = (authenticate andThen journeyType(validTypes)) { implicit request =>
    val transport = request.cacheModel.transport
    val form = (transport.meansOfTransportCrossingTheBorderType, transport.meansOfTransportCrossingTheBorderIDNumber) match {
      case (Some(meansType), Some(meansId)) =>
        BorderTransport.form.withSubmissionErrors.fill(BorderTransport(meansType, meansId))

      case (None, None) =>
        BorderTransport.form.withSubmissionErrors.fill(BorderTransport("", ""))

      case _ => BorderTransport.form.withSubmissionErrors
    }

    Ok(borderTransport(form))
  }

  def submitForm: Action[AnyContent] = (authenticate andThen journeyType(validTypes)).async { implicit request =>
    BorderTransport.form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(borderTransport(formWithErrors))),
        updateCache(_).map(_ => navigator.continueTo(TransportCountryController.displayPage))
      )
  }

  private def updateCache(borderTransport: BorderTransport)(implicit r: JourneyRequest[AnyContent]): Future[ExportsDeclaration] =
    updateDeclarationFromRequest(_.updateBorderTransport(borderTransport))
}
