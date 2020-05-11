/*
 * Copyright 2020 HM Revenue & Customs
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
import controllers.util.{Add, SaveAndContinue, SaveAndReturn}
import forms.declaration.AdditionalInformation
import helpers.views.declaration.CommonMessages
import models.DeclarationType.DeclarationType
import models.{DeclarationType, Mode}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.i18n.MessagesApi
import services.cache.ExportsTestData
import unit.tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.additional_information
import views.tags.ViewTest

@ViewTest
class AdditionalInformationViewSpec extends UnitViewSpec with ExportsTestData with CommonMessages with Stubs with Injector {

  val itemId = "a7sc78"
  private val form: Form[AdditionalInformation] = AdditionalInformation.form()

  private val page = instanceOf[additional_information]

  private def createView(declarationType: DeclarationType = DeclarationType.STANDARD, form: Form[AdditionalInformation] = form): Document =
    page(Mode.Normal, itemId, form, Seq())(journeyRequest(declarationType), messages)

  "Additional Information View" should {

    "have a proper messages" in {

      val messages = instanceOf[MessagesApi].preferred(request)

      messages must haveTranslationFor("declaration.additionalInformation.title")
      messages must haveTranslationFor("declaration.additionalInformation.code")
      messages must haveTranslationFor("declaration.additionalInformation.item.code")
      messages must haveTranslationFor("declaration.additionalInformation.code.error")
      messages must haveTranslationFor("declaration.additionalInformation.code.empty")
      messages must haveTranslationFor("declaration.additionalInformation.description")
      messages must haveTranslationFor("declaration.additionalInformation.item.description")
      messages must haveTranslationFor("declaration.additionalInformation.description.error")
      messages must haveTranslationFor("declaration.additionalInformation.description.empty")
    }
  }

  "Additional Information View on empty page" should {

    "display page title" in {

      createView().getElementsByTag("h1").text() mustBe messages("declaration.additionalInformation.title")
    }

    "display section header" in {

      createView().getElementById("section-header").text() must include("supplementary.summary.yourReferences.header")
    }

    "display empty input with label for Union code" in {

      val view = createView()

      view.getElementsByAttributeValue("for", "code").text() mustBe messages("declaration.additionalInformation.code")
      view.getElementById("code").attr("value") mustBe empty
    }

    "display empty input with label for Description" in {

      val view = createView()

      view.getElementsByAttributeValue("for", "description").text() mustBe messages("declaration.additionalInformation.description")
      view.getElementById("description").attr("value") mustBe empty
    }

    "display 'Back' button that links to 'Commodity measure' page" when {
      "on the Standard journey" in {

        val backButton = createView().getElementById("back-link")

        backButton.text() mustBe messages(backCaption)
        backButton.attr("href") must endWith(s"/items/$itemId/commodity-measure")
      }

      "on the Simplified journey" in {

        val backButton = createView(declarationType = DeclarationType.SIMPLIFIED).getElementById("back-link")

        backButton.text() mustBe messages(backCaption)
        backButton.attr("href") must endWith(s"/items/$itemId/package-information")
      }
    }

    "display 'Save and continue' button" in {
      val view: Document = createView()
      view must containElement("button").withName(SaveAndContinue.toString)
    }

    "display 'Save and return' button" in {
      val view: Document = createView()
      view must containElement("button").withName(SaveAndReturn.toString)
    }

    "display 'Add' button" in {
      val view: Document = createView()
      view must containElement("button").withName(Add.toString)
    }
  }

  "Additional Information View when filled" should {

    "display data in both inputs" in {

      val view = createView(form = AdditionalInformation.form.fill(AdditionalInformation("12345", "12345")))

      view.getElementById("code").attr("value") mustBe "12345"
      view.getElementById("description").text() mustBe "12345"

    }

    "display data in code input" in {

      val view = createView(form = AdditionalInformation.form.fill(AdditionalInformation("12345", "")))

      view.getElementById("code").attr("value") mustBe "12345"
      view.getElementById("description").text() mustBe empty
    }

    "display data in description input" in {

      val view = createView(form = AdditionalInformation.form.fill(AdditionalInformation("", "12345")))

      view.getElementById("code").attr("value") mustBe empty
      view.getElementById("description").text() mustBe "12345"
    }

    "display one row with data in table" which {

      val view = page(Mode.Normal, itemId, form, Seq(AdditionalInformation("12345", "12345678")))(journeyRequest(DeclarationType.STANDARD), messages)

      "has Code header" in {
        view.select("#additional_information thead tr th").get(0).text() mustBe "declaration.additionalInformation.table.headers.code"
      }

      "has 'Required information' header" in {
        view
          .select("#additional_information thead tr th")
          .get(1)
          .text() mustBe "declaration.additionalInformation.table.headers.description"
      }

      "has row with 'Code' in " in {
        view.select("#additional_information-row0-code").first().text() mustBe "12345"
      }

      "has row wiht 'Required information" in {
        view.select("#additional_information-row0-info").first().text() mustBe "12345678"
      }

      "has 'Remove' button" in {

        val removeButton = view.select("#additional_information-row0-remove_button .govuk-button").first()
        removeButton.text() mustBe "site.removedeclaration.additionalInformation.remove.hint"
        removeButton.attr("name") mustBe "Remove"
      }

    }
  }
}
