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

import forms.declaration.officeOfExit.OfficeOfExitSupplementary
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsObject, JsString, JsValue}

class OfficeOfExitSupplementarySpec extends WordSpec with MustMatchers {
  import OfficeOfExitSupplementarySpec._

  "Method toMetadataProperties" should {
    "return proper Metadata Properties" in {
      val officeOfExit = correctOfficeOfExit
      val expectedMetadataProperties: Map[String, String] = Map("declaration.exitOffice.id" -> officeOfExit.officeId)

      officeOfExit.toMetadataProperties() must equal(expectedMetadataProperties)
    }
  }

}

object OfficeOfExitSupplementarySpec {
  val correctOfficeOfExit = OfficeOfExitSupplementary(officeId = "123qwe12")
  val emptyOfficeOfExit = OfficeOfExitSupplementary(officeId = "")
  val incorrectOfficeOfExit = OfficeOfExitSupplementary(officeId = "office")

  val correctOfficeOfExitJSON: JsValue = JsObject(Map("officeId" -> JsString("123qwe12")))
  val emptyOfficeOfExitJSON: JsValue = JsObject(Map("officeId" -> JsString("")))
  val incorrectOfficeOfExitJSON: JsValue = JsObject(Map("officeId" -> JsString("office")))
}