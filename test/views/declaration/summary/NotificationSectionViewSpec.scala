/*
 * Copyright 2020 HM Revenue & Customs
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

package views.declaration.summary

import java.time.{ZoneOffset, ZonedDateTime}

import base.Injector
import models.declaration.notifications.Notification
import models.declaration.submissions.SubmissionStatus
import services.cache.ExportsTestData
import views.ViewDates
import views.declaration.spec.UnitViewSpec
import views.html.declaration.summary.notifications_section

class NotificationSectionViewSpec extends UnitViewSpec with ExportsTestData with Injector {

  private val acceptedNotification =
    Notification("actionId", "SOME_MRN", ZonedDateTime.now(ZoneOffset.UTC), SubmissionStatus.ACCEPTED, Seq.empty, "payload")
  private val clearedNotification =
    Notification("actionId", "SOME_MRN", ZonedDateTime.now(ZoneOffset.UTC), SubmissionStatus.CLEARED, Seq.empty, "payload")

  val section = instanceOf[notifications_section]

  val view = section(Seq(acceptedNotification, clearedNotification))(messages, journeyRequest())

  "Accepted section" should {

    "have MRN" in {

      val row = view.getElementsByClass("mrn-row")
      row must haveSummaryKey(messages("declaration.summary.accepted.mrn"))
      row must haveSummaryValue("SOME_MRN")
    }

    "have Accepted" in {

      val row = view.getElementsByClass("accepted-row")
      row must haveSummaryKey(acceptedNotification.displayStatus)
      row must haveSummaryValue(ViewDates.submissionDateTimeFormatter.format(acceptedNotification.dateTimeIssuedInUK))
    }

    "have Cleared" in {

      val row = view.getElementsByClass("cleared-row")
      row must haveSummaryKey(clearedNotification.displayStatus)
      row must haveSummaryValue(ViewDates.submissionDateTimeFormatter.format(clearedNotification.dateTimeIssuedInUK))
    }
  }

}