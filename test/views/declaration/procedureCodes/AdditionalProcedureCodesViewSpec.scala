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

package views.declaration.procedureCodes

import base.Injector
import forms.common.YesNoAnswer.allYesNoAnswers
import forms.declaration.countries.Country
import forms.declaration.procedurecodes.AdditionalProcedureCode
import models.DeclarationType.CLEARANCE
import models.Mode
import models.codes.{AdditionalProcedureCode => AdditionalProcedureCodeModel, ProcedureCode}
import models.requests.JourneyRequest
import org.jsoup.nodes.Document
import play.api.data.Form
import services.cache.ExportsTestHelper
import tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.procedureCodes.additional_procedure_codes
import views.tags.ViewTest

@ViewTest
class AdditionalProcedureCodesViewSpec extends UnitViewSpec with ExportsTestHelper with Stubs with Injector {

  private val page = instanceOf[additional_procedure_codes]
  private val form: Form[AdditionalProcedureCode] = AdditionalProcedureCode.form()
  private val itemId = "itemId"
  private val sampleProcedureCode = ProcedureCode("1040", "blah blah blah")
  private val defaultAdditionalProcedureCodes = Seq(AdditionalProcedureCodeModel("000", "None"))

  private def createView(
    form: Form[AdditionalProcedureCode] = form,
    validCodes: Seq[AdditionalProcedureCodeModel] = defaultAdditionalProcedureCodes,
    codes: Seq[String] = Seq.empty,
    mode: Mode = Mode.Normal
  )(implicit request: JourneyRequest[_]): Document =
    page(mode, itemId, form, sampleProcedureCode, validCodes, codes)(request, messages)

  "Additional Procedure Codes View" should {

    "have proper messages for labels" in {
      messages must haveTranslationFor("declaration.additionalProcedureCodes.title")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.paragraph1")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.paragraph2")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.jersey.hint")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.jersey.clearanceNonEidr.hint")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.table.header")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.inset")
      messages must haveTranslationFor("declaration.additionalProcedureCodes.inset.linkText")
    }

    onEveryDeclarationJourney() { implicit request =>
      "provided with empty form" should {
        val view = createView()

        "display page title" in {
          view.getElementsByTag("h1") must containMessageForElements("declaration.additionalProcedureCodes.title", sampleProcedureCode.code)
        }

        "display section header" in {
          view.getElementById("section-header") must containMessage("declaration.section.5")
        }

        "display empty input with label for Additional Procedure Codes" in {
          view.getElementById("additionalProcedureCode").attr("value") mustBe empty
        }

        "display 'Back' button that links to 'Procedure Codes' page" in {
          val backButton = view.getElementById("back-link")

          backButton must containMessage("site.back")
          backButton.getElementById("back-link") must haveHref(
            controllers.declaration.routes.ProcedureCodesController.displayPage(Mode.Normal, itemId)
          )
        }

        "display 'Add' button on page" in {
          val addButton = view.getElementById("add")
          addButton.text() must include(messages("site.add"))
          addButton.text() must include(messages("declaration.additionalProcedureCodes.add.hint"))
        }

        val createViewWithMode: Mode => Document = mode => createView(mode = mode)
        checkAllSaveButtonsAreDisplayed(createViewWithMode)
      }

      "provided with filled form" should {
        "display data in Additional Procedure Code input" in {
          val view = createView(form = AdditionalProcedureCode.form().fill(AdditionalProcedureCode(Some("123"))))

          view.getElementById("additionalProcedureCode").attr("value") mustBe "123"
        }

        "display table headers" in {
          val view = createView(codes = Seq("123", "456"))

          view.getElementsByTag("th").get(0).text() mustBe messages("declaration.additionalProcedureCodes.table.header")
        }

        "have visually hidden header for Remove links" in {
          val view = createView(codes = Seq("123", "456"))

          view.getElementsByTag("th").get(1).text() mustBe messages("site.remove.header")
        }

        "display table values in reverse order they were entered" in {
          val view = createView(codes = Seq("123", "456"))

          view.getElementsByTag("tr").get(1).text() must include("456")
          view.getElementsByTag("tr").get(2).text() must include("123")
        }
      }
    }

    "display the correct hint" when {
      for {
        answer <- allYesNoAnswers
        country <- List(Country(Some("JE")), Country(Some("GG")))
      } onEveryDeclarationJourney(withDestinationCountry(country), withEntryIntoDeclarantsRecords(answer)) { implicit request =>
        s"$country entered in destination country and EIDR answer is $answer" in {
          val view = createView()

          view.getElementById("additionalProcedureCode-hint") must {
            if (request.cacheModel.isNotEntryIntoDeclarantsRecords && request.declarationType == CLEARANCE)
              containMessage("declaration.additionalProcedureCodes.jersey.clearanceNonEidr.hint")
            else containMessage("declaration.additionalProcedureCodes.jersey.hint")
          }
        }
      }
    }
  }
}
