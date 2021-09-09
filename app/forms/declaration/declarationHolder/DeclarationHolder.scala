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

package forms.declaration.declarationHolder

import forms.DeclarationPage
import forms.common.Eori
import forms.MappingHelper.requiredRadio
import models.DeclarationType.DeclarationType
import models.declaration.EoriSource
import models.declaration.EoriSource.UserEori
import models.viewmodels.TariffContentKey
import play.api.data.{Form, Forms, Mapping}
import play.api.data.Forms.{optional, text}
import play.api.libs.json.Json
import uk.gov.voa.play.form.ConditionalMappings.mandatoryIfEqual

case class DeclarationHolder(authorisationTypeCode: Option[String], eori: Option[Eori], eoriSource: Option[EoriSource]) {
  def id: String = s"${authorisationTypeCode.getOrElse("")}-${eori.getOrElse("")}"
  def isEmpty: Boolean = authorisationTypeCode.isEmpty && eori.isEmpty
  def isComplete: Boolean = authorisationTypeCode.isDefined && eori.isDefined

  def isAdditionalDocumentationRequired: Boolean =
    authorisationTypeCode.exists(AuthorizationTypeCodes.CodesRequiringDocumentation.contains)
}

object DeclarationHolder extends DeclarationPage {
  implicit val format = Json.format[DeclarationHolder]

  val authorisationTypeCode = "authorisationTypeCode"
  val eoriSource = "eoriSource"
  val eori = "eori"

  private def applyDeclarationHolder(userEori: String)(authorisationTypeCode: Option[String], eori: Option[Eori], eoriSource: String) = {
    val maybeEoriSource = EoriSource.lookupByValue.get(eoriSource)

    (eori, maybeEoriSource) match {
      case (None, Some(eoriSource)) if eoriSource.equals(UserEori) =>
        DeclarationHolder(authorisationTypeCode, Some(Eori(userEori)), maybeEoriSource)

      case _ =>
        DeclarationHolder(authorisationTypeCode, eori, maybeEoriSource)
    }
  }

  private def unapplyDeclarationHolder(declarationHolder: DeclarationHolder): Option[(Option[String], Option[Eori], String)] = {
    val maybeEoriSourceValue = declarationHolder.eoriSource.map(_.toString) orElse declarationHolder.eori.map(_ => EoriSource.OtherEori.toString)

    Some((declarationHolder.authorisationTypeCode, declarationHolder.eori, maybeEoriSourceValue.getOrElse("")))
  }

  def mapping(userEori: String): Mapping[DeclarationHolder] =
    Forms.mapping(
      authorisationTypeCode ->
        optional(text()).verifying("declaration.declarationHolder.authorisationCode.empty", _.isDefined),
      eori -> mandatoryIfEqual(eoriSource, EoriSource.OtherEori.toString, Eori.mapping("declaration.declarationHolder.eori.other.error.empty")),
      eoriSource -> requiredRadio("declaration.declarationHolder.eori.error.radio", EoriSource.values.map(_.toString))
    )(applyDeclarationHolder(userEori))(unapplyDeclarationHolder)

  def form(userEori: String): Form[DeclarationHolder] = Form(mapping(userEori))

  override def defineTariffContentKeys(decType: DeclarationType): Seq[TariffContentKey] =
    Seq(TariffContentKey(s"tariff.declaration.addAuthorisationRequired.${DeclarationPage.getJourneyTypeSpecialisation(decType)}"))
}

object DeclarationHolderRequired extends DeclarationPage {
  override def defineTariffContentKeys(decType: DeclarationType): Seq[TariffContentKey] =
    Seq(TariffContentKey(s"tariff.declaration.isAuthorisationRequired.${DeclarationPage.getJourneyTypeSpecialisation(decType)}"))
}

object DeclarationHolderSummary extends DeclarationPage