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
import forms.supplementary.Document
import play.api.libs.json.{JsBoolean, JsObject, JsString, JsValue}
import play.api.test.Helpers._

class AddDocumentsControllerSpec extends CustomExportsBaseSpec {

  val uri = uriWithContextPath("/declaration/supplementary/add-document")

  "AddDocument" should {
    "return 200 with a success" in {
      authorizedUser()
      withCaching[Document](None)

      val result = route(app, getRequest(uri)).get
      status(result) must be(OK)
    }

    "display add document form" in {
      authorizedUser()
      withCaching[Document](None)

      val result = route(app, getRequest(uri)).get
      val stringResult = contentAsString(result)

      status(result) must be(OK)
      stringResult must include(messages("supplementary.addDocument.title"))
      stringResult must include(messages("supplementary.addDocument.hint"))
      stringResult must include(messages("supplementary.addDocument.enterDocumentTypeOfCode"))
      stringResult must include(messages("supplementary.addDocument.identifier"))
      stringResult must include(messages("supplementary.addDocument.status"))
      stringResult must include(messages("supplementary.addDocument.issuingAuthority"))
      stringResult must include(messages("supplementary.addDocument.dateOfValidity"))
      stringResult must include(messages("supplementary.addDocument.measurementUnitAndQualifier"))
      stringResult must include(messages("supplementary.addDocument.checkbox"))
    }

    "validate form - empty form" in {
      pending
      authorizedUser()
      withCaching[Document](None)

      val emptyForm: JsValue = JsObject(
        Map(
          "enterDocumentTypeCode" -> JsString(""),
          "identifier" -> JsString(""),
          "status" -> JsString(""),
          "issuingAuthority" -> JsString(""),
          "dateOfValidity" -> JsString(""),
          "measurementUnitAndQualifier" -> JsString(""),
          "additionalInformation" -> JsBoolean(false)
        )
      )

      val result = route(app, postRequest(uri, emptyForm)).get
      val header = result.futureValue.header

      status(result) mustBe (SEE_OTHER)
      header.headers.get("Location") must be(Some(""))

    }

    "validate form - correct values" in {
      pending
      authorizedUser()
      withCaching[Document](None)

      val correctForm: JsValue = JsObject(
        Map(
          "enterDocumentTypeCode" -> JsString(""),
          "identifier" -> JsString(""),
          "status" -> JsString(""),
          "issuingAuthority" -> JsString(""),
          "dateOfValidity" -> JsString(""),
          "measurementUnitAndQualifier" -> JsString(""),
          "additionalInformation" -> JsBoolean(false)
        )
      )

      val result = route(app, postRequest(uri, correctForm)).get
      val header = result.futureValue.header

      status(result) mustBe (SEE_OTHER)
      header.headers.get("Location") must be(Some(""))
    }

  }
}