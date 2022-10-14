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

package views.declaration.addtionalDocuments

import base.Injector
import controllers.declaration.routes.AdditionalDocumentsController
import forms.declaration.additionaldocuments.AdditionalDocument.form
import models.Mode.Normal
import models.declaration.ExportDeclarationTestData.declaration
import models.requests.JourneyRequest
import org.jsoup.nodes.Document
import views.declaration.spec.UnitViewSpec
import views.html.declaration.additionalDocuments.additional_document_change
import views.tags.ViewTest

@ViewTest
class AdditionalDocumentChangeViewSpec extends UnitViewSpec with Injector {

  val documentId = "1.2131231"

  val page = instanceOf[additional_document_change]

  def createView(implicit request: JourneyRequest[_]): Document =
    page(Normal, itemId, documentId, form(declaration))(request, messages)

  "additional_document_change view on empty page" should {
    onEveryDeclarationJourney() { implicit request =>
      "display 'Back' button that links to summary page" in {
        val backButton = createView.getElementById("back-link")
        backButton must containMessage(backToPreviousQuestionCaption)
        backButton must haveHref(AdditionalDocumentsController.displayPage(Normal, itemId))
      }
    }
  }
}
