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

package unit.services.model

import java.time.LocalDateTime

import models.Pointer
import models.declaration.notifications.{Notification, NotificationError}
import models.declaration.submissions.SubmissionStatus
import play.api.i18n.Messages
import services.model.RejectionReason
import unit.base.UnitSpec
import org.mockito.BDDMockito.given
import org.mockito.ArgumentMatchers._

class RejectionReasonSpec extends UnitSpec {

  import services.model.RejectionReason._
  private val messages = mock[Messages]

  "Apply" should {

    "create correct error based on the list" in {

      val errorCode = "ErrorCode"
      val errorDescription = "Error description"
      val error = List(errorCode, errorDescription)

      RejectionReason.apply(error) mustBe RejectionReason(errorCode, errorDescription, None)
    }

    "throw an exception when input is incorrect" in {

      intercept[IllegalArgumentException](RejectionReason.apply(List.empty))
    }
  }

  "All Errors" should {
    "have 136 errors" in {

      allRejectedErrors.length mustBe 136
    }

    "contain correct values" in {

      allRejectedErrors must contain(RejectionReason("CDS40049", "Quota exhausted.", None))
      allRejectedErrors must contain(RejectionReason("CDS40051", "Quota blocked.", None))
      allRejectedErrors must contain(
        RejectionReason(
          "CDS12087",
          "Relation error: VAT Declaring Party Identification (D.E. 3/40), where mandated, must be supplied at either header or item.",
          None
        )
      )
      allRejectedErrors must contain(RejectionReason("CDS12108", "Obligation error: DUCR is mandatory on an Export Declaration.", None))
    }

    "correctly read multiline values" in {

      val expectedMessages =
        """Sequence error: The referred declaration does not comply with one of the following conditions:
          |- The AdditionalMessage.declarationReference must refer to an existing declaration (Declaration.reference),
          |- have been accepted,
          |- not be invalidated.""".stripMargin
      val expectedRejectionReason = RejectionReason("CDS12015", expectedMessages, None)

      allRejectedErrors must contain(expectedRejectionReason)
    }
  }

  "Get Error  Description" should {

    "correctly return error description" in {

      getErrorDescription("CDS12016") mustBe "Date error: Date of acceptance is not allowed."
    }

    "return Unknown error when error code is not in rejected errors" in {

      getErrorDescription("unknown code") mustBe "Unknown error"
    }
  }

  "Map from Notifications" should {
    "map to Rejected Reason" when {
      val nonRejectionNotification =
        Notification("convId", "mrn", LocalDateTime.now(), SubmissionStatus.ACCEPTED, Seq.empty, "")

      "list is empty" in {
        fromNotifications(Seq.empty)(messages) mustBe Seq.empty
      }

      "list doesn't contain rejected notification" in {
        fromNotifications(Seq(nonRejectionNotification))(messages) mustBe Seq.empty
      }

      "list contains rejected notification" when {
        "pointer is known" in {
          given(messages.isDefinedAt(anyString())).willReturn(true)
          val error = NotificationError("CDS12016", Some(Pointer("x.0.z")))
          val notification =
            Notification("actionId", "mrn", LocalDateTime.now(), SubmissionStatus.REJECTED, Seq(error), "")

          fromNotifications(Seq(notification))(messages) mustBe Seq(
            RejectionReason("CDS12016", "Date error: Date of acceptance is not allowed.", Some("field.x.$.z"))
          )
        }

        "pointer is unknown" in {
          given(messages.isDefinedAt(anyString())).willReturn(false)
          val error = NotificationError("CDS12016", Some(Pointer("x.0.z")))
          val notification =
            Notification("actionId", "mrn", LocalDateTime.now(), SubmissionStatus.REJECTED, Seq(error), "")

          fromNotifications(Seq(notification))(messages) mustBe Seq(
            RejectionReason("CDS12016", "Date error: Date of acceptance is not allowed.", None)
          )
        }

        "pointer is empty" in {
          val error = NotificationError("CDS12016", None)
          val notification =
            Notification("actionId", "mrn", LocalDateTime.now(), SubmissionStatus.REJECTED, Seq(error), "")

          fromNotifications(Seq(notification))(messages) mustBe Seq(
            RejectionReason("CDS12016", "Date error: Date of acceptance is not allowed.", None)
          )
        }
      }
    }
  }
}
