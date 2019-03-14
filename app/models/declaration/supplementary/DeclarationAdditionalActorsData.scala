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
import forms.supplementary.DeclarationAdditionalActors
import play.api.libs.json.Json

case class DeclarationAdditionalActorsData(actors: Seq[DeclarationAdditionalActors])
    extends MetadataPropertiesConvertable {
  override def toMetadataProperties(): Map[String, String] =
    actors.zipWithIndex.map { actor =>
      Map(
        "declaration.goodsShipment.aeoMutualRecognitionParties[" + actor._2 + "].id" -> actor._1.eori.getOrElse(""),
        "declaration.goodsShipment.aeoMutualRecognitionParties[" + actor._2 + "].roleCode" -> actor._1.partyType
          .getOrElse("")
      )
    }.fold(Map.empty)(_ ++ _)

  def containsItem(actor: DeclarationAdditionalActors): Boolean = actors.contains(actor)
}

object DeclarationAdditionalActorsData {
  implicit val format = Json.format[DeclarationAdditionalActorsData]

  val formId = "DeclarationAdditionalActorsData"

  val maxNumberOfItems = 99
}