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

import controllers.declaration.OfficeOfExitController
import forms.Choice.AllowedChoiceValues.{StandardDec, SupplementaryDec}
import forms.declaration.officeOfExit.{OfficeOfExitStandard, OfficeOfExitSupplementary}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.OptionValues
import play.api.data.Form
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import unit.base.ControllerSpec
import views.html.declaration.{office_of_exit_standard, office_of_exit_supplementary}

class OfficeOfExitControllerSpec extends ControllerSpec with OptionValues {

  val mockOfficeOfExitSupplementaryPage = mock[office_of_exit_supplementary]
  val mockOfficeOfExitStandardPage = mock[office_of_exit_standard]

  val controller = new OfficeOfExitController(
    mockAuthAction,
    mockJourneyAction,
    stubMessagesControllerComponents(),
    mockOfficeOfExitSupplementaryPage,
    mockOfficeOfExitStandardPage,
    mockExportsCacheService
  )(ec)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    authorizedUser()
    when(mockOfficeOfExitSupplementaryPage.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(mockOfficeOfExitStandardPage.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    reset(mockOfficeOfExitSupplementaryPage)
    reset(mockOfficeOfExitStandardPage)
  }

  def checkSupplementaryViewInteractions(noOfInvocations: Int = 1): Unit =
    verify(mockOfficeOfExitSupplementaryPage, times(noOfInvocations)).apply(any())(any(), any())

  def checkStandardViewInteractions(noOfInvocations: Int = 1): Unit =
    verify(mockOfficeOfExitStandardPage, times(noOfInvocations)).apply(any())(any(), any())

  def theSupplementaryResponseForm: Form[OfficeOfExitSupplementary] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[OfficeOfExitSupplementary]])
    verify(mockOfficeOfExitSupplementaryPage).apply(captor.capture())(any(), any())
    captor.getValue
  }

  def theStandardResponseForm: Form[OfficeOfExitStandard] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[OfficeOfExitStandard]])
    verify(mockOfficeOfExitStandardPage).apply(captor.capture())(any(), any())
    captor.getValue
  }

  "Office of Exit controller for supplementary journey" should {

    "return 200 (OK)" when {

      "display page method is invoked and cache is empty" in {

        withNewCaching(aDeclaration(withChoice(SupplementaryDec)))

        val result = controller.displayForm()(getRequest())

        status(result) mustBe OK
        checkSupplementaryViewInteractions()

        theSupplementaryResponseForm.value mustBe empty
      }

      "display page method is invoked and cache contains data" in {

        val officeId = "officeId"
        withNewCaching(aDeclaration(withChoice(SupplementaryDec), withOfficeOfExit(officeId)))

        val result = controller.displayForm()(getRequest())

        status(result) mustBe OK
        checkSupplementaryViewInteractions()

        theSupplementaryResponseForm.value.value.officeId mustBe officeId
      }
    }

    "return 400 (BAD_REQUEST)" when {

      "form is incorrect" in {

        withNewCaching(aDeclaration(withChoice(SupplementaryDec)))

        val incorrectForm = Json.toJson(OfficeOfExitSupplementary("!@#$"))

        val result = controller.saveOffice()(postRequest(incorrectForm))

        status(result) mustBe BAD_REQUEST
        checkSupplementaryViewInteractions()
      }
    }

    "return 303 (SEE_OTHER)" when {

      "information provided by user are correct" in {

        withNewCaching(aDeclaration(withChoice(SupplementaryDec)))

        val correctForm = Json.toJson(OfficeOfExitSupplementary("officeId"))

        val result = controller.saveOffice()(postRequest(correctForm))

        status(result) mustBe SEE_OTHER
        checkSupplementaryViewInteractions(0)
      }
    }
  }

  "Office of Exit controller for standard journey" should {

    "return 200 (OK)" when {

      "display page method is invoked and cache is empty" in {

        withNewCaching(aDeclaration(withChoice(StandardDec)))

        val result = controller.displayForm()(getRequest())

        status(result) mustBe OK
        checkStandardViewInteractions()

        theStandardResponseForm.value mustBe empty
      }

      "display page method is invoked and cache contains data" in {

        val officeId = "officeId"
        val presentationOfficeId = Some("preOffice")
        val circumstancesCode = "Yes"
        withNewCaching(
          aDeclaration(
            withChoice(StandardDec),
            withOfficeOfExit(officeId, presentationOfficeId, Some(circumstancesCode))
          )
        )

        val result = controller.displayForm()(getRequest())

        status(result) mustBe OK
        checkStandardViewInteractions()

        theStandardResponseForm.value.value.officeId mustBe officeId
        theStandardResponseForm.value.value.presentationOfficeId mustBe presentationOfficeId
        theStandardResponseForm.value.value.circumstancesCode mustBe circumstancesCode
      }
    }

    "return 400 (BAD_REQUEST)" when {

      "form is incorrect" in {

        withNewCaching(aDeclaration(withChoice(StandardDec)))

        val incorrectForm = Json.toJson(OfficeOfExitStandard("!@#$", None, "wrong"))

        val result = controller.saveOffice()(postRequest(incorrectForm))

        status(result) mustBe BAD_REQUEST
        checkStandardViewInteractions()
      }
    }

    "return 303 (SEE_OTHER)" when {

      "information provided by user are correct" in {

        withNewCaching(aDeclaration(withChoice(StandardDec)))

        val correctForm = Json.toJson(OfficeOfExitStandard("officeId", None, "Yes"))

        val result = controller.saveOffice()(postRequest(correctForm))

        status(result) mustBe SEE_OTHER
        checkStandardViewInteractions(0)
      }
    }
  }
}