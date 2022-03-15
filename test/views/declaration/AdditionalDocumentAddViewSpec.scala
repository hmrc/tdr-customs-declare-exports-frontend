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

package views.declaration

import base.Injector
import com.typesafe.config.ConfigFactory
import controllers.declaration.routes
import forms.common.{Eori, YesNoAnswer}
import forms.declaration.additionaldocuments.AdditionalDocument
import forms.declaration.declarationHolder.DeclarationHolder
import models.DeclarationType._
import models.{ExportsDeclaration, Mode}
import models.declaration.EoriSource
import models.declaration.ExportDeclarationTestData.declaration
import models.requests.JourneyRequest
import org.jsoup.nodes.Document
import org.scalatest.{Assertion, OptionValues}
import play.api.Configuration
import play.api.data.Form
import play.api.mvc.Call
import tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.helpers.CommonMessages
import views.html.declaration.additionalDocuments.additional_document_add
import views.tags.ViewTest

@ViewTest
class AdditionalDocumentAddViewSpec extends UnitViewSpec with CommonMessages with Stubs with Injector with OptionValues {

  override val configuration: Configuration = Configuration(ConfigFactory.parseString("microservice.services.features.waiver999L=enabled"))

  private val itemId = "a7sc78"
  private val mode = Mode.Normal

  private val additionalDocumentAddPage = instanceOf[additional_document_add]

  private val form: Form[AdditionalDocument] = AdditionalDocument.form(declaration)

  private def createView(implicit request: JourneyRequest[_]): Document =
    additionalDocumentAddPage(mode, itemId, form)(request, messages)

  object TestDeclaration {
    private val withAdditionalInfo = withAdditionalInformation("code", "desc")
    private val withAdditionalDocs =
      withAdditionalDocuments(YesNoAnswer.Yes, AdditionalDocument(Some("C501"), Some("GBAEOC1342"), None, None, None, None, None))

    val licenseRequired: ExportsDeclaration = aDeclaration(withItem(anItem(withItemId(itemId), withLicenseRequired(), withAdditionalInfo)))
    val licenseNotRequired: ExportsDeclaration = aDeclaration(withItem(anItem(withItemId(itemId), withAdditionalInfo)))
    val withDocs: ExportsDeclaration = aDeclaration(withItem(anItem(withItemId(itemId), withAdditionalDocs)))
    val authCodeRequiresDocs: ExportsDeclaration = aDeclaration(
      withDeclarationHolders(DeclarationHolder(Some("OPO"), Some(Eori("GB123456789012")), Some(EoriSource.OtherEori))),
      withItem(anItem(withItemId(itemId), withAdditionalInfo))
    )
  }

  "additional_document_add view" when {

    "asked previously 'Is License Required?'" when {

      onJourney(STANDARD, SUPPLEMENTARY, SIMPLIFIED, OCCASIONAL)(TestDeclaration.licenseRequired) { implicit request =>
        "display a 'Back' button that links to the 'Is License Required' page" when {
          "license is required" in {
            verifyBackButton(routes.IsLicenceRequiredController.displayPage(mode, itemId))
          }
        }
      }

      onJourney(STANDARD, SUPPLEMENTARY, SIMPLIFIED, OCCASIONAL)(TestDeclaration.authCodeRequiresDocs) { implicit request =>
        "display a 'Back' button that links to the 'Is License Required' page" when {
          "license is not required" when {
            "the authorisation code requires additional documents" in {
              verifyBackButton(routes.IsLicenceRequiredController.displayPage(mode, itemId))
            }

          }
        }

      }

      onJourney(STANDARD, SUPPLEMENTARY, SIMPLIFIED, OCCASIONAL)(TestDeclaration.licenseNotRequired) { implicit request =>
        "display a 'Back' button that links to the 'Additional Documents Required' page" when {

          "license is not required" when {
            "the authorisation code does not require additional documents" in {

              verifyBackButton(routes.AdditionalDocumentsRequiredController.displayPage(mode, itemId))
            }
          }
        }

      }

    }

    "additional documents are present" should {

      onJourney(CLEARANCE)(TestDeclaration.withDocs) { implicit request =>
        "display a 'Back' button that links to the 'Additional Documents Required' page" in {
          verifyBackButton(routes.AdditionalDocumentsController.displayPage(mode, itemId))
        }
      }
    }

    "no additional documents are present" when {

      "the authorisation code does not require additional documents" should {
        onJourney(CLEARANCE)(TestDeclaration.licenseNotRequired) { implicit request =>
          "display a 'Back' button that links to the 'Additional Documents Required' page" in {
            verifyBackButton(routes.AdditionalDocumentsRequiredController.displayPage(mode, itemId))
          }
        }

      }

      "the authorisation code requires additional documents" should {

        onJourney(CLEARANCE)(TestDeclaration.authCodeRequiresDocs) { implicit request =>
          "display a 'Back' button that links to the 'Additional Information List' page" in {
            verifyBackButton(routes.AdditionalInformationController.displayPage(mode, itemId))
          }
        }

      }
    }

    def verifyBackButton(call: Call)(implicit request: JourneyRequest[_]): Assertion = {
      val backButton = createView.getElementById("back-link")
      backButton must containMessage(backCaption)
      backButton must haveHref(call)
    }
  }
}
