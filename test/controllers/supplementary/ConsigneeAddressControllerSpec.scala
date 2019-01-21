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

package controllers.supplementary

import base.CustomExportsBaseSpec
import base.ExportsTestData._
import forms.supplementary.AddressAndIdentification
import play.api.test.Helpers._

class ConsigneeAddressControllerSpec extends CustomExportsBaseSpec {

  val uri = uriWithContextPath("/declaration/supplementary/consignee-address")

  "Consignee address controller" should {
    "display consignee address form" in {
      authorizedUser()
      withCaching[AddressAndIdentification](None)

      val result = route(app, getRequest(uri)).get
      val stringResult = contentAsString(result)

      status(result) must be(OK)
      stringResult must include(messages("supplementary.consignee.title"))
      stringResult must include(messages("supplementary.consignee.title.hint"))
      stringResult must include(messages("supplementary.eori"))
      stringResult must include(messages("supplementary.fullName"))
      stringResult must include(messages("supplementary.addressLine"))
      stringResult must include(messages("supplementary.townOrCity"))
      stringResult must include(messages("supplementary.postCode"))
      stringResult must include(messages("supplementary.country"))
    }

    "validate form - incorrect values" in {
      authorizedUser()
      withCaching[AddressAndIdentification](None)

      val result = route(app, postRequest(uri, incorrectAddress)).get
      val stringResult = contentAsString(result)

      stringResult must include(messages("supplementary.eori.error"))
      stringResult must include(messages("supplementary.fullName.error"))
      stringResult must include(messages("supplementary.addressLine.error"))
      stringResult must include(messages("supplementary.townOrCity.error"))
      stringResult must include(messages("supplementary.postCode.error"))
      stringResult must include(messages("supplementary.country.error"))
    }

    "validate form - optional fields" in {
      authorizedUser()
      withCaching[AddressAndIdentification](None)

      val result = route(app, postRequest(uri, emptyAddress)).get
      val header = result.futureValue.header

      status(result) must be(SEE_OTHER)
      header.headers.get("Location") must be(
        Some("/customs-declare-exports/declaration/supplementary/additional-actors")
      )
    }

    "validate form - correct values" in {
      authorizedUser()
      withCaching[AddressAndIdentification](None)

      val result = route(app, postRequest(uri, correctAddress)).get
      val header = result.futureValue.header

      status(result) must be(SEE_OTHER)
      header.headers.get("Location") must be(
        Some("/customs-declare-exports/declaration/supplementary/additional-actors")
      )
    }
  }
}