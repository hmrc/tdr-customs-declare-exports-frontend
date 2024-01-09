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
import controllers.declaration.routes.PreviousDocumentsSummaryController
import controllers.navigation.Navigator
import forms.declaration.NatureOfTransaction
import forms.declaration.NatureOfTransaction._
import models.requests.JourneyRequest
import models.ExportsDeclaration
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.audit.AuditService
import services.cache.ExportsCacheService
import uk.gov.hmrc.play.bootstrap.controller.WithUnsafeDefaultFormBinding
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.declaration.nature_of_transaction

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NatureOfTransactionController @Inject() (
  authenticate: AuthAction,
  journeyType: JourneyAction,
  navigator: Navigator,
  mcc: MessagesControllerComponents,
  natureOfTransactionPage: nature_of_transaction,
  override val exportsCacheService: ExportsCacheService
)(implicit ec: ExecutionContext, auditService: AuditService)
    extends FrontendController(mcc) with I18nSupport with ModelCacheable with SubmissionErrors with WithUnsafeDefaultFormBinding {

  def displayPage: Action[AnyContent] = (authenticate andThen journeyType) { implicit request =>
    val frm = form.withSubmissionErrors
    request.cacheModel.natureOfTransaction match {
      case Some(data) => Ok(natureOfTransactionPage(frm.fill(data)))
      case _          => Ok(natureOfTransactionPage(frm))
    }
  }

  def saveTransactionType(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(natureOfTransactionPage(formWithErrors))),
        updateCache(_).map(_ => navigator.continueTo(PreviousDocumentsSummaryController.displayPage))
      )
  }

  private def updateCache(formData: NatureOfTransaction)(implicit r: JourneyRequest[AnyContent]): Future[ExportsDeclaration] =
    updateDeclarationFromRequest(_.copy(natureOfTransaction = Some(formData)))
}
