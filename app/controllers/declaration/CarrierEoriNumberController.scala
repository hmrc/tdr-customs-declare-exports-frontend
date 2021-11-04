/*
 * Copyright 2021 HM Revenue & Customs
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

import scala.concurrent.{ExecutionContext, Future}

import controllers.actions.{AuthAction, JourneyAction}
import controllers.declaration.routes.{CarrierDetailsController, ConsigneeDetailsController}
import controllers.navigation.Navigator
import forms.common.YesNoAnswer.YesNoAnswers
import forms.declaration.carrier.{CarrierDetails, CarrierEoriNumber}
import javax.inject.Inject
import models.DeclarationType.{CLEARANCE, OCCASIONAL, SIMPLIFIED, STANDARD}
import models.requests.JourneyRequest
import models.{ExportsDeclaration, Mode}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.cache.ExportsCacheService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.declaration.carrier_eori_number

class CarrierEoriNumberController @Inject()(
  authenticate: AuthAction,
  journeyType: JourneyAction,
  navigator: Navigator,
  mcc: MessagesControllerComponents,
  carrierEoriDetailsPage: carrier_eori_number,
  override val exportsCacheService: ExportsCacheService
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with ModelCacheable with SubmissionErrors {

  val validJourneys = Seq(STANDARD, SIMPLIFIED, OCCASIONAL, CLEARANCE)

  def displayPage(mode: Mode): Action[AnyContent] = (authenticate andThen journeyType(validJourneys)) { implicit request =>
    carrierDetails match {
      case Some(data) => Ok(carrierEoriDetailsPage(mode, form.fill(CarrierEoriNumber(data))))
      case _          => Ok(carrierEoriDetailsPage(mode, form))
    }
  }

  def submit(mode: Mode): Action[AnyContent] = (authenticate andThen journeyType(validJourneys)).async { implicit request =>
    form.bindFromRequest
      .fold(
        formWithErrors => Future.successful(BadRequest(carrierEoriDetailsPage(mode, formWithErrors))),
        formData => updateCache(formData, carrierDetails).map(_ => navigator.continueTo(mode, nextPage(formData.hasEori)))
      )
  }

  private def carrierDetails(implicit request: JourneyRequest[_]): Option[CarrierDetails] =
    request.cacheModel.parties.carrierDetails

  private def form(implicit request: JourneyRequest[_]): Form[CarrierEoriNumber] =
    CarrierEoriNumber.form.withSubmissionErrors

  private def nextPage(hasEori: String): Mode => Call =
    if (hasEori == YesNoAnswers.yes) ConsigneeDetailsController.displayPage else CarrierDetailsController.displayPage

  private def updateCache(formData: CarrierEoriNumber, savedCarrierDetails: Option[CarrierDetails])(
    implicit r: JourneyRequest[AnyContent]
  ): Future[Option[ExportsDeclaration]] =
    updateExportsDeclarationSyncDirect(
      model => model.copy(parties = model.parties.copy(carrierDetails = Some(CarrierDetails.from(formData, savedCarrierDetails))))
    )
}
