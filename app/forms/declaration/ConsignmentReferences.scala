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

package forms.declaration

import forms._
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType.AdditionalDeclarationType
import models.DeclarationType.{CLEARANCE, DeclarationType, SUPPLEMENTARY}
import models.viewmodels.TariffContentKey
import play.api.data.Forms.{mapping, optional, text}
import play.api.data.{Form, FormError}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import utils.validators.forms.FieldValidator._

import scala.concurrent.{ExecutionContext, Future}

case class ConsignmentReferences(ducr: Ducr, lrn: Option[Lrn], mrn: Option[Mrn] = None, eidrDateStamp: Option[String] = None)

object ConsignmentReferences extends DeclarationPage {

  implicit val format = Json.format[ConsignmentReferences]

  val ducrId = "ducr.ducr"

  def form(decType: DeclarationType, additionalDecType: Option[AdditionalDeclarationType]): Form[ConsignmentReferences] = {

    def form2Model: (Ducr, Lrn, Option[Mrn], Option[String]) => ConsignmentReferences = { case (ducr, lrn, mrn, eidrDateStamp) =>
      ConsignmentReferences(ducr, Some(lrn), mrn, eidrDateStamp)
    }

    def model2Form: ConsignmentReferences => Option[(Ducr, Lrn, Option[Mrn], Option[String])] = model =>
      model.lrn.map(lrn => (model.ducr, lrn, model.mrn, model.eidrDateStamp))

    val mrnMapping = (decType, additionalDecType) match {
      case (SUPPLEMENTARY, Some(AdditionalDeclarationType.SUPPLEMENTARY_SIMPLIFIED)) =>
        optional(Mrn.mapping("declaration.consignmentReferences.supplementary.mrn"))
          .verifying("declaration.consignmentReferences.supplementary.mrn.error.empty", isPresent(_))
      case _ =>
        optional(text())
          .verifying("error.notRequired", isMissing(_))
          .transform(_.map(Mrn(_)), (o: Option[Mrn]) => o.map(_.value))
    }

    val eidrMapping = (decType, additionalDecType) match {
      case (SUPPLEMENTARY, Some(AdditionalDeclarationType.SUPPLEMENTARY_EIDR)) =>
        optional(
          text()
            .verifying("declaration.consignmentReferences.supplementary.eidr.error.empty", nonEmpty)
            .verifying("declaration.consignmentReferences.supplementary.eidr.error.invalid", isEmpty or (isNumeric and hasSpecificLength(8)))
        ).verifying("declaration.consignmentReferences.supplementary.eidr.error.empty", isPresent(_))
      case _ =>
        optional(text())
          .verifying("error.notRequired", isMissing(_))
    }

    Form(
      mapping(
        "ducr" -> Ducr.mapping,
        "lrn" -> Lrn.mapping("declaration.consignmentReferences.lrn").verifying(),
        "mrn" -> mrnMapping,
        "eidrDateStamp" -> eidrMapping
      )(form2Model)(model2Form)
    )
  }

  implicit class ConsignmentReferencesFormEnhanced(form: Form[ConsignmentReferences]) {

    def verifyLrnValidity(lrnValidator: LrnValidator)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Form[ConsignmentReferences]] =
      form.value.flatMap(_.lrn).fold(Future.successful(form)) { lrn =>
        lrnValidator.hasBeenSubmittedInThePast48Hours(lrn).map {
          case true  => form.copy(errors = Seq(FormError("lrn", "declaration.consignmentReferences.lrn.error.notExpiredYet")))
          case false => form
        }
      }
  }

  override def defineTariffContentKeys(decType: DeclarationType): Seq[TariffContentKey] =
    decType match {
      case CLEARANCE =>
        Seq(
          TariffContentKey("tariff.declaration.consignmentReferences.1.clearance"),
          TariffContentKey("tariff.declaration.consignmentReferences.2.clearance"),
          TariffContentKey("tariff.declaration.consignmentReferences.3.clearance")
        )
      case SUPPLEMENTARY =>
        Seq(
          TariffContentKey("tariff.declaration.consignmentReferences.1.supplementary"),
          TariffContentKey("tariff.declaration.consignmentReferences.1.common"),
          TariffContentKey("tariff.declaration.consignmentReferences.2.common"),
          TariffContentKey("tariff.declaration.consignmentReferences.3.common")
        )
      case _ =>
        Seq(
          TariffContentKey("tariff.declaration.consignmentReferences.1.common"),
          TariffContentKey("tariff.declaration.consignmentReferences.2.common"),
          TariffContentKey("tariff.declaration.consignmentReferences.3.common")
        )
    }
}
