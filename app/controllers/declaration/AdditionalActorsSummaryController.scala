/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.navigation.Navigator
import forms.NoneOfTheAbove
import forms.common.YesNoAnswer
import forms.common.YesNoAnswer.YesNoAnswers
import forms.declaration.DeclarationAdditionalActors
import forms.declaration.DeclarationAdditionalActors.form
import javax.inject.Inject
import models.{DeclarationType, Mode}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.cache.ExportsCacheService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration.additionalActors.additional_actors_summary

import scala.concurrent.ExecutionContext

class AdditionalActorsSummaryController @Inject()(
  authenticate: AuthAction,
  journeyType: JourneyAction,
  override val exportsCacheService: ExportsCacheService,
  navigator: Navigator,
  mcc: MessagesControllerComponents,
  additionalActorsPage: additional_actors_summary
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with ModelCacheable with SubmissionErrors {

  val validTypes =
    Seq(DeclarationType.STANDARD, DeclarationType.SUPPLEMENTARY, DeclarationType.SIMPLIFIED, DeclarationType.OCCASIONAL)

  def displayPage(mode: Mode): Action[AnyContent] = (authenticate andThen journeyType(validTypes)) { implicit request =>
    val frm = form().withSubmissionErrors()
    request.cacheModel.parties.declarationAdditionalActorsData match {
      case Some(data) if data.actors.nonEmpty =>
        Ok(additionalActorsPage(mode, frm.fill(DeclarationAdditionalActors(None, Some(NoneOfTheAbove.value))), data.actors))
      case _ => navigator.continueTo(mode, routes.AdditionalActorsAddController.displayPage)
    }
  }

  def submitForm(mode: Mode): Action[AnyContent] = (authenticate andThen journeyType) { implicit request =>
    val actors = request.cacheModel.parties.declarationAdditionalActorsData.map(_.actors).getOrElse(Seq.empty)
    YesNoAnswer
      .form()
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[YesNoAnswer]) => BadRequest(additionalActorsPage(mode, formWithErrors, actors)),
        validYesNo =>
          validYesNo.answer match {
            case YesNoAnswers.yes => navigator.continueTo(mode, controllers.declaration.routes.AdditionalActorsAddController.displayPage)
            case YesNoAnswers.no  => navigator.continueTo(mode, routes.DeclarationHolderController.displayPage)
        }
      )
  }
}