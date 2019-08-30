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
import helpers.views.declaration.ConfirmationMessages
import play.api.mvc.Flash
import play.twirl.api.Html
import views.html.declaration.submission_confirmation_page
import views.declaration.spec.ViewSpec
import views.tags.ViewTest

@ViewTest
class SubmissionConfirmationPageViewSpec extends ViewSpec with ConfirmationMessages {

  private val confirmationPage = app.injector.instanceOf[submission_confirmation_page]
  private def createView(): Html = confirmationPage()(fakeRequest, flash, messages)

  "Confirmation Page View on empty page" should {

    "display page title" in {

      createView().select("title").text() must be(messages(title))
    }

    "display header" in {

      val view = createView()

      view.select("article>div.govuk-box-highlight>h1").text() must be(messages(header))
      view.select("article>div.govuk-box-highlight>p").text() must be("-")
    }

    "display declaration status" in {

      createView().select("article>p:nth-child(2)").text() must be(messages(information))
    }

    "display information about future steps" in {

      val view = createView()

      view.select("article>h1").text() must be(messages(whatHappensNext))
      view.select("article>p:nth-child(4)").text() must be(messages(explanation) + " " + messages(explanationLink))
    }

    "display an 'Check your notification status in the dashboard' empty link without conversationId" in {

      val view = createView()

      val link = view.select("article>p:nth-child(4)>a")
      link.text() must be(messages(explanationLink))
      link.attr("href") must be("/customs-declare-exports/submissions")
    }

    "display a 'Submit another declaration' button that links to 'What do you want to do ?' page" in {

      val view = createView()

      val button = view.select("article>div.section>a")
      button.text() must be(messages(submitAnother))
      button.attr("href") must be("/customs-declare-exports/choice")
    }
  }

  "Confirmation Page View when filled" should {

    "display LRN and proper link to submissions" in {

      val view = confirmationPage()(fakeRequest, new Flash(Map("LRN" -> "12345")), messages)

      view.select("article>div.govuk-box-highlight>p").text() must be("12345")

      val link = view.select("article>p:nth-child(4)>a")
      link.text() must be(messages(explanationLink))
      link.attr("href") must be("/customs-declare-exports/submissions")
    }
  }
}
