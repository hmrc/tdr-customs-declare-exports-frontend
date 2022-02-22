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

package views.helpers

import config.AppConfig
import forms.common.YesNoAnswer.{No, Yes}
import forms.declaration.AuthorisationProcedureCodeChoice.{Choice1007, Choice1040, ChoiceOthers}
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType._

import javax.inject.{Inject, Singleton}
import models.DeclarationType._
import models.ExportsDeclaration
import models.declaration.Parties
import models.requests.JourneyRequest
import play.api.i18n.Messages
import play.api.mvc.Call
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukInsetText, GovukWarningText}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.insettext.InsetText
import uk.gov.hmrc.govukfrontend.views.viewmodels.warningtext.WarningText
import views.helpers.DeclarationHolderHelper.{bodyId, insetTextId, valuesToMatch, warningBodyId}
import views.html.components.gds.{bulletList, link, numberedList, paragraphBody}

@Singleton
class DeclarationHolderHelper @Inject()(
  bulletList: bulletList,
  govukInsetText: GovukInsetText,
  govukWarningText: GovukWarningText,
  link: link,
  numberedList: numberedList,
  paragraphBody: paragraphBody
) {

  def bodyForDeclarationHolderEditPage(appConfig: AppConfig)(implicit messages: Messages, request: JourneyRequest[_]): Option[Html] = {
    val messageList = valuesToMatch(request.cacheModel) match {
      case (SUPPLEMENTARY, _, _, _)                               => listOfMessages("body.supplementary")
      case (SIMPLIFIED, Some(SIMPLIFIED_FRONTIER), Choice1007, _) => messagesWithLinkFor1007(appConfig, "simplified.arrived")
      case (SIMPLIFIED, _, _, _)                                  => listOfMessages("body.simplified")
      case (CLEARANCE, _, Choice1040, Yes)                        => listOfMessages("body.clearance.eidr.1040")
      case (CLEARANCE, _, ChoiceOthers, Yes)                      => listOfMessages("body.clearance.eidr.others")
      case (CLEARANCE, _, Choice1007, Yes)                        => messagesWithLinkFor1007(appConfig, "clearance.eidr")
      case _                                                      => List.empty
    }

    if (messageList.isEmpty) None
    else bodyText(messageList, bodyId)
  }

  private val bodyKey = "declaration.declarationHolderRequired.body"

  def bodyForDeclarationHolderRequiredPage(implicit messages: Messages, request: JourneyRequest[_]): Html = {
    val model = request.cacheModel
    val body = (model.`type`, model.additionalDeclarationType, model.parties.authorisationProcedureCodeChoice) match {
      case (STANDARD, Some(STANDARD_PRE_LODGED), Choice1040)   => List(paragraph(s"$bodyKey.standard.prelodged.1040"))
      case (STANDARD, Some(STANDARD_PRE_LODGED), ChoiceOthers) => List(paragraph(s"$bodyKey.standard.prelodged.others"))

      case (OCCASIONAL, Some(OCCASIONAL_PRE_LODGED), _) =>
        List(paragraph(s"$bodyKey.occasional.1"), paragraph(s"$bodyKey.occasional.2"))

      case (OCCASIONAL, Some(OCCASIONAL_FRONTIER), _) =>
        val bullets = bulletList(List(row(s"$bodyKey.occasional.bullet.1"), row(s"$bodyKey.occasional.bullet.2")))
        List(paragraph(s"$bodyKey.occasional.1"), paragraph(s"$bodyKey.occasional.2"), bullets)

      case _ => List(paragraph(s"$bodyKey.default"))
    }

    HtmlFormat.fill(body)
  }

  case class arrivedComponents(warning: Option[Html], paragraph: Option[Html])

  def componentsForArrived(implicit messages: Messages, request: JourneyRequest[_]): Option[arrivedComponents] = {
    if(isArrived(request.cacheModel.additionalDeclarationType))
      Some(arrivedComponents(warningTextForArrivedDeclarations, bodyText(listOfMessages("body.arrived.paragraph"), warningBodyId)))
    else None
  }

  def hintForAuthorisationCode(implicit messages: Messages, request: JourneyRequest[_]): List[String] =
    valuesToMatch(request.cacheModel) match {
      case (STANDARD, Some(STANDARD_PRE_LODGED), Choice1007, _)   => listOfMessages("authCode.hint.standard.prelodged.1007")
      case (STANDARD, Some(STANDARD_PRE_LODGED), ChoiceOthers, _) => listOfMessages("authCode.hint.standard.prelodged.others")
      case (STANDARD, Some(STANDARD_PRE_LODGED), Choice1040, _)   => listOfMessages("authCode.hint.standard.1040")
      case (CLEARANCE, Some(CLEARANCE_PRE_LODGED), _, No)         => listOfMessages("authCode.hint.clearance")
      case _                                                      => List.empty
    }

  def insetTextBelowAuthorisationCode(appConfig: AppConfig)(implicit messages: Messages, request: JourneyRequest[_]): Option[Html] =
    valuesToMatch(request.cacheModel) match {
      case (STANDARD, _, Choice1007, _)                             => insetTextForExciseRemovals(appConfig)
      case (STANDARD | SIMPLIFIED, _, ChoiceOthers, _)              => insetTextForNonStandardProcedures(appConfig)
      case (SIMPLIFIED, Some(SIMPLIFIED_PRE_LODGED), Choice1007, _) => insetTextForExciseRemovals(appConfig)
      case (CLEARANCE, _, ChoiceOthers, Yes)                        => insetTextForNonStandardProcedures(appConfig)
      case _                                                        => None
    }

  def textForEoriRadiosWhenEXRR(implicit messages: Messages, request: JourneyRequest[_]): Html = {
    val model = request.cacheModel
    model.additionalDeclarationType match {
      case Some(STANDARD_FRONTIER) | Some(SIMPLIFIED_FRONTIER) | Some(OCCASIONAL_FRONTIER) | Some(CLEARANCE_FRONTIER) =>
        paragraphForEoriRadiosWhenEXRR(model.parties)

      case _ => HtmlFormat.empty
    }
  }

  private val insetKey = "declaration.declarationHolder.authCode.inset"

  private def insetText(appendable: Html, key: String)(implicit messages: Messages): Option[Html] = {
    val html = new Html(List(paragraphBody(messages(s"$insetKey.$key.title"), "govuk-label--s"), appendable))
    Some(govukInsetText(InsetText(id = Some(insetTextId), content = HtmlContent(html))))
  }

  private def bodyText(messageList: List[String], id: String): Option[Html] =
    Some(HtmlFormat.fill(messageList.map(message => paragraphBody(message, s"govuk-body", Some(id)))))

  private def insetTextForExciseRemovals(appConfig: AppConfig)(implicit messages: Messages): Option[Html] = {
    val call1 = Call("GET", appConfig.permanentExportOrDispatch.authHolder)
    val link1 = link(messages(s"$insetKey.excise.bullet1.link"), call1, "_blank")

    val call2 = Call("GET", appConfig.permanentExportOrDispatch.conditions)
    val link2 = link(messages(s"$insetKey.excise.bullet2.link"), call2, "_blank")

    val call3 = Call("GET", appConfig.permanentExportOrDispatch.documents)
    val link3 = link(messages(s"$insetKey.excise.bullet3.link"), call3, "_blank")

    insetText(
      bulletList(
        List(
          Html(messages(s"$insetKey.excise.bullet1", link1)),
          Html(messages(s"$insetKey.excise.bullet2", link2)),
          Html(messages(s"$insetKey.excise.bullet3", link3))
        )
      ),
      "excise"
    )
  }

  private def insetTextForNonStandardProcedures(appConfig: AppConfig)(implicit messages: Messages): Option[Html] = {
    val call1 = Call("GET", appConfig.previousProcedureCodes)
    val link1 = link(messages(s"$insetKey.special.bullet1.link"), call1, "_blank")

    insetText(
      numberedList(
        List(
          Html(messages(s"$insetKey.special.bullet1", link1)),
          Html(messages(s"$insetKey.special.bullet2")),
          Html(messages(s"$insetKey.special.bullet3")),
          Html(messages(s"$insetKey.special.bullet4"))
        )
      ),
      "special"
    )
  }

  private def warningTextForArrivedDeclarations()(implicit messages: Messages): Option[Html] =
    Some(
      govukWarningText(
        WarningText(iconFallbackText = messages("site.warning"), content = Text(messages("declaration.declarationHolder.body.arrived.warning")))
      )
    )

  private def listOfMessages(key: String)(implicit messages: Messages): List[String] =
    List(messages(s"declaration.declarationHolder.$key"))

  private def messagesWithLinkFor1007(appConfig: AppConfig, key: String)(implicit messages: Messages): List[String] =
    List(
      messages(
        s"declaration.declarationHolder.body.$key.1007",
        link(messages("declaration.declarationHolder.body.1007.link"), Call("GET", appConfig.permanentExportOrDispatch.section), "_blank")
      )
    )

  private def paragraphForEoriRadiosWhenEXRR(parties: Parties)(implicit messages: Messages): Html =
    parties.declarantIsExporter.fold {
      paragraph(s"$eoriKey.body.exrr.v2", Some("EXRR-help"))
    } { declarantIsExporter =>
      if (declarantIsExporter.isExporter) paragraph(s"$eoriKey.body.exrr.v1", Some("EXRR-help"))
      else {
        val version = if (parties.exporterDetails.flatMap(_.details.eori).isDefined) "v2" else "v3"
        paragraph(s"$eoriKey.body.exrr.$version", Some("EXRR-help"))
      }
    }

  private def paragraph(key: String, id: Option[String] = None)(implicit messages: Messages): Html = paragraphBody(message = messages(key), id = id)

  private def row(key: String)(implicit messages: Messages): Html = Html(messages(key))

  private val eoriKey = "declaration.declarationHolder.eori"
}

object DeclarationHolderHelper {

  def valuesToMatch(model: ExportsDeclaration) =
    (model.`type`, model.additionalDeclarationType, model.parties.authorisationProcedureCodeChoice, model.parties.isEntryIntoDeclarantsRecords)

  val bodyId = "text-under-h1"
  val warningBodyId = "text-for-frontier"
  val insetTextId = "inset-text"
}
