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

package forms.declaration

import base.ExportsTestData._
import base.{JourneyTypeTestRunner, TestHelper}
import forms.common.DeclarationPageBaseSpec
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType.{AdditionalDeclarationType, SUPPLEMENTARY_EIDR, SUPPLEMENTARY_SIMPLIFIED}
import forms.{Ducr, Lrn, LrnValidator}
import models.DeclarationType.{CLEARANCE, OCCASIONAL, SIMPLIFIED, STANDARD, SUPPLEMENTARY}
import models.requests.JourneyRequest
import models.viewmodels.TariffContentKey
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import play.api.libs.json.{JsObject, JsString, JsValue}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsignmentReferencesSpec extends DeclarationPageBaseSpec with JourneyTypeTestRunner with ScalaFutures {

  import ConsignmentReferencesSpec._

  private def getBoundedForm(data: JsValue, additionalDeclarationType: Option[AdditionalDeclarationType] = None)(
    implicit request: JourneyRequest[_]
  ): Form[ConsignmentReferences] =
    ConsignmentReferences.form(request.declarationType, additionalDeclarationType).bind(data, Form.FromJsonMaxChars)

  "ConsignmentReferences mapping used for binding data" should {
    "return form without errors" when {

      onJourney(STANDARD, SIMPLIFIED, OCCASIONAL, CLEARANCE) { implicit request =>
        s"provided with valid input for ${request.declarationType}" in {
          val form = getBoundedForm(correctConsignmentReferencesJSON)

          form.hasErrors mustBe false
        }
      }

      onJourney(SUPPLEMENTARY) { implicit request =>
        "with additionalDeclarationType of SUPPLEMENTARY_SIMPLIFIED" when {
          "provided with valid input for SUPPLEMENTARY with additionalDecType of SUPPLEMENTARY_SIMPLIFIED" in {

            val data = addMrnToJSON(correctConsignmentReferencesJSON, mrn)
            val form = getBoundedForm(data, Some(SUPPLEMENTARY_SIMPLIFIED))

            form.hasErrors mustBe false
          }
        }

        "with additionalDeclarationType of SUPPLEMENTARY_EIDR" when {
          "provided with valid input for SUPPLEMENTARY with additionalDecType of SUPPLEMENTARY_EIDR" in {

            val data = addEidrToJSON(correctConsignmentReferencesJSON, eidrDateStamp)
            val form = getBoundedForm(data, Some(SUPPLEMENTARY_EIDR))

            form.hasErrors mustBe false
          }
        }
      }
    }

    "return form with errors" when {

      onJourney(STANDARD, SIMPLIFIED, OCCASIONAL, CLEARANCE) { implicit request =>
        s"provided with empty input for ${request.declarationType}" in {
          val form = getBoundedForm(emptyJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 2
          form.errors(0).key mustBe "ducr.ducr"
          form.errors(0).message mustBe "error.required"
          form.errors(1).key mustBe "lrn"
          form.errors(1).message mustBe "error.required"
        }
      }

      onJourney(SUPPLEMENTARY) { implicit request =>
        "provided with empty input for SUPPLEMENTARY with additionalDecType of SUPPLEMENTARY_SIMPLIFIED" in {
          val form = getBoundedForm(emptyJSON, Some(SUPPLEMENTARY_SIMPLIFIED))

          form.hasErrors mustBe true
          form.errors.length mustBe 3
          form.errors(0).key mustBe "ducr.ducr"
          form.errors(0).message mustBe "error.required"
          form.errors(1).message mustBe "error.required"
          form.errors(2).message mustBe "declaration.consignmentReferences.supplementary.mrn.error.empty"
        }

        "provided with empty input for SUPPLEMENTARY with additionalDecType of SUPPLEMENTARY_EIDR" in {
          val form = getBoundedForm(emptyJSON, Some(SUPPLEMENTARY_EIDR))

          form.hasErrors mustBe true
          form.errors.length mustBe 3
          form.errors(0).key mustBe "ducr.ducr"
          form.errors(0).message mustBe "error.required"
          form.errors(1).message mustBe "error.required"
          form.errors(2).message mustBe "declaration.consignmentReferences.supplementary.eidr.error.empty"
        }
      }

      onJourney(STANDARD, SUPPLEMENTARY, SIMPLIFIED, OCCASIONAL, CLEARANCE) { implicit request =>
        "provided with invalid input (no DUCR)" in {
          val form = getBoundedForm(consignmentReferencesNoDucrJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "ducr.ducr"
          form.errors.head.message mustBe "declaration.consignmentReferences.ducr.error.empty"
        }

        "provided with valid input (lowercase DUCR)" in {
          val form = getBoundedForm(correctConsignmentReferencesLowercaseDucrJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "ducr.ducr"
          form.errors.head.message mustBe "declaration.consignmentReferences.ducr.error.invalid"
        }

        "provided with invalid input (no LRN)" in {
          val form = getBoundedForm(consignmentReferencesNoLrnJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "lrn"
          form.errors.head.message mustBe "declaration.consignmentReferences.lrn.error.empty"
        }

        "provided with invalid input (invalid chars in LRN)" in {
          val form = getBoundedForm(consignmentReferencesBadLrnJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "lrn"
          form.errors.head.message mustBe "declaration.consignmentReferences.lrn.error.specialCharacter"
        }

        "provided with invalid input (LRN too long)" in {
          val form = getBoundedForm(consignmentReferencesLrnTooLongJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "lrn"
          form.errors.head.message mustBe "declaration.consignmentReferences.lrn.error.length"
        }

        "provided with invalid input (LRN invalid chars and too long) only show invalid char error" in {
          val form = getBoundedForm(consignmentReferencesLrnBadAndTooLongJSON)

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "lrn"
          form.errors.head.message mustBe "declaration.consignmentReferences.lrn.error.specialCharacter"
        }
      }

      onJourney(STANDARD, SIMPLIFIED, OCCASIONAL, CLEARANCE) { implicit request =>
        "provided with invalid input (MRN present when should not be)" in {
          val form = getBoundedForm(addMrnToJSON(correctConsignmentReferencesJSON, mrn))

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "mrn"
          form.errors.head.message mustBe "error.notRequired"
        }

        "provided with invalid input (EIDR date stamp present when should not be)" in {
          val form = getBoundedForm(addEidrToJSON(correctConsignmentReferencesJSON, eidrDateStamp))

          form.hasErrors mustBe true
          form.errors.length mustBe 1
          form.errors.head.key mustBe "eidrDateStamp"
          form.errors.head.message mustBe "error.notRequired"
        }
      }

      onJourney(SUPPLEMENTARY) { implicit request =>
        "with additionalDeclarationType of SUPPLEMENTARY_SIMPLIFIED" when {
          "provided with invalid input (no MRN)" in {
            val form = getBoundedForm(correctConsignmentReferencesJSON, Some(SUPPLEMENTARY_SIMPLIFIED))

            form.hasErrors mustBe true
            form.errors.length mustBe 1
            form.errors.head.key mustBe "mrn"
            form.errors.head.message mustBe "declaration.consignmentReferences.supplementary.mrn.error.empty"
          }

          "provided with invalid input (MRN too long)" in {
            val form = getBoundedForm(
              addMrnToJSON(correctConsignmentReferencesJSON, TestHelper.createRandomAlphanumericString(19)),
              Some(SUPPLEMENTARY_SIMPLIFIED)
            )

            form.hasErrors mustBe true
            form.errors.length mustBe 1
            form.errors.head.key mustBe "mrn"
            form.errors.head.message mustBe "declaration.consignmentReferences.supplementary.mrn.error.invalid"
          }

          "provided with invalid input (EIDR Date Stamp present when should not be)" in {
            val form =
              getBoundedForm(addEidrToJSON(addMrnToJSON(correctConsignmentReferencesJSON, mrn), eidrDateStamp), Some(SUPPLEMENTARY_SIMPLIFIED))

            form.hasErrors mustBe true
            form.errors.length mustBe 1
            form.errors.head.key mustBe "eidrDateStamp"
            form.errors.head.message mustBe "error.notRequired"
          }
        }

        "with additionalDeclarationType of SUPPLEMENTARY_EIDR" when {
          "provided with invalid input (no EIDR Date Stamp)" in {
            val form = getBoundedForm(correctConsignmentReferencesJSON, Some(SUPPLEMENTARY_EIDR))

            form.hasErrors mustBe true
            form.errors.length mustBe 1
            form.errors.head.key mustBe "eidrDateStamp"
            form.errors.head.message mustBe "declaration.consignmentReferences.supplementary.eidr.error.empty"
          }

          "provided with invalid input (EIDR Date Stamp too long)" in {
            val form =
              getBoundedForm(addEidrToJSON(correctConsignmentReferencesJSON, TestHelper.createRandomNumericString(9)), Some(SUPPLEMENTARY_EIDR))

            form.hasErrors mustBe true
            form.errors.length mustBe 1
            form.errors.head.key mustBe "eidrDateStamp"
            form.errors.head.message mustBe "declaration.consignmentReferences.supplementary.eidr.error.invalid"
          }

          "provided with invalid input (MRN present when should not be)" in {
            val form = getBoundedForm(addEidrToJSON(addMrnToJSON(correctConsignmentReferencesJSON, mrn), eidrDateStamp), Some(SUPPLEMENTARY_EIDR))

            form.hasErrors mustBe true
            form.errors.length mustBe 1
            form.errors.head.key mustBe "mrn"
            form.errors.head.message mustBe "error.notRequired"
          }
        }
      }
    }
  }

  "ConsignmentReferencesForm on verifyLrnValidity" should {

    import forms.declaration.ConsignmentReferences.ConsignmentReferencesFormEnhanced

    implicit val hc: HeaderCarrier = HeaderCarrier()

    onEveryDeclarationJourney() { implicit request =>
      "return form without errors" when {

        "LrnValidator returns false" in {

          val lrnValidator = mock[LrnValidator]
          when(lrnValidator.hasBeenSubmittedInThePast48Hours(any[Lrn])(any(), any())).thenReturn(Future.successful(false))

          val form = getBoundedForm(correctConsignmentReferencesJSON)

          val result = form.verifyLrnValidity(lrnValidator).futureValue

          result.hasErrors mustBe false
        }
      }

      "return form with errors" when {

        "LrnValidator returns true" in {

          val lrnValidator = mock[LrnValidator]
          when(lrnValidator.hasBeenSubmittedInThePast48Hours(any[Lrn])(any(), any())).thenReturn(Future.successful(true))

          val form = getBoundedForm(correctConsignmentReferencesJSON)

          val result = form.verifyLrnValidity(lrnValidator).futureValue

          result.hasErrors mustBe true
          result.errors.length mustBe 1
          result.errors.head.key mustBe "lrn"
          result.errors.head.message mustBe "declaration.consignmentReferences.lrn.error.notExpiredYet"
        }
      }
    }
  }

  "ConsignmentReferences" when {
    testTariffContentKeys(ConsignmentReferences, "tariff.declaration.consignmentReferences")
  }

  override def getCommonTariffKeys(messageKey: String): Seq[TariffContentKey] =
    Seq(TariffContentKey(s"${messageKey}.1.common"), TariffContentKey(s"${messageKey}.2.common"), TariffContentKey(s"${messageKey}.3.common"))

  override def getClearanceTariffKeys(messageKey: String): Seq[TariffContentKey] =
    Seq(
      TariffContentKey(s"${messageKey}.1.clearance"),
      TariffContentKey(s"${messageKey}.2.clearance"),
      TariffContentKey(s"${messageKey}.3.clearance")
    )

  override def getSupplementaryTariffKeys(messageKey: String): Seq[TariffContentKey] =
    Seq(
      TariffContentKey(s"${messageKey}.1.supplementary"),
      TariffContentKey(s"${messageKey}.1.common"),
      TariffContentKey(s"${messageKey}.2.common"),
      TariffContentKey(s"${messageKey}.3.common")
    )
}

object ConsignmentReferencesSpec {

  val emptyJSON: JsValue = JsObject(Map("" -> JsString("")))

  val correctConsignmentReferences = ConsignmentReferences(ducr = Ducr(ducr = ducr), lrn = Lrn(lrn))
  val correctConsignmentReferencesNoDucr = ConsignmentReferences(ducr = Ducr(""), lrn = Lrn(lrn))
  val emptyConsignmentReferences = ConsignmentReferences(ducr = Ducr(""), lrn = Lrn(""))

  def addMrnToJSON(data: JsValue, mrn: String): JsValue =
    data.asInstanceOf[JsObject].deepMerge(JsObject(Map("mrn" -> JsString(mrn))))

  def addEidrToJSON(data: JsValue, eidrDateStamp: String): JsValue =
    data.asInstanceOf[JsObject].deepMerge(JsObject(Map("eidrDateStamp" -> JsString(eidrDateStamp))))

  val correctConsignmentReferencesJSON: JsValue = JsObject(Map("ducr" -> JsObject(Map("ducr" -> JsString(ducr))), "lrn" -> JsString(lrn)))

  val correctConsignmentReferencesLowercaseDucrJSON: JsValue = JsObject(
    Map("ducr" -> JsObject(Map("ducr" -> JsString(ducr.toLowerCase))), "lrn" -> JsString(lrn))
  )
  val consignmentReferencesNoDucrJSON: JsValue = JsObject(Map("ducr" -> JsObject(Map("ducr" -> JsString(""))), "lrn" -> JsString(lrn)))
  val consignmentReferencesNoLrnJSON: JsValue = JsObject(Map("ducr" -> JsObject(Map("ducr" -> JsString(ducr))), "lrn" -> JsString("")))
  val consignmentReferencesLrnTooLongJSON: JsValue = JsObject(
    Map("ducr" -> JsObject(Map("ducr" -> JsString(ducr))), "lrn" -> JsString("12345678901234567890123"))
  )
  val consignmentReferencesBadLrnJSON: JsValue = JsObject(Map("ducr" -> JsObject(Map("ducr" -> JsString(ducr))), "lrn" -> JsString(s"${lrn}*")))
  val consignmentReferencesLrnBadAndTooLongJSON: JsValue = JsObject(
    Map("ducr" -> JsObject(Map("ducr" -> JsString(ducr))), "lrn" -> JsString("1234567890123456789012*"))
  )
  val emptyConsignmentReferencesJSON: JsValue = JsObject(Map("ducr" -> JsObject(Map("ducr" -> JsString(""))), "lrn" -> JsString("")))
}
