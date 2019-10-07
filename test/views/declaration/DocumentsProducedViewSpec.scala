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

package views.declaration

import base.{Injector, TestHelper}
import controllers.util.SaveAndReturn
import forms.common.Date._
import forms.declaration.DocumentsProducedSpec._
import forms.declaration.additionaldocuments.DocumentIdentifierAndPart._
import forms.declaration.additionaldocuments.DocumentIdentifierAndPartSpec._
import forms.declaration.additionaldocuments.DocumentWriteOff._
import forms.declaration.additionaldocuments.DocumentWriteOffSpec._
import forms.declaration.additionaldocuments.DocumentsProduced
import forms.declaration.additionaldocuments.DocumentsProduced._
import helpers.views.components.DateMessages
import helpers.views.declaration.{CommonMessages, DocumentsProducedMessages}
import models.Mode
import org.jsoup.nodes.Document
import org.scalatest.OptionValues
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import unit.tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.documents_produced
import views.tags.ViewTest

@ViewTest
class DocumentsProducedViewSpec
    extends UnitViewSpec with DocumentsProducedMessages with DateMessages with CommonMessages with Stubs with Injector
    with OptionValues {

  val itemId = "a7sc78"
  private val form: Form[DocumentsProduced] = DocumentsProduced.form()
  private val documentsProducedPage = new documents_produced(mainTemplate)
  private def createView(
    form: Form[DocumentsProduced] = form,
    cachedDocuments: Seq[DocumentsProduced] = Seq()
  ): Document =
    documentsProducedPage(Mode.Normal, itemId, form, cachedDocuments)(request, messages)

  "Document Produced" should {

    "have correct message keys" in {

      val messages = instanceOf[MessagesApi].preferred(request)

      messages must haveTranslationFor("supplementary.addDocument.title")
      messages must haveTranslationFor("supplementary.addDocument.hint")
      messages must haveTranslationFor("supplementary.addDocument.documentTypeCode")
      messages must haveTranslationFor("supplementary.addDocument.item.documentTypeCode")
      messages must haveTranslationFor("supplementary.addDocument.documentTypeCode.error")
      messages must haveTranslationFor("supplementary.addDocument.documentIdentifier")
      messages must haveTranslationFor("supplementary.addDocument.item.documentIdentifier")
      messages must haveTranslationFor("supplementary.addDocument.documentIdentifier.error")
      messages must haveTranslationFor("supplementary.addDocument.documentPart")
      messages must haveTranslationFor("supplementary.addDocument.item.documentPart")
      messages must haveTranslationFor("supplementary.addDocument.documentPart.error")
      messages must haveTranslationFor("supplementary.addDocument.documentStatus")
      messages must haveTranslationFor("supplementary.addDocument.item.documentStatus")
      messages must haveTranslationFor("supplementary.addDocument.documentStatus.error")
      messages must haveTranslationFor("supplementary.addDocument.documentStatusReason")
      messages must haveTranslationFor("supplementary.addDocument.item.documentStatusReason")
      messages must haveTranslationFor("supplementary.addDocument.documentStatusReason.error")
      messages must haveTranslationFor("supplementary.addDocument.issuingAuthorityName")
      messages must haveTranslationFor("supplementary.addDocument.issuingAuthorityName.error.length")
      messages must haveTranslationFor("supplementary.addDocument.dateOfValidity")
      messages must haveTranslationFor("supplementary.addDocument.measurementUnit")
      messages must haveTranslationFor("supplementary.addDocument.measurementUnit.error.length")
      messages must haveTranslationFor("supplementary.addDocument.measurementUnit.error.specialCharacters")
      messages must haveTranslationFor("supplementary.addDocument.documentQuantity")
      messages must haveTranslationFor("supplementary.addDocument.item.documentQuantity")
      messages must haveTranslationFor("supplementary.addDocument.documentQuantity.error.precision")
      messages must haveTranslationFor("supplementary.addDocument.documentQuantity.error.scale")
      messages must haveTranslationFor("supplementary.addDocument.documentQuantity.error")
      messages must haveTranslationFor("supplementary.addDocument.error.maximumAmount")
      messages must haveTranslationFor("supplementary.addDocument.error.duplicated")
      messages must haveTranslationFor("supplementary.addDocument.error.notDefined")
      messages must haveTranslationFor("supplementary.addDocument.error.documentIdentifierAndPart")
      messages must haveTranslationFor("supplementary.addDocument.error.measurementUnitAndQuantity")
    }
  }

  "Documents Produced View on empty page" should {

    val view = createView()

    "display page title" in {
      view.getElementById("title").text() mustBe messages(title)
    }

    "display section header" in {
      view.getElementById("section-header").text() mustBe include(messages(
        "supplementary.summary.yourReferences.header"
      ))
    }

    "display header with hint" in {
      view.getElementById("hint").text() must include(messages(hint))
    }

    "display empty input with label for Document type code" in {
      view.getElementById(s"$documentTypeCodeKey-label").text() mustBe messages(documentTypeCode)
      view.getElementById(s"$documentTypeCodeKey").attr("value") mustBe empty
    }

    "display empty input with label for Document identifier" in {
      view.getElementById(s"${documentIdentifierAndPartKey}_$documentIdentifierKey-label").text() mustBe
        messages(documentIdentifier)

      view.getElementById(s"${documentIdentifierAndPartKey}_$documentIdentifierKey").attr("value") mustBe empty
    }

    "display empty input with label for Document status" in {
      view.getElementById(s"$documentStatusKey-label").text() mustBe messages(documentStatus)
      view.getElementById(s"$documentStatusKey").attr("value") mustBe empty
    }

    "display empty input with label for Document status reason" in {
      view.getElementById(s"$documentStatusReasonKey-label").text() mustBe messages(documentStatusReason)
      view.getElementById(s"$documentStatusReasonKey").attr("value") mustBe empty
    }

    "display empty input with label for Issuing Authority Name" in {
      view.getElementById(s"$issuingAuthorityNameKey-label").text() mustBe messages(issuingAuthorityName)
      view.getElementById(issuingAuthorityNameKey).attr("value") mustBe empty
    }

    "display empty input with label for Date of Validity" in {
      view.getElementById(s"$dateOfValidityKey-label").text() mustBe messages(dateOfValidity)
      view.getElementById(dateOfValidityKey).attr("value") mustBe empty
    }

    "display empty input with label for Measurement Unit" in {
      view.getElementById(s"${documentWriteOffKey}_$measurementUnitKey-label").text() mustBe messages(measurementUnit)
      view.getElementById(s"${documentWriteOffKey}_$measurementUnitKey").attr("value") mustBe empty
    }

    "display empty input with label for Document quantity" in {
      view.getElementById(s"${documentWriteOffKey}_$documentQuantityKey-label").text() mustBe
        messages(documentQuantity)

      view.getElementById(s"${documentWriteOffKey}_$documentQuantityKey").attr("value") mustBe empty
    }

    "display 'Back' button that links to 'Additional Information' page" in {

      val backButton = view.getElementById("link-back")

      backButton.text() mustBe messages(backCaption)
      backButton.attr("href") must endWith(s"/items/$itemId/additional-information")
    }

    "display both 'Add' and 'Save and continue' button on page" in {
      val addButton = view.getElementById("add")
      addButton.text() mustBe messages(addCaption)

      val saveAndContinueButton = view.getElementById("submit")
      saveAndContinueButton.text() mustBe messages(saveAndContinueCaption)

      val saveAndReturnButton = view.getElementById("submit_and_return")
      saveAndReturnButton.text() mustBe messages(saveAndReturnCaption)
      saveAndReturnButton.attr("name") mustBe SaveAndReturn.toString
    }
  }

  "Documents Produced View for invalid input" should {

    "display error for Document type code" in {

      val view = createView(
        DocumentsProduced.form
          .fillAndValidate(
            correctDocumentsProduced.copy(documentTypeCode = Some(TestHelper.createRandomAlphanumericString(5)))
          )
      )

      checkErrorsSummary(view)
      view must haveFieldErrorLink(s"$documentTypeCodeKey", s"#$documentTypeCodeKey")

      view.select(s"#error-message-$documentTypeCodeKey-input").text() mustBe messages(documentTypeCodeError)
    }

    "display error for Document identifier" in {

      val documentsProducedWithIncorrectDocumentIdentifier = correctDocumentsProduced.copy(
        documentIdentifierAndPart = Some(
          correctDocumentIdentifierAndPart
            .copy(documentIdentifier = incorrectDocumentIdentifierAndPart.documentIdentifier)
        )
      )
      val view =
        createView(DocumentsProduced.form.bind(Json.toJson(documentsProducedWithIncorrectDocumentIdentifier)))

      checkErrorsSummary(view)
      view must haveFieldErrorLink(
        s"$documentIdentifierAndPartKey.$documentIdentifierKey",
        s"#${documentIdentifierAndPartKey}_$documentIdentifierKey"
      )

      view
        .select(s"#error-message-${documentIdentifierAndPartKey}_$documentIdentifierKey-input")
        .text() mustBe messages(documentIdentifierError)
    }

    "display error for Document status" in {

      val view = createView(
        DocumentsProduced.form
          .fillAndValidate(correctDocumentsProduced.copy(documentStatus = Some("ABC")))
      )

      checkErrorsSummary(view)
      view must haveFieldErrorLink(s"$documentStatusKey", s"#$documentStatusKey")

      view.select(s"#error-message-$documentStatusKey-input").text() mustBe messages(documentStatusError)
    }

    "display error for Document status reason" in {

      val view = createView(
        DocumentsProduced.form
          .fillAndValidate(
            correctDocumentsProduced.copy(documentStatusReason = Some(TestHelper.createRandomAlphanumericString(36)))
          )
      )

      checkErrorsSummary(view)
      view must haveFieldErrorLink(s"$documentStatusReasonKey", s"#$documentStatusReasonKey")

      view.select(s"#error-message-$documentStatusReasonKey-input").text() mustBe messages(documentStatusReasonError)
    }

    "display error for Issuing Authority Name" in {

      val view = createView(
        DocumentsProduced.form
          .fillAndValidate(
            correctDocumentsProduced.copy(issuingAuthorityName = Some(TestHelper.createRandomAlphanumericString(71)))
          )
      )

      checkErrorsSummary(view)
      view must haveFieldErrorLink(s"$issuingAuthorityNameKey", s"#$issuingAuthorityNameKey")

      view.select(s"#error-message-$issuingAuthorityNameKey-input").text() mustBe
        messages(issuingAuthorityNameLengthError)
    }

    "display error for Date of Validity" when {

      "year is out of range (2000-2099)" in {

        val view = createView(
          DocumentsProduced.form
            .bind(
              correctDocumentsProducedMap ++ Map(
                s"$dateOfValidityKey.$yearKey" -> "1999",
                s"$dateOfValidityKey.$monthKey" -> "12",
                s"$dateOfValidityKey.$dayKey" -> "30"
              )
            )
        )

        checkErrorsSummary(view)
        view must haveFieldErrorLink(s"$dateOfValidityKey", s"#$dateOfValidityKey")

        view.select(s"#error-message-$dateOfValidityKey-input").text() mustBe messages(dateOutOfRangeError)
      }

      "provided with non-existing month and day" in {

        val view = createView(
          DocumentsProduced.form
            .bind(
              correctDocumentsProducedMap ++ Map(
                s"$dateOfValidityKey.$monthKey" -> "13",
                s"$dateOfValidityKey.$dayKey" -> "32"
              )
            )
        )

        checkErrorsSummary(view)
        view must haveFieldErrorLink(s"$dateOfValidityKey", s"#$dateOfValidityKey")

        view.select(s"#error-message-$dateOfValidityKey-input").text() mustBe messages(dateFormatError)
      }
    }

    "display error for Measurement Unit" in {

      val documentsProducedWithIncorrectMeasurementUnit = correctDocumentsProduced.copy(
        documentWriteOff =
          Some(correctDocumentWriteOff.copy(measurementUnit = incorrectDocumentWriteOff.measurementUnit))
      )
      val view = createView(DocumentsProduced.form.bind(Json.toJson(documentsProducedWithIncorrectMeasurementUnit)))

      checkErrorsSummary(view)
      view must haveFieldErrorLink(
        s"$documentWriteOffKey.$measurementUnitKey",
        s"#${documentWriteOffKey}_$measurementUnitKey"
      )

      view.select(s"#error-message-${documentWriteOffKey}_$measurementUnitKey-input").text() mustBe
        messages(measurementUnitLengthError)
    }

    "display error for Document quantity" in {

      val documentsProducedWithIncorrectDocumentQuantity = correctDocumentsProduced.copy(
        documentWriteOff =
          Some(correctDocumentWriteOff.copy(documentQuantity = incorrectDocumentWriteOff.documentQuantity))
      )
      val view = createView(DocumentsProduced.form.bind(Json.toJson(documentsProducedWithIncorrectDocumentQuantity)))

      checkErrorsSummary(view)
      view must haveFieldErrorLink(
        s"$documentWriteOffKey.$documentQuantityKey",
        s"#${documentWriteOffKey}_$documentQuantityKey"
      )

      view.select(s"#error-message-${documentWriteOffKey}_$documentQuantityKey-input").text() mustBe
        messages(documentQuantityPrecisionError)
    }

    "display error for Document WriteOff" when {

      "provided with Measurement Unit but no Document Quantity" in {

        val documentsProducedWithIncorrectDocumentWriteOff = correctDocumentsProduced.copy(
          documentWriteOff = Some(emptyDocumentWriteOff.copy(measurementUnit = correctDocumentWriteOff.measurementUnit))
        )
        val view =
          createView(DocumentsProduced.form.bind(Json.toJson(documentsProducedWithIncorrectDocumentWriteOff)))

        checkErrorsSummary(view)
        view must haveFieldErrorLink(s"$documentWriteOffKey", s"#$documentWriteOffKey")

        view.select(s"#error-message-$documentWriteOffKey-input").text() mustBe
          messages(measurementUnitAndQuantityError)
      }

      "provided with Document Quantity but no Measurement Unit" in {

        val documentsProducedWithIncorrectDocumentWriteOff = correctDocumentsProduced.copy(
          documentWriteOff =
            Some(emptyDocumentWriteOff.copy(documentQuantity = correctDocumentWriteOff.documentQuantity))
        )
        val view =
          createView(DocumentsProduced.form.bind(Json.toJson(documentsProducedWithIncorrectDocumentWriteOff)))

        checkErrorsSummary(view)
        view must haveFieldErrorLink(s"$documentWriteOffKey", s"#$documentWriteOffKey")

        view.select(s"#error-message-$documentWriteOffKey-input").text() mustBe
          messages(measurementUnitAndQuantityError)
      }
    }

    "display errors for all fields" in {

      val form = DocumentsProduced.form.bind(incorrectDocumentsProducedMap)

      val view = createView(form)

      checkErrorsSummary(view)
      view must haveFieldErrorLink(s"$documentTypeCodeKey", s"#$documentTypeCodeKey")
      view must haveFieldErrorLink(
        s"$documentIdentifierAndPartKey.$documentIdentifierKey",
        s"#${documentIdentifierAndPartKey}_$documentIdentifierKey"
      )
      view must haveFieldErrorLink(s"$documentStatusKey", s"#$documentStatusKey")
      view must haveFieldErrorLink(s"$documentStatusReasonKey", s"#$documentStatusReasonKey")
      view must haveFieldErrorLink(s"$issuingAuthorityNameKey", s"#$issuingAuthorityNameKey")
      view must haveFieldErrorLink(s"$dateOfValidityKey", s"#$dateOfValidityKey")
      view must haveFieldErrorLink(
        s"$documentWriteOffKey.$measurementUnitKey",
        s"#${documentWriteOffKey}_$measurementUnitKey"
      )
      view must haveFieldErrorLink(
        s"$documentWriteOffKey.$documentQuantityKey",
        s"#${documentWriteOffKey}_$documentQuantityKey"
      )

      view.select(s"#error-message-$documentTypeCodeKey-input").text() mustBe messages(documentTypeCodeError)
      view
        .select(s"#error-message-${documentIdentifierAndPartKey}_$documentIdentifierKey-input")
        .text() mustBe messages(documentIdentifierError)

      view.select(s"#error-message-$documentStatusKey-input").text() mustBe messages(documentStatusError)
      view.select(s"#error-message-$documentStatusReasonKey-input").text() mustBe messages(documentStatusReasonError)
      view.select(s"#error-message-$issuingAuthorityNameKey-input").text() mustBe
        messages(issuingAuthorityNameLengthError)
      view.select(s"#error-message-$dateOfValidityKey-input").text() mustBe messages(dateFormatError)
      view.select(s"#error-message-${documentWriteOffKey}_$measurementUnitKey-input").text() mustBe
        messages(measurementUnitLengthError)
      view.select(s"#error-message-${documentWriteOffKey}_$documentQuantityKey-input").text() mustBe
        messages(documentQuantityPrecisionError)
    }
  }

  "Documents Produced View when filled" should {

    "display data in all inputs" in {

      val data = correctDocumentsProduced
      val form = DocumentsProduced.form.fill(data)
      val view = createView(form)

      view.getElementById(documentTypeCodeKey).attr("value") must equal(data.documentTypeCode.value)
      view.getElementById(s"${documentIdentifierAndPartKey}_$documentIdentifierKey").attr("value") must equal(
        data.documentIdentifierAndPart.value.documentIdentifier
      )
      view.getElementById(documentStatusKey).attr("value") must equal(data.documentStatus.value)
      view.getElementById(documentStatusReasonKey).attr("value") must equal(data.documentStatusReason.value)
      view.getElementById(issuingAuthorityNameKey).attr("value") must equal(data.issuingAuthorityName.value)
      view.getElementById(s"${dateOfValidityKey}_$dayKey").attr("value") must equal(
        data.dateOfValidity.value.day.value.toString
      )
      view.getElementById(s"${dateOfValidityKey}_$monthKey").attr("value") must equal(
        data.dateOfValidity.value.month.value.toString
      )
      view.getElementById(s"${dateOfValidityKey}_$yearKey").attr("value") must equal(
        data.dateOfValidity.value.year.value.toString
      )
      view.getElementById(s"${documentWriteOffKey}_$measurementUnitKey").attr("value") must equal(
        data.documentWriteOff.get.measurementUnit.value
      )
      view.getElementById(s"${documentWriteOffKey}_$documentQuantityKey").attr("value") must equal(
        data.documentWriteOff.get.documentQuantity.value.toString
      )
    }

    "display a table with previously entered document" which {

      val view = createView(cachedDocuments = Seq(correctDocumentsProduced))

      "have header row" that {
        val header = view.selectFirst(".documents thead tr")

        "have header for Document Type" in {
          header.selectFirst(".document-type").text()  must equal(
            messages(documentTypeCode)
          )
        }

        "have header for Document Identifier" in {
          header.selectFirst(".document-identifier").text() must equal(
            messages(documentIdentifier)
          )
        }
        "have header for Document Status" in {
          header.selectFirst(".document-status").text() must equal(
            messages(documentStatus)
          )
        }
        "have header for Document Status Reason" in {
          header.selectFirst(".document-status-reason").text() must equal(
            messages(documentStatusReason)
          )
        }
        "have header for Document Issuing Authroity" in {
          header.selectFirst(".document-issuing-authority").text() must equal(
            messages(issuingAuthorityName)
          )
        }
        "have header for Document Date of Validtiy" in {
          header.selectFirst(".date-of-validity").text() must equal(
            messages(dateOfValidity)
          )
        }
        "have header for Document Measurement Unit" in {
          header.selectFirst(".measurement-unit").text() must equal(
            messages(measurementUnit)
          )
        }
        "have header for Document Quntity" in {
          header.selectFirst(".document-quantity").text() must equal(
            messages(documentQuantity)
          )
        }
      }



      "have data row" that {

        val row  = view.select(".documents tbody tr").first()

        "have Document Type" in {
          row.selectFirst(".document-type").text() must equal(
            correctDocumentsProduced.documentTypeCode.get
          )
        }

        "have Document Identifier" in {
          row.selectFirst(".document-identifier").text() must equal(
            correctDocumentsProduced.documentIdentifierAndPart.get.documentIdentifier
          )
        }
        "have Document Status" in {
          row.selectFirst(".document-status").text() must equal(
            correctDocumentsProduced.documentStatus.get
          )
        }
        "have Document Status Reason" in {
          row.selectFirst(".document-status-reason").text() must equal(
            correctDocumentsProduced.documentStatusReason.get
          )
        }

        "have Document Issuing Authority" in {
          row.selectFirst(".document-issuing-authority").text() must equal(
            correctDocumentsProduced.issuingAuthorityName.get
          )
        }

        "have Document Date of Validity" in {
          row.selectFirst(".date-of-validity").text() must equal(
            correctDocumentsProduced.dateOfValidity.get.toString
          )
        }

        "have Document Measurment Unit" in {
          row.selectFirst(".measurement-unit").text() must equal(
            correctDocumentsProduced.documentWriteOff.get.measurementUnit.get
          )
        }

        "have Document Quantitiy" in {
          row.selectFirst(".document-quantity").text() must equal(
            correctDocumentsProduced.documentWriteOff.get.documentQuantity.get.toString
          )
        }

        "have remove button" in {
          val removeButton = row.selectFirst(".remove .remove-button")
          removeButton.text() mustBe messages("site.remove")
          removeButton.attr("value") mustBe correctDocumentsProduced.toJson.toString()
        }
      }

    }
  }
}
