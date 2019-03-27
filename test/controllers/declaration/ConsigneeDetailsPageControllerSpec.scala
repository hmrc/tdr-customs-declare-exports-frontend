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

package controllers.declaration

import base.CustomExportsBaseSpec
import forms.Choice
import forms.Choice.choiceId
import forms.declaration.{Address, ConsigneeDetails, EntityDetails}
import forms.declaration.ConsigneeDetailsSpec._
import play.api.test.Helpers._

class ConsigneeDetailsPageControllerSpec extends CustomExportsBaseSpec {

  private val uri = uriWithContextPath("/declaration/consignee-details")

  before {
    authorizedUser()
    withCaching[ConsigneeDetails](None)
    withCaching[Choice](Some(Choice(Choice.AllowedChoiceValues.SupplementaryDec)), choiceId)
  }

  "Consignee Details Controller on GET" should {

    "return 200 status code" in {
      val result = route(app, getRequest(uri)).get

      status(result) must be(OK)
    }

    "read item from cache and display it" in {

      val cachedData = ConsigneeDetails(EntityDetails(Some("12345"), Some(Address("Spiderman", "Test Street", "Leeds", "LS18BN", "Germany"))))
      withCaching[ConsigneeDetails](Some(cachedData), "ConsigneeDetails")

      val result = route(app, getRequest(uri)).get
      val page = contentAsString(result)

      status(result) must be(OK)
      page must include("12345")
      page must include("Spiderman")
      page must include("Test Street")
      page must include("Leeds")
      page must include("LS18BN")
      page must include("Germany")
    }
  }

  "Consignee Details Controller on POST" should {

    "validate request and redirect - both EORI and business details are empty" in {

      val result = route(app, postRequest(uri, emptyConsigneeDetailsJSON)).get

      status(result) must be(BAD_REQUEST)
    }

    "validate request and redirect - only EORI provided" in {

      val result = route(app, postRequest(uri, correctConsigneeDetailsEORIOnlyJSON)).get
      val header = result.futureValue.header

      status(result) must be(SEE_OTHER)
      header.headers.get("Location") must be(Some("/customs-declare-exports/declaration/declarant-details"))
    }

    "validate request and redirect - only address provided" in {

      val result = route(app, postRequest(uri, correctConsigneeDetailsAddressOnlyJSON)).get
      val header = result.futureValue.header

      status(result) must be(SEE_OTHER)
      header.headers.get("Location") must be(Some("/customs-declare-exports/declaration/declarant-details"))
    }

    "validate request and redirect - all values provided" in {

      val result = route(app, postRequest(uri, correctConsigneeDetailsJSON)).get
      val header = result.futureValue.header

      status(result) must be(SEE_OTHER)
      header.headers.get("Location") must be(Some("/customs-declare-exports/declaration/declarant-details"))
    }
  }
}