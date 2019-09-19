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

package models.declaration.notifications

import java.time.LocalDateTime

import models.declaration.submissions.SubmissionStatus
import play.api.libs.json.Json

case class Notification(
  conversationId: String,
  mrn: String,
  dateTimeIssued: LocalDateTime,
  functionCode: String,
  nameCode: Option[String],
  errors: Seq[NotificationError],
  payload: String
) extends Ordered[Notification] {

  def compare(that: Notification): Int =
    if (this.dateTimeIssued == that.dateTimeIssued) 0
    else if (this.dateTimeIssued.isAfter(that.dateTimeIssued)) 1
    else -1

  val status: SubmissionStatus = SubmissionStatus.retrieve(this.functionCode, this.nameCode)

  val isStatusRejected: Boolean = status == SubmissionStatus.Rejected
}

object Notification {
  implicit val format = Json.format[Notification]
}
