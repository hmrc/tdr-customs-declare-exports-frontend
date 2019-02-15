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

package models.declaration.supplementary

import forms.MetadataPropertiesConvertable
import forms.supplementary.DeclarationHolder
import play.api.libs.json.Json

case class DeclarationHoldersData(holders: Seq[DeclarationHolder]) extends MetadataPropertiesConvertable {
  override def toMetadataProperties(): Map[String, String] =
    holders.zipWithIndex.map { holderWithId =>
      Map(
        "declaration.authorisationHolders[" + holderWithId._2 + "].categoryCode" -> holderWithId._1.authorisationTypeCode
          .getOrElse(""),
        "declaration.authorisationHolders[" + holderWithId._2 + "].id" -> holderWithId._1.eori.getOrElse("")
      )
    }.fold(Map.empty)(_ ++ _)

  def containsHolder(holder: DeclarationHolder): Boolean = holders.contains(holder)
}

object DeclarationHoldersData {
  implicit val format = Json.format[DeclarationHoldersData]

  val formId = "DeclarationHoldersData"

  val limitOfHolders = 99
}