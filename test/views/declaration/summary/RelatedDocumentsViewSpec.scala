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

import forms.declaration.Document
import services.cache.ExportsTestData
import views.declaration.spec.UnitViewSpec
import views.html.declaration.summary.related_documents

class RelatedDocumentsViewSpec extends UnitViewSpec with ExportsTestData {

  "Related documents" should {

    "display row with No value with change button" when {

      "documents are empty" in {

        val view = related_documents(Seq.empty)(messages, journeyRequest())

        view.getElementById("previous-documents-label").text() mustBe messages("declaration.summary.transaction.previousDocuments")
        view.getElementById("previous-documents").text() mustBe messages("site.no")
        view.getElementById("previous-documents-change").text() mustBe messages("site.change")
        view.getElementById("previous-documents-change") must haveHref(controllers.declaration.routes.PreviousDocumentsController.displayPage())
      }
    }

    "display documents with change button" when {

      "documents exists" in {

        val data = Seq(Document("X", "325", "123456", None), Document("X", "271", "654321", None))

        val view = related_documents(data)(messages, journeyRequest())

        view.getElementById("previous-documents").text() mustBe messages("declaration.summary.transaction.previousDocuments")
        view.getElementById("previous-documents-type").text() mustBe messages("declaration.summary.transaction.previousDocuments.type")
        view.getElementById("previous-documents-reference").text() mustBe messages("declaration.summary.transaction.previousDocuments.reference")
        view.getElementById("previous-document-0-type").text() mustBe "Proforma Invoice - 325"
        view.getElementById("previous-document-0-reference").text() mustBe "123456"
        view.getElementById("previous-document-0-change").text() mustBe messages("site.change")
        view.getElementById("previous-document-0-change") must haveHref(controllers.declaration.routes.PreviousDocumentsController.displayPage())
        view.getElementById("previous-document-1-type").text() mustBe "Packing List - 271"
        view.getElementById("previous-document-1-reference").text() mustBe "654321"
        view.getElementById("previous-document-1-change").text() mustBe messages("site.change")
        view.getElementById("previous-document-1-change") must haveHref(controllers.declaration.routes.PreviousDocumentsController.displayPage())
      }
    }
  }
}