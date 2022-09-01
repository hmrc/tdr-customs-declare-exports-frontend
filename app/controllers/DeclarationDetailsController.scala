/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import connectors.CustomsDeclareExportsConnector
import controllers.actions.{AuthAction, VerifiedEmailAction}
import models.declaration.submissions.Submission
import models.requests.ExportsSessionKeys
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.declaration_details

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeclarationDetailsController @Inject() (
  authenticate: AuthAction,
  verifyEmail: VerifiedEmailAction,
  customsDeclareExportsConnector: CustomsDeclareExportsConnector,
  mcc: MessagesControllerComponents,
  declarationDetailsPage: declaration_details
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  def displayPage(submissionId: String): Action[AnyContent] = (authenticate andThen verifyEmail).async { implicit request =>
    customsDeclareExportsConnector.findSubmission(submissionId).map {
      case Some(submission) =>
        Ok(declarationDetailsPage(submission))
          .addingToSession(sessionKeys(submission): _*)
      case _ => Redirect(routes.SubmissionsController.displayListOfSubmissions())
    }
  }

  private def sessionKeys(submission: Submission): Seq[(String, String)] = {
    val submissionId = Some(ExportsSessionKeys.submissionId -> submission.uuid)
    val lrn = Some(ExportsSessionKeys.submissionLrn -> submission.lrn)
    val mrn = submission.mrn.map(ExportsSessionKeys.submissionMrn -> _)
    val ducr = submission.ducr.map(ExportsSessionKeys.submissionDucr -> _)

    Seq(submissionId, lrn, mrn, ducr).flatten

  }
}
