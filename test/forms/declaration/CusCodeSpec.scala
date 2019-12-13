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

package forms.declaration

import forms.declaration.CusCode._
import org.scalatest.{MustMatchers, WordSpec}
import play.api.data.FormError
import play.api.libs.json.{JsObject, JsString}

class CusCodeSpec extends WordSpec with MustMatchers {

  def formData(hasCode: String, code: Option[String]) =
    JsObject(Map(hasCusCodeKey -> JsString(hasCode), cusCodeKey -> JsString(code.getOrElse(""))))

  "CUSCode mapping" should {

    "return form with errors" when {
      "provided with code too long" in {
        val form = CusCode.form.bind(formData("Yes", Some("ABCD12345")))

        form.errors mustBe Seq(FormError(cusCodeKey, "declaration.cusCode.error.length"))
      }

      "provided with code too short" in {
        val form = CusCode.form.bind(formData("Yes", Some("ABCD123")))

        form.errors mustBe Seq(FormError(cusCodeKey, "declaration.cusCode.error.length"))
      }

      "provided with non-alphanumeric code" in {
        val form = CusCode.form.bind(formData("Yes", Some("!2345678")))

        form.errors mustBe Seq(FormError(cusCodeKey, "declaration.cusCode.error.specialCharacters"))
      }

      "provided with no code when user said yes" in {
        val form = CusCode.form.bind(formData("Yes", None))

        form.errors mustBe Seq(FormError(cusCodeKey, "declaration.cusCode.error.empty"))
      }
    }

    "return form without errors" when {
      "provided with valid input when user said Yes" in {
        val form = CusCode.form.bind(formData("Yes", Some("1234ABCD")))

        form.hasErrors must be(false)
      }

      "provided with no input when user said No" in {
        val form = CusCode.form.bind(formData("No", None))

        form.hasErrors must be(false)
      }

    }
  }
}