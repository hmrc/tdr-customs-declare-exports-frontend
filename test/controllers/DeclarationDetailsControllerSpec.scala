/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import java.time.ZonedDateTime
import java.util.UUID

import scala.concurrent.Future

import base.ControllerWithoutFormSpec
import config.featureFlags.QueryNotificationMessageConfig
import models.declaration.notifications.Notification
import models.declaration.submissions.RequestType.SubmissionRequest
import models.declaration.submissions.{Action, Submission, SubmissionStatus}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import views.html.{declaration_details, declaration_information}

class DeclarationDetailsControllerSpec extends ControllerWithoutFormSpec with BeforeAndAfterEach {

  private val actionId = "actionId"
  private val notification = Notification(actionId, "mrn", ZonedDateTime.now, SubmissionStatus.UNKNOWN, Seq.empty)

  private val submission = Submission(
    uuid = UUID.randomUUID().toString,
    eori = "eori",
    lrn = "lrn",
    mrn = None,
    ducr = None,
    actions = Seq(Action(id = actionId, requestType = SubmissionRequest, requestTimestamp = ZonedDateTime.now))
  )

  private val queryNotificationMessageConfig = mock[QueryNotificationMessageConfig]
  private val declarationInformationPage = mock[declaration_information]
  private val declarationDetailsPage = mock[declaration_details]

  val controller = new DeclarationDetailsController(
    mockAuthAction,
    mockVerifiedEmailAction,
    mockCustomsDeclareExportsConnector,
    stubMessagesControllerComponents(),
    queryNotificationMessageConfig,
    declarationInformationPage,
    declarationDetailsPage
  )(ec)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    authorizedUser()
    when(declarationInformationPage.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(declarationDetailsPage.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit =
    reset(queryNotificationMessageConfig, declarationInformationPage, declarationDetailsPage, mockCustomsDeclareExportsConnector)

  "displayPage method of Declaration Details page" should {

    "return 200 (OK)" when {
      val submissionCaptor: ArgumentCaptor[Submission] = ArgumentCaptor.forClass(classOf[Submission])
      val notificationsCaptor: ArgumentCaptor[Seq[Notification]] = ArgumentCaptor.forClass(classOf[Seq[Notification]])

      "submission and notifications are provided for the Declaration" when {

        "QueryNotificationMessage feature flag is disabled" in {
          responsesToReturn(isQueryNotificationMessageEnabled = false)

          val result = controller.displayPage(actionId)(getRequest())
          status(result) mustBe OK

          verifyNoInteractions(declarationDetailsPage)

          verify(declarationInformationPage).apply(submissionCaptor.capture(), notificationsCaptor.capture())(any(), any())
          submissionCaptor.getValue mustBe submission
          notificationsCaptor.getValue mustBe List(notification)
        }

        "QueryNotificationMessage feature flag is enabled" in {
          responsesToReturn(isQueryNotificationMessageEnabled = true)

          val result = controller.displayPage(actionId)(getRequest())
          status(result) mustBe OK

          verifyNoInteractions(declarationInformationPage)

          verify(declarationDetailsPage).apply(submissionCaptor.capture(), notificationsCaptor.capture())(any(), any())
          submissionCaptor.getValue mustBe submission
          notificationsCaptor.getValue mustBe List(notification)
        }
      }

      "submission but no notifications are provided for the Declaration" when {

        "QueryNotificationMessage feature flag is disabled" in {
          responsesToReturn(isQueryNotificationMessageEnabled = false, List.empty)

          val result = controller.displayPage(actionId)(getRequest())
          status(result) mustBe OK

          verifyNoInteractions(declarationDetailsPage)

          verify(declarationInformationPage).apply(submissionCaptor.capture(), notificationsCaptor.capture())(any(), any())
          submissionCaptor.getValue mustBe submission
          notificationsCaptor.getValue mustBe List.empty
        }

        "QueryNotificationMessage feature flag is enabled" in {
          responsesToReturn(isQueryNotificationMessageEnabled = true, List.empty)

          val result = controller.displayPage(actionId)(getRequest())
          status(result) mustBe OK

          verifyNoInteractions(declarationInformationPage)

          verify(declarationDetailsPage).apply(submissionCaptor.capture(), notificationsCaptor.capture())(any(), any())
          submissionCaptor.getValue mustBe submission
          notificationsCaptor.getValue mustBe List.empty
        }
      }

      def responsesToReturn(
        isQueryNotificationMessageEnabled: Boolean,
        notifications: Seq[Notification] = List(notification)
      ): OngoingStubbing[Future[Seq[Notification]]] = {
        when(queryNotificationMessageConfig.isQueryNotificationMessageEnabled).thenReturn(isQueryNotificationMessageEnabled)
        when(mockCustomsDeclareExportsConnector.findSubmission(any())(any(), any())).thenReturn(Future.successful(Some(submission)))
        when(mockCustomsDeclareExportsConnector.findNotifications(any())(any(), any())).thenReturn(Future.successful(notifications))
      }
    }

    "return 303 (SEE_OTHER)" when {

      "there is no submission for the Declaration" in {

        when(mockCustomsDeclareExportsConnector.findSubmission(any())(any(), any())).thenReturn(Future.successful(None))

        val result = controller.displayPage(actionId)(getRequest())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get mustBe routes.SubmissionsController.displayListOfSubmissions().url

        verifyNoInteractions(declarationDetailsPage)
        verifyNoInteractions(declarationInformationPage)
      }
    }
  }
}