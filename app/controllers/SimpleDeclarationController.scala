/*
 * Copyright 2018 HM Revenue & Customs
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

import config.AppConfig
import connectors.CustomsDeclarationsConnector
import controllers.actions.AuthAction
import forms.{SimpleDeclarationForm, SimpleDeclarationFormProvider}
import javax.inject.Inject
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.wco.dec.{Declaration, GoodsShipment, MetaData, Ucr}
import views.html.simpleDeclaration

import scala.concurrent.Future

class SimpleDeclarationController @Inject()(appConfig: AppConfig,
                                            authenticate: AuthAction,
                                            formProvider: SimpleDeclarationFormProvider,
                                            customsDeclarationsConnector: CustomsDeclarationsConnector)
                                           (implicit val messagesApi: MessagesApi)
  extends FrontendController with I18nSupport {

  val form: Form[SimpleDeclarationForm] = formProvider()

  def displayForm(): Action[AnyContent] = authenticate.async { implicit request =>
    Future.successful(Ok(simpleDeclaration(appConfig, form)))
  }

  def onSubmit(): Action[AnyContent] = authenticate.async { implicit request =>
    implicit val signedInUser = request.user

    form.bindFromRequest().fold(
      (formWithErrors: Form[_]) =>
        Future.successful(BadRequest(simpleDeclaration(appConfig, formWithErrors))),
      form => {
        customsDeclarationsConnector.submitExportDeclaration(createMetadataDeclaration(form)).flatMap{
          resp =>
            resp.status match  {
              case ACCEPTED => Future.successful(Ok("Declaration has been submitted successfully."))
              case _ => Logger.error(s"Error from Customs declarations api ${resp.toString}")
                Future.successful(Ok("Declaration Submission unsuccessful."))
          }
        }
      }
    )
  }

  private def createMetadataDeclaration(form:SimpleDeclarationForm) : MetaData =
    MetaData(
      declaration = Declaration(goodsShipment = Some(GoodsShipment(ucr = Some(Ucr(traderAssignedReferenceId = Some("1234"))))))
    )
}

