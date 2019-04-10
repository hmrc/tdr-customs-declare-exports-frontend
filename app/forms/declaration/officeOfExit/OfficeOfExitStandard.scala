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

package forms.declaration.officeOfExit

import forms.MetadataPropertiesConvertable
import play.api.data.{Form, Forms}
import play.api.data.Forms.text
import play.api.libs.json.Json
import utils.validators.forms.FieldValidator._

case class OfficeOfExitStandard(officeId: String, presentationOfficeId: String, circumstancesCode: String)
    extends MetadataPropertiesConvertable {
  override def toMetadataProperties(): Map[String, String] =
    Map(
      "declaration.exitOffice.id" -> officeId,
      "declaration.presentationOffice.id" -> presentationOfficeId,
      "declaration.specificCircumstancesCode" -> circumstancesCodeForMetadata
    )

  import OfficeOfExitStandard.AllowedCircumstancesCodeAnswers.yes
  private val circumstancesCodeForMetadata: String = if (circumstancesCode == yes) "A20" else ""
}

object OfficeOfExitStandard {
  implicit val format = Json.format[OfficeOfExitStandard]

  object AllowedCircumstancesCodeAnswers {
    val yes = "Yes"
    val no = "No"
  }
  import AllowedCircumstancesCodeAnswers._

  val allowedValues: Seq[String] = Seq(yes, no)

  val mapping = Forms.mapping(
    "officeId" -> text()
      .verifying("declaration.officeOfExit.empty", nonEmpty)
      .verifying("declaration.officeOfExit.length", isEmpty or hasSpecificLength(8))
      .verifying("declaration.officeOfExit.specialCharacters", isEmpty or isAlphanumeric),
    "presentationOfficeId" -> text()
      .verifying("standard.officeOfExit.presentationOffice.empty", nonEmpty)
      .verifying("standard.officeOfExit.presentationOffice.length", isEmpty or hasSpecificLength(8))
      .verifying("standard.officeOfExit.presentationOffice.specialCharacters", isEmpty or isAlphanumeric),
    "circumstancesCode" -> text()
      .verifying("standard.officeOfExit.circumstancesCode.empty", nonEmpty)
      .verifying("standard.officeOfExit.circumstancesCode.error", isEmpty or isContainedIn(allowedValues))
  )(OfficeOfExitStandard.apply)(OfficeOfExitStandard.unapply)

  def adjustCircumstancesError(form: Form[OfficeOfExitStandard]): Form[OfficeOfExitStandard] = {
    val errors = form.errors.map { error =>
      if (error.key == "circumstancesCode" && error.message == "error.required")
        error.copy(messages = Seq("standard.officeOfExit.circumstancesCode.empty"))
      else error
    }

    form.copy(errors = errors)
  }
}