/*
 * Copyright 2018 HM Revenue & Customs
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

package forms.supplementary

import org.scalatest.{MustMatchers, WordSpec}

class DeclarationTypeSpec extends WordSpec with MustMatchers {

  "toMetadataProperties" should {
    "contain key from wco-dec domain" in {
      val userInput = DeclarationType(
        declarationType = "EX",
        additionalDeclarationType = "Y"
      )
      val expectedPropertiesKey = "declaration.typeCode"

      val properties = userInput.toMetadataProperties()

      properties.keySet must contain(expectedPropertiesKey)
    }

    "contain value from DeclarationType fields combined" in {
      val userInput = DeclarationType(
        declarationType = "EX",
        additionalDeclarationType = "Y"
      )
      val expectedPropertiesValue = "EXY"

      val properties = userInput.toMetadataProperties()

      properties.values must contain(expectedPropertiesValue)
    }
  }

}