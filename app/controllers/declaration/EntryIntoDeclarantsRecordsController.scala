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
import forms.common.YesNoAnswer
import forms.common.YesNoAnswer.YesNoAnswers
import forms.declaration.EntryIntoDeclarantsRecords.form
import javax.inject.Inject
import models.DeclarationType.CLEARANCE
import models.requests.JourneyRequest
import models.{ExportsDeclaration, Mode}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.cache.ExportsCacheService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration.entry_into_declarants_records

import scala.concurrent.{ExecutionContext, Future}

class EntryIntoDeclarantsRecordsController @Inject()(
  authenticate: AuthAction,
  journeyType: JourneyAction,
  override val exportsCacheService: ExportsCacheService,
  navigator: Navigator,
  mcc: MessagesControllerComponents,
  entryIntoDeclarantsRecordsPage: entry_into_declarants_records
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with ModelCacheable {

  def displayPage(mode: Mode): Action[AnyContent] = (authenticate andThen journeyType(CLEARANCE)) { implicit request =>
    request.cacheModel.parties.isEntryIntoDeclarantsRecords match {
      case Some(data) => Ok(entryIntoDeclarantsRecordsPage(mode, form().fill(data)))
      case _          => Ok(entryIntoDeclarantsRecordsPage(mode, form()))
    }
  }

  def submitForm(mode: Mode): Action[AnyContent] = (authenticate andThen journeyType(CLEARANCE)).async { implicit request =>
    form()
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(entryIntoDeclarantsRecordsPage(mode, formWithErrors))),
        validData => updateCache(validData).map(_ => navigator.continueTo(mode, nextPage(validData)))
      )
  }

  private def updateCache(validData: YesNoAnswer)(implicit request: JourneyRequest[AnyContent]): Future[Option[ExportsDeclaration]] =
    updateExportsDeclarationSyncDirect(model => model.copy(parties = model.parties.copy(isEntryIntoDeclarantsRecords = Some(validData))))

  private def nextPage(answer: YesNoAnswer): Mode => Call =
    if (answer.answer == YesNoAnswers.yes)
      controllers.declaration.routes.PersonPresentingGoodsDetailsController.displayPage
    else
      controllers.declaration.routes.DeclarantDetailsController.displayPage

}