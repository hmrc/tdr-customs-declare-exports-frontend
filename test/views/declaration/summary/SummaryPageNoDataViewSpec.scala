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

package views.declaration.summary

import services.cache.ExportsTestData
import unit.tools.Stubs
import views.declaration.spec.UnitViewSpec
import views.html.declaration.summary.summary_page_no_data

class SummaryPageNoDataViewSpec extends UnitViewSpec with Stubs with ExportsTestData {

  val summaryPageNoData = new summary_page_no_data(mainTemplate)
  val view = summaryPageNoData()(request, messages)

  "Summary page no data" should {

    "display correct page title" in {

      view.getElementById("title").text() mustBe messages("declaration.summary.noData.header")
    }

    "display correct hint" in {

      view.getElementById("hint").text() mustBe messages("declaration.summary.noData.header.secondary")
    }

    "display back link which redirect to start page" in {

      val backButton = view.getElementById("summary-back-link")

      backButton.text() mustBe messages("declaration.summary.noData.button")
      backButton must haveHref("/customs-declare-exports/start")
    }
  }
}