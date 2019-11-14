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

import forms.declaration.TaricCode
import helpers.views.declaration.CommonMessages
import models.DeclarationType.DeclarationType
import models.{DeclarationType, Mode}
import org.jsoup.nodes.Document
import play.api.data.Form
import services.cache.ExportsTestData
import unit.tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.taric_codes
import views.tags.ViewTest

@ViewTest
class TaricCodesViewSpec extends UnitViewSpec with ExportsTestData with Stubs with CommonMessages {

  private val page = new taric_codes(mainTemplate, minimalAppConfig)
  private val itemId = "item1"
  private val realMessages = validatedMessages
  private def createView(declarationType: DeclarationType, form: Form[TaricCode], codes: List[TaricCode]): Document =
    page(Mode.Normal, itemId, form, codes)(journeyRequest(declarationType), realMessages)

  def taricCodeView(declarationType: DeclarationType, taricCode: Option[TaricCode] = None, codes: List[TaricCode] = List.empty): Unit = {
    val form = taricCode.fold(TaricCode.form)(TaricCode.form.fill(_))
    val view = createView(declarationType, form, codes)

    "display page title" in {

      view.getElementById("title").text() mustBe realMessages("declaration.taricAdditionalCodes.header")
    }

    "display taric code input field" in {
      val expectedCode = taricCode.map(_.taricCode).getOrElse("")
      view.getElementById(TaricCode.taricCodeKey).attr("value") mustBe expectedCode
    }

    "display existing taric codes table" in {
      codes.zipWithIndex.foreach {
        case (code, index) => {
          view.getElementById(s"taricCode-table-row$index-label").text mustBe code.taricCode
          var removeButton = view.getElementById(s"taricCode-table-row$index-remove_button")
          removeButton.text must include(realMessages(removeCaption))
          removeButton.text must include(realMessages("declaration.taricAdditionalCodes.remove.hint", code.taricCode))
        }
      }
    }

    "display 'Add' button on page" in {

      val addButton = view.getElementById("add")
      addButton.text() must include(realMessages(addCaption))
      addButton.text() must include(realMessages("declaration.taricAdditionalCodes.add.hint"))
    }

    "display 'Back' button that links to 'CUS Code' page" in {
      val backButton = view.getElementById("link-back")

      backButton.getElementById("link-back") must haveHref(controllers.declaration.routes.CUSCodeController.displayPage(Mode.Normal, itemId))
    }

    "display 'Save and continue' button on page" in {

      val saveButton = view.select("#submit")
      saveButton.text() mustBe realMessages(saveAndContinueCaption)
    }
  }

  "Taric Code View on empty page" when {
    "we are on Standard journey" should {
      behave like taricCodeView(DeclarationType.STANDARD)
    }
    "we are on Supplementary journey" should {
      behave like taricCodeView(DeclarationType.SUPPLEMENTARY)
    }
    "we are on Simplified journey" should {
      behave like taricCodeView(DeclarationType.SIMPLIFIED)
    }
  }

  "Taric Code View on populated page" when {
    val taricCode = Some(TaricCode("1234"))
    val existingCodes = List(TaricCode("ABCD"), TaricCode("4321"))

    "we are on Standard journey" should {
      behave like taricCodeView(DeclarationType.STANDARD, taricCode, existingCodes)
    }
    "we are on Supplementary journey" should {
      behave like taricCodeView(DeclarationType.SUPPLEMENTARY, taricCode, existingCodes)
    }
    "we are on Simplified journey" should {
      behave like taricCodeView(DeclarationType.SIMPLIFIED, taricCode, existingCodes)
    }
  }
}