/*
 * Copyright 2019 HM Revenue & Customs
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

import config.AppConfig
import controllers.actions.{AuthAction, JourneyAction}
import controllers.util.CacheIdGenerator.cacheId
import forms.declaration.TransactionType
import forms.declaration.TransactionType.{form, formId}
import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.CustomsCacheService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration.transaction_type

import scala.concurrent.{ExecutionContext, Future}

class TransactionTypeController @Inject()(
  appConfig: AppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  journeyType: JourneyAction,
  customsCacheService: CustomsCacheService
)(implicit ec: ExecutionContext)
    extends FrontendController with I18nSupport {

  def displayForm(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    customsCacheService.fetchAndGetEntry[TransactionType](cacheId, formId).map {
      case Some(data) => Ok(transaction_type(appConfig, form.fill(data)))
      case _          => Ok(transaction_type(appConfig, form))
    }
  }

  def saveTransactionType(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    form.bindFromRequest
      .fold(
        (formWithErrors: Form[TransactionType]) =>
          Future.successful(BadRequest(transaction_type(appConfig, formWithErrors))),
        form =>
          customsCacheService.cache[TransactionType](cacheId, formId, form).map { _ =>
            Redirect(controllers.declaration.routes.PreviousDocumentsController.displayForm())
        }
      )
  }
}