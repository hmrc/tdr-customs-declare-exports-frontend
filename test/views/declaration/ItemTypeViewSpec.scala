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

import base.Injector
import forms.declaration.ItemTypeForm
import models.Mode
import models.declaration.ExportItem
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.Helpers.stubMessages
import services.cache.ExportsTestData
import unit.tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.item_type
import views.tags.ViewTest

@ViewTest
class ItemTypeViewSpec extends UnitViewSpec with ExportsTestData with Stubs with Injector {

  private val page = new item_type(mainTemplate)
  private val form: Form[ItemTypeForm] = ItemTypeForm.form()
  private def createView(
    mode: Mode = Mode.Normal,
    item: ExportItem = ExportItem(id = "itemId", sequenceId = 1),
    form: Form[ItemTypeForm] = form,
    taricAdditionalCodes: Seq[String] = Seq.empty,
    nationalAdditionalCodes: Seq[String] = Seq.empty,
    messages: Messages = stubMessages()
  ): Document =
    page(mode, item, form, taricAdditionalCodes, nationalAdditionalCodes)(journeyRequest(), messages)

  "Item Type View on empty page" when {

    "have proper messages for labels" in {
      val messages = instanceOf[MessagesApi].preferred(journeyRequest())
      messages must haveTranslationFor("declaration.itemType.title")
      messages must haveTranslationFor("supplementary.items")
      messages must haveTranslationFor("declaration.itemType.title")
      messages must haveTranslationFor("declaration.itemType.taricAdditionalCodes.header")
      messages must haveTranslationFor("declaration.itemType.taricAdditionalCodes.header.hint")
      messages must haveTranslationFor("declaration.itemType.nationalAdditionalCode.header")
      messages must haveTranslationFor("declaration.itemType.nationalAdditionalCode.header.hint")
      messages must haveTranslationFor("declaration.itemType.statisticalValue.header")
      messages must haveTranslationFor("declaration.itemType.statisticalValue.header.hint")
      messages must haveTranslationFor("declaration.itemType.cusCode.header")
      messages must haveTranslationFor("declaration.itemType.unDangerousGoodsCode.header")
      messages must haveTranslationFor("declaration.itemType.unDangerousGoodsCode.header.hint")
    }

    val view = createView()

    "used for Standard Declaration journey" should {

      "display same page title as header" in {
        val viewWithMessage = createView(messages = realMessagesApi.preferred(request))
        viewWithMessage.title() must include(viewWithMessage.getElementsByTag("h1").text())
      }

      "display section header" in {
        view.getElementById("section-header").text() must include("supplementary.items")
      }

      "display empty input with label for TARIC" in {
        view
          .getElementById("taricAdditionalCode-label")
          .text() mustBe "declaration.itemType.taricAdditionalCodes.header"
        view
          .getElementById("taricAdditionalCode-hint")
          .text() mustBe "declaration.itemType.taricAdditionalCodes.header.hint"
        view.getElementById("taricAdditionalCode").attr("value") mustBe empty
      }

      "display empty input with label for NAC" in {
        view
          .getElementById("nationalAdditionalCode-label")
          .text() mustBe "declaration.itemType.nationalAdditionalCode.header"
        view
          .getElementById("nationalAdditionalCode-hint")
          .text() mustBe "declaration.itemType.nationalAdditionalCode.header.hint"
        view.getElementById("nationalAdditionalCode").attr("value") mustBe empty
      }

      "display empty input with label for Statistical Value" in {
        view.getElementById("statisticalValue-label").text() mustBe "declaration.itemType.statisticalValue.header"
        view.getElementById("statisticalValue-hint").text() mustBe "declaration.itemType.statisticalValue.header.hint"
        view.getElementById("statisticalValue").attr("value") mustBe empty
      }

      "display empty input with label for CUS" in {
        view.getElementById("cusCode-label").text() mustBe "declaration.itemType.cusCode.header"
        view.getElementById("cusCode-hint").text() mustBe "declaration.itemType.cusCode.header.hint"
        view.getElementById("cusCode").attr("value") mustBe empty
      }

      "display empty input with label for UN Dangerous Goods Code" in {
        view
          .getElementById("unDangerousGoodsCode-label")
          .text() mustBe "declaration.itemType.unDangerousGoodsCode.header"
        view
          .getElementById("unDangerousGoodsCode-hint")
          .text() mustBe "declaration.itemType.unDangerousGoodsCode.header.hint"
        view.getElementById("unDangerousGoodsCode").attr("value") mustBe empty
      }

      "display two 'Add' buttons" in {

        view.getElementsByAttributeValue("name", "AddField").size() must be(2)
      }

      "display 'Back' button that links to 'fiscal-information' page" in {

        val backButton = createView().getElementById("link-back")

        backButton.text() mustBe "site.back"
        backButton.getElementById("link-back") must haveHref(
          controllers.declaration.routes.CommodityDetailsController.displayPage(Mode.Normal, itemId = "itemId")
        )
      }

      "display 'Save and continue' button" in {
        val view = createView()
        val saveButton = view.getElementById("submit")
        saveButton.text() mustBe "site.save_and_continue"
      }

      "display 'Save and return' button" in {
        val view = createView()
        val saveButton = view.getElementById("submit_and_return")
        saveButton.text() mustBe "site.save_and_come_back_later"
      }
    }

    "used for Simplified Declaration journey" should {

      "display same page title as header" in {
        val viewWithMessage = createView(messages = realMessagesApi.preferred(request))
        viewWithMessage.title() must include(viewWithMessage.getElementsByTag("h1").text())
      }

      val view = createView()

      "display section header" in {
        view.getElementById("section-header").text() must include("supplementary.items")
      }

      "display empty input with label for TARIC" in {
        view
          .getElementById("taricAdditionalCode-label")
          .text() mustBe "declaration.itemType.taricAdditionalCodes.header"
        view
          .getElementById("taricAdditionalCode-hint")
          .text() mustBe "declaration.itemType.taricAdditionalCodes.header.hint"
        view.getElementById("taricAdditionalCode").attr("value") mustBe empty
      }

      "display empty input with label for NAC" in {
        view
          .getElementById("nationalAdditionalCode-label")
          .text() mustBe "declaration.itemType.nationalAdditionalCode.header"
        view
          .getElementById("nationalAdditionalCode-hint")
          .text() mustBe "declaration.itemType.nationalAdditionalCode.header.hint"
        view.getElementById("nationalAdditionalCode").attr("value") mustBe empty
      }

      "display empty input with label for Statistical Value" in {
        view.getElementById("statisticalValue-label").text() mustBe "declaration.itemType.statisticalValue.header"
        view.getElementById("statisticalValue-hint").text() mustBe "declaration.itemType.statisticalValue.header.hint"
        view.getElementById("statisticalValue").attr("value") mustBe empty
      }

      "display empty input with label for CUS" in {
        view.getElementById("cusCode-label").text() mustBe "declaration.itemType.cusCode.header"
        view.getElementById("cusCode-hint").text() mustBe "declaration.itemType.cusCode.header.hint"
        view.getElementById("cusCode").attr("value") mustBe empty
      }

      "display empty input with label for UN Dangerous Goods Code" in {
        view
          .getElementById("unDangerousGoodsCode-label")
          .text() mustBe "declaration.itemType.unDangerousGoodsCode.header"
        view
          .getElementById("unDangerousGoodsCode-hint")
          .text() mustBe "declaration.itemType.unDangerousGoodsCode.header.hint"
        view.getElementById("unDangerousGoodsCode").attr("value") mustBe empty
      }

      "display two 'Add' buttons" in {

        view.getElementsByAttributeValue("name", "AddField").size() must be(2)
      }

      "display 'Back' button that links to 'commodity-details' page" in {

        val backButton = createView().getElementById("link-back")

        backButton.text() mustBe "site.back"
        backButton.getElementById("link-back") must haveHref(
          controllers.declaration.routes.CommodityDetailsController.displayPage(Mode.Normal, itemId = "itemId")
        )
      }

      "display 'Save and continue' button" in {
        val view = createView()
        val saveButton = view.getElementById("submit")
        saveButton.text() mustBe "site.save_and_continue"
      }

      "display 'Save and return' button" in {
        val view = createView()
        val saveButton = view.getElementById("submit_and_return")
        saveButton.text() mustBe "site.save_and_come_back_later"
      }
    }

    "used for Supplementary Declaration journey" should {

      "display page title" in {

        createView()
          .getElementById("title")
          .text() mustBe "declaration.itemType.title"
      }

      "display empty input with label for TARIC" in {

        val view = createView()

        view
          .getElementById("taricAdditionalCode-label")
          .text() mustBe "declaration.itemType.taricAdditionalCodes.header"
        view
          .getElementById("taricAdditionalCode-hint")
          .text() mustBe "declaration.itemType.taricAdditionalCodes.header.hint"
        view.getElementById("taricAdditionalCode").attr("value") mustBe empty
      }

      "display empty input with label for NAC" in {

        val view = createView()

        view
          .getElementById("nationalAdditionalCode-label")
          .text() mustBe "declaration.itemType.nationalAdditionalCode.header"
        view
          .getElementById("nationalAdditionalCode-hint")
          .text() mustBe "declaration.itemType.nationalAdditionalCode.header.hint"
        view.getElementById("nationalAdditionalCode").attr("value") mustBe empty
      }

      "display empty input with label for CUS" in {

        val view = createView()

        view.getElementById("cusCode-label").text() mustBe "declaration.itemType.cusCode.header"
        view.getElementById("cusCode-hint").text() mustBe "declaration.itemType.cusCode.header.hint"
        view.getElementById("cusCode").attr("value") mustBe empty
      }

      "display empty input with label for Statistical Value" in {

        val view = createView()

        view.getElementById("statisticalValue-label").text() mustBe "declaration.itemType.statisticalValue.header"
        view.getElementById("statisticalValue-hint").text() mustBe "declaration.itemType.statisticalValue.header.hint"
        view.getElementById("statisticalValue").attr("value") mustBe empty
      }

      "display two 'Add' buttons" in {

        createView().getElementsByAttributeValue("name", "AddField").size() must be(2)
      }

      "display 'Back' button that links to 'commodity-details' page" in {

        val backButton =
          createView().getElementById("link-back")

        backButton.text() mustBe "site.back"
        backButton.getElementById("link-back") must haveHref(
          controllers.declaration.routes.CommodityDetailsController.displayPage(Mode.Normal, itemId = "itemId")
        )
      }

      "display 'Save and continue' button" in {
        val view = createView()
        val saveButton = view.getElementById("submit")
        saveButton.text() mustBe "site.save_and_continue"
      }

      "display 'Save and return' button" in {
        val view = createView()
        val saveButton = view.getElementById("submit_and_return")
        saveButton.text() mustBe "site.save_and_come_back_later"
      }
    }
  }

  "Item Type View with entered data" should {

    "used for Standard Declaration journey" should {

      "display data in CNC input" in {

        val itemType = ItemTypeForm(None, None, None, None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in Description input" in {

        val itemType = ItemTypeForm(None, Some(""), None, None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in CUS input" in {

        val itemType = ItemTypeForm(None, Some(""), Some("1234"), None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in UN Dangerous Goods Code input" in {

        val itemType = ItemTypeForm(None, Some(""), None, Some("1234"), "12345")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in Statistical Value input" in {

        val itemType = ItemTypeForm(None, Some(""), None, None, "12345")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      def assertViewDataEntered(view: Document, itemType: ItemTypeForm): Unit = {
        view.getElementById("cusCode").attr("value") must equal(itemType.cusCode.getOrElse(""))
        view.getElementById("unDangerousGoodsCode").attr("value") must equal(itemType.unDangerousGoodsCode.getOrElse(""))
        view.getElementById("statisticalValue").attr("value") must equal(itemType.statisticalValue)
      }
    }

    "used for Simplified Declaration journey" should {

      "display data in CNC input" in {

        val itemType = ItemTypeForm(None, None, None, None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in Description input" in {

        val itemType = ItemTypeForm(None, Some(""), None, None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in CUS input" in {

        val itemType = ItemTypeForm(None, Some(""), Some("1234"), None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in UN Dangerous Goods Code input" in {

        val itemType = ItemTypeForm(None, Some(""), None, Some("1234"), "12345")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in Statistical Value input" in {

        val itemType = ItemTypeForm(None, Some(""), None, None, "12345")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      def assertViewDataEntered(view: Document, itemType: ItemTypeForm): Unit = {
        view.getElementById("cusCode").attr("value") must equal(itemType.cusCode.getOrElse(""))
        view.getElementById("unDangerousGoodsCode").attr("value") must equal(itemType.unDangerousGoodsCode.getOrElse(""))
        view.getElementById("statisticalValue").attr("value") must equal(itemType.statisticalValue)
      }
    }

    "used for Supplementary Declaration journey" should {

      "display data in CNC input" in {

        val itemType = ItemTypeForm(None, None, None, None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in Description input" in {

        val itemType = ItemTypeForm(None, Some(""), None, None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in CUS input" in {

        val itemType = ItemTypeForm(None, Some(""), Some("1234"), None, "")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      "display data in Statistical Value input" in {

        val itemType = ItemTypeForm(None, Some(""), None, None, "12345")
        val view = createView(form = ItemTypeForm.form().fill(itemType))

        assertViewDataEntered(view, itemType)
      }

      def assertViewDataEntered(view: Document, itemType: ItemTypeForm): Unit = {
        view.getElementById("cusCode").attr("value") must equal(itemType.cusCode.getOrElse(""))
        view.getElementById("statisticalValue").attr("value") must equal(itemType.statisticalValue)
      }
    }

  }

}
