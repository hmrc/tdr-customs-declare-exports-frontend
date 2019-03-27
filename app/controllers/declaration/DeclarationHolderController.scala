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
import controllers.util.{Add, FormAction, Remove, SaveAndContinue}
import forms.declaration.DeclarationHolder
import handlers.ErrorHandler
import javax.inject.Inject
import models.declaration.DeclarationHoldersData
import models.declaration.DeclarationHoldersData.{formId, limitOfHolders}
import models.requests.JourneyRequest
import play.api.data.{Form, FormError}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.CustomsCacheService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.declaration.declaration_holder

import scala.concurrent.{ExecutionContext, Future}

class DeclarationHolderController @Inject()(
  appConfig: AppConfig,
  override val messagesApi: MessagesApi,
  authenticate: AuthAction,
  journeyType: JourneyAction,
  errorHandler: ErrorHandler,
  customsCacheService: CustomsCacheService
)(implicit ec: ExecutionContext)
    extends FrontendController with I18nSupport {

  import forms.declaration.DeclarationHolder.form

  def displayForm(): Action[AnyContent] = (authenticate andThen journeyType).async { implicit request =>
    customsCacheService.fetchAndGetEntry[DeclarationHoldersData](cacheId, formId).map {
      case Some(data) => Ok(declaration_holder(appConfig, form, data.holders))
      case _          => Ok(declaration_holder(appConfig, form, Seq()))
    }
  }

  def submitHoldersOfAuthorisation(): Action[AnyContent] = (authenticate andThen journeyType).async {
    implicit request =>
      val boundForm = form.bindFromRequest()

      val actionTypeOpt = request.body.asFormUrlEncoded.map(FormAction.fromUrlEncoded(_))

      val cachedData = customsCacheService
        .fetchAndGetEntry[DeclarationHoldersData](cacheId, formId)
        .map(_.getOrElse(DeclarationHoldersData(Seq())))

      cachedData.flatMap { cache =>
        boundForm
          .fold(
            (formWithErrors: Form[DeclarationHolder]) =>
              Future.successful(BadRequest(declaration_holder(appConfig, formWithErrors, cache.holders))),
            validForm =>
              actionTypeOpt match {
                case Some(Add)             => addHolder(validForm, cache)
                case Some(SaveAndContinue) => saveAndContinue(validForm, cache)
                case Some(Remove(values))  => removeHolder(retrieveHolder(values), cache)
                case _                     => errorHandler.displayErrorPage()
            }
          )
      }
  }

  private def addHolder(
    userInput: DeclarationHolder,
    cachedData: DeclarationHoldersData
  )(implicit request: JourneyRequest[_], hc: HeaderCarrier): Future[Result] =
    (userInput, cachedData.holders) match {
      case (_, holders) if holders.length >= limitOfHolders =>
        handleErrorPage(
          Seq(("", "supplementary.declarationHolders.maximumAmount.error")),
          userInput,
          cachedData.holders
        )

      case (holder, holders) if holders.contains(holder) =>
        handleErrorPage(Seq(("", "supplementary.declarationHolders.duplicated")), userInput, cachedData.holders)

      case (holder, holders) if holder.authorisationTypeCode.isDefined && holder.eori.isDefined =>
        val updatedCache = DeclarationHoldersData(holders :+ holder)

        customsCacheService.cache[DeclarationHoldersData](cacheId, formId, updatedCache).map { _ =>
          Redirect(controllers.declaration.routes.DeclarationHolderController.displayForm())
        }

      case (DeclarationHolder(authCode, eori), _) =>
        val authCodeError = authCode.fold(
          Seq(("authorisationTypeCode", "supplementary.declarationHolder.authorisationCode.empty"))
        )(_ => Seq[(String, String)]())
        val eoriError = eori.fold(Seq(("eori", "supplementary.eori.empty")))(_ => Seq[(String, String)]())

        handleErrorPage(authCodeError ++ eoriError, userInput, cachedData.holders)
    }

  private def saveAndContinue(
    userInput: DeclarationHolder,
    cachedData: DeclarationHoldersData
  )(implicit request: JourneyRequest[_], hc: HeaderCarrier): Future[Result] =
    (userInput, cachedData.holders) match {
      case (holder, Seq()) =>
        holder match {
          case DeclarationHolder(Some(typeCode), Some(eori)) =>
            val updatedCache = DeclarationHoldersData(Seq(DeclarationHolder(Some(typeCode), Some(eori))))

            customsCacheService.cache[DeclarationHoldersData](cacheId, formId, updatedCache).map { _ =>
              Redirect(controllers.declaration.routes.DestinationCountriesController.displayForm())
            }

          case DeclarationHolder(maybeTypeCode, maybeEori) =>
            val typeCodeError = maybeTypeCode.fold(
              Seq(("authorisationTypeCode", "supplementary.declarationHolder.authorisationCode.empty"))
            )(_ => Seq[(String, String)]())

            val eoriError = maybeEori.fold(Seq(("eori", "supplementary.eori.empty")))(_ => Seq[(String, String)]())

            handleErrorPage(typeCodeError ++ eoriError, userInput, Seq())
        }

      case (holder, holders) =>
        holder match {
          case _ if holders.length >= limitOfHolders =>
            handleErrorPage(Seq(("", "supplementary.declarationHolders.maximumAmount.error")), userInput, holders)

          case _ if holders.contains(holder) =>
            handleErrorPage(Seq(("", "supplementary.declarationHolders.duplicated")), userInput, holders)

          case _ if holder.authorisationTypeCode.isDefined == holder.eori.isDefined =>
            val updatedHolders = if (holder.authorisationTypeCode.isDefined) holders :+ holder else holders
            val updatedCache = DeclarationHoldersData(updatedHolders)

            customsCacheService.cache[DeclarationHoldersData](cacheId, formId, updatedCache).map { _ =>
              Redirect(controllers.declaration.routes.DestinationCountriesController.displayForm())
            }

          case DeclarationHolder(maybeTypeCode, maybeEori) =>
            val typeCodeError = maybeTypeCode.fold(
              Seq(("authorisationTypeCode", "supplementary.declarationHolder.authorisationCode.empty"))
            )(_ => Seq[(String, String)]())

            val eoriError = maybeEori.fold(Seq(("eori", "supplementary.eori.empty")))(_ => Seq[(String, String)]())

            handleErrorPage(typeCodeError ++ eoriError, userInput, holders)
        }
    }

  private def removeHolder(
    holderToRemove: DeclarationHolder,
    cachedData: DeclarationHoldersData
  )(implicit request: JourneyRequest[_], hc: HeaderCarrier): Future[Result] =
    if (cachedData.containsHolder(holderToRemove)) {
      val updatedCache = cachedData.copy(holders = cachedData.holders.filterNot(_ == holderToRemove))

      customsCacheService.cache[DeclarationHoldersData](cacheId, formId, updatedCache).map { _ =>
        Redirect(controllers.declaration.routes.DeclarationHolderController.displayForm())
      }
    } else errorHandler.displayErrorPage()

  private def handleErrorPage(
    fieldWithError: Seq[(String, String)],
    userInput: DeclarationHolder,
    holders: Seq[DeclarationHolder]
  )(implicit request: Request[_]): Future[Result] = {
    val updatedErrors = fieldWithError.map((FormError.apply(_: String, _: String)).tupled)

    val formWithError = form.fill(userInput).copy(errors = updatedErrors)

    Future.successful(BadRequest(declaration_holder(appConfig, formWithError, holders)))
  }

  private def retrieveHolder(values: Seq[String]): DeclarationHolder =
    DeclarationHolder.buildFromString(values.headOption.getOrElse(""))
}