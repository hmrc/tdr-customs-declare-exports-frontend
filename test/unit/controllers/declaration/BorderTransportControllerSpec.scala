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

package unit.controllers.declaration

import controllers.declaration.BorderTransportController
import forms.declaration.BorderTransport
import forms.declaration.TransportCodes.IMOShipIDNumber
import models.{DeclarationType, ExportsDeclaration, Mode}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import unit.base.ControllerSpec
import views.html.declaration.border_transport
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

class BorderTransportControllerSpec extends ControllerSpec {

  val borderTransportPage = mock[border_transport]

  val controller = new BorderTransportController(
    mockAuthAction,
    mockJourneyAction,
    navigator,
    mockExportsCacheService,
    stubMessagesControllerComponents(),
    borderTransportPage
  )(ec)

  override def beforeEach(): Unit = {
    super.beforeEach()
    authorizedUser()
    when(borderTransportPage.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  def borderTransportController(declarationFactory: () => ExportsDeclaration): Unit = {
    "return 200 (OK)" when {

      "display page method is invoked and cache is empty" in {
        withNewCaching(declarationFactory())

        val result = controller.displayPage(Mode.Normal)(getRequest())

        status(result) must be(OK)
      }

      "display page method is invoked and cache is not empty" in {
        withNewCaching(aDeclarationAfter(declarationFactory(), withBorderTransport()))

        val result = controller.displayPage(Mode.Normal)(getRequest())

        status(result) must be(OK)
      }
    }

    "return 400 (BAD_REQUEST)" when {

      "form contains incorrect values" in {
        withNewCaching(declarationFactory())

        val incorrectForm = Json.toJson(BorderTransport(Some("incorrect"), "", ""))

        val result = controller.submitForm(Mode.Normal)(postRequest(incorrectForm))

        status(result) must be(BAD_REQUEST)
      }
    }

  }

  "Transport Details Controller" when {
    "we are on supplementary declaration journey" should {
      def declarationFactory() = aDeclaration(withType(DeclarationType.SUPPLEMENTARY))
      behave like borderTransportController(declarationFactory)

      "return 303 (SEE_OTHER) to Containers" when {
        "valid options are selected" in {
          withNewCaching(declarationFactory())

          val correctForm =
            Json.toJson(BorderTransport(Some("United Kingdom"), IMOShipIDNumber, "correct"))

          val result = controller.submitForm(Mode.Draft)(postRequest(correctForm))

          await(result) mustBe aRedirectToTheNextPage
          thePageNavigatedTo mustBe controllers.declaration.routes.TransportContainerController
            .displayContainerSummary(Mode.Draft)
        }

      }
    }
    "we are on standard declaration journey" should {
      def declarationFactory() = aDeclaration(withType(DeclarationType.STANDARD))
      behave like borderTransportController(declarationFactory)

      "return 303 (SEE_OTHER) to TransportPayment" when {
        "valid options are selected" in {
          withNewCaching(declarationFactory())

          val correctForm =
            Json.toJson(BorderTransport(Some("United Kingdom"), IMOShipIDNumber, "correct"))

          val result = controller.submitForm(Mode.Draft)(postRequest(correctForm))

          await(result) mustBe aRedirectToTheNextPage
          thePageNavigatedTo mustBe controllers.declaration.routes.TransportPaymentController
            .displayPage(Mode.Draft)
        }

      }
    }
    "we are on simplified declaration journey" should {
      def declarationFactory() = aDeclaration(withType(DeclarationType.SIMPLIFIED))
      behave like borderTransportController(declarationFactory)

      "return 303 (SEE_OTHER) to TransportPayment" when {
        "valid options are selected" in {
          withNewCaching(declarationFactory())

          val correctForm =
            Json.toJson(BorderTransport(Some("United Kingdom"), IMOShipIDNumber, "correct"))

          val result = controller.submitForm(Mode.Draft)(postRequest(correctForm))

          await(result) mustBe aRedirectToTheNextPage
          thePageNavigatedTo mustBe controllers.declaration.routes.TransportPaymentController
            .displayPage(Mode.Draft)
        }

      }
    }

  }
}
