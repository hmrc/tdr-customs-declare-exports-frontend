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

import forms.common.YesNoAnswer
import forms.declaration.Seal
import helpers.views.declaration.CommonMessages
import models.Mode
import models.declaration.Container
import org.jsoup.nodes.Document
import org.scalatest.MustMatchers
import play.api.data.Form
import unit.tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.transport_container_summary
import views.tags.ViewTest

@ViewTest
class TransportContainerSummaryViewSpec extends UnitViewSpec with Stubs with MustMatchers with CommonMessages {

  val containerId = "212374"
  val sealId = "76434574"
  val container = Container(containerId, Seq(Seal(sealId)))
  private val form: Form[YesNoAnswer] = YesNoAnswer.form()
  private val page = new transport_container_summary(mainTemplate)

  private def createView(form: Form[YesNoAnswer] = form, containers: Seq[Container] = Seq(container), showSeals: Boolean = true): Document =
    page(Mode.Normal, form, containers, showSeals)

  "Transport Containers Summary View" should {
    val view = createView()

    "display page title" in {
      view.getElementById("title").text() must be(messages("supplementary.transportInfo.containers.title"))
    }

    "display summary of container with seals" in {
      view.getElementById("containers-row0-container").text() must be(containerId)
      view.getElementById("containers-row0-seals").text() must be(sealId)
    }

    "display summary of container without seals" in {
      val view = createView(showSeals = false)

      view.getElementById("containers-row0-container").text() must be(containerId)
      view.getElementById("containers-row0-seals") must be(null)
    }

    "display summary of container with no seals" in {
      val view = createView(containers = Seq(Container(containerId, Seq.empty)))

      view.getElementById("containers-row0-container").text() must be(containerId)
      view.getElementById("containers-row0-seals").text() must be(messages("standard.seal.summary.noSeals"))
    }

    "display 'Back' button that links to 'transport details' page" in {
      val backLinkContainer = view.getElementById("link-back")

      backLinkContainer.text() must be(messages(backCaption))
      backLinkContainer.getElementById("link-back") must haveHref(controllers.declaration.routes.BorderTransportController.displayPage(Mode.Normal))
    }

    "display 'Save and continue' button on page" in {
      val saveButton = view.getElementById("submit")
      saveButton.text() must be(messages(saveAndContinueCaption))
    }

    "display 'Save and return' button on page" in {
      val saveAndReturnButton = view.getElementById("submit_and_return")
      saveAndReturnButton.text() must be(messages(saveAndReturnCaption))
    }
  }

  "Transport Containers Summary View for invalid input" should {

    "display error if nothing is entered" in {
      val view = createView(YesNoAnswer.form().bind(Map[String, String]()))

      view.select("#error-message-yesNo-input").text() must be(messages("error.yesNo.required"))
    }

  }
}
