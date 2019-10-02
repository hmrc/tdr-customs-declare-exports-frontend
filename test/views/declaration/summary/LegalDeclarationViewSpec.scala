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

import forms.declaration.LegalDeclaration
import views.declaration.spec.UnitViewSpec
import views.html.declaration.summary.legal_declaration

class LegalDeclarationViewSpec extends UnitViewSpec {

  private val emptyForm = LegalDeclaration.form()
  private val view = legal_declaration(emptyForm)

  "Legal Declaration View" should {

    val messages = realMessagesApi.preferred(request)

    "have header and translation for it" in {

      view.getElementsByClass("legal-declaration-heading").first().text() mustBe "legal.declaration.heading"
      messages must haveTranslationFor("legal.declaration.heading")
    }

    "have information about declaration" in {

      view.body must include("legal.declaration.info")
      messages must haveTranslationFor("legal.declaration.info")
    }

    "have legal declaration warning" in {

      view.getElementsByClass("legal-declaration-warning").first().text() mustBe "Warning legal.declaration.warning"
      messages must haveTranslationFor("legal.declaration.warning")
    }

    "have full name input" in {

      view.getElementById("fullName-label").text() mustBe "legal.declaration.fullName"
      messages must haveTranslationFor("legal.declaration.fullName")
      messages must haveTranslationFor("legal.declaration.fullName.empty")
      messages must haveTranslationFor("legal.declaration.fullName.short")
      messages must haveTranslationFor("legal.declaration.fullName.long")
      messages must haveTranslationFor("legal.declaration.fullName.error")
    }

    "have job role input" in {

      view.getElementById("jobRole-label").text() mustBe "legal.declaration.jobRole"
      messages must haveTranslationFor("legal.declaration.jobRole")
      messages must haveTranslationFor("legal.declaration.jobRole.empty")
      messages must haveTranslationFor("legal.declaration.jobRole.short")
      messages must haveTranslationFor("legal.declaration.jobRole.long")
      messages must haveTranslationFor("legal.declaration.jobRole.error")
    }

    "have email input" in {

      view.getElementById("email-label").text() mustBe "legal.declaration.email"
      messages must haveTranslationFor("legal.declaration.email")
      messages must haveTranslationFor("legal.declaration.email.empty")
      messages must haveTranslationFor("legal.declaration.email.long")
      messages must haveTranslationFor("legal.declaration.email.error")
    }

    "have confirmation box" in {

      view.getElementById("confirmation-label").text() mustBe "legal.declaration.confirmation"
      messages must haveTranslationFor("legal.declaration.confirmation")
      messages must haveTranslationFor("legal.declaration.confirmation.missing")
    }
  }
}