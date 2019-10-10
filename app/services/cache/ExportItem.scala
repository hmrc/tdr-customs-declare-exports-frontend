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

package services.cache

import forms.declaration.{AdditionalFiscalReferencesData, CommodityMeasure, FiscalInformation, ItemType, PackageInformation}
import forms.declaration.FiscalInformation.AllowedFiscalInformationAnswers.yes
import models.declaration.{AdditionalInformationData, DocumentsProducedData, ProcedureCodesData}
import play.api.libs.json.Json

case class ExportItem(
  id: String,
  sequenceId: Int = 0,
  procedureCodes: Option[ProcedureCodesData] = None,
  fiscalInformation: Option[FiscalInformation] = None,
  additionalFiscalReferencesData: Option[AdditionalFiscalReferencesData] = None,
  itemType: Option[ItemType] = None,
  packageInformation: List[PackageInformation] = Nil,
  commodityMeasure: Option[CommodityMeasure] = None,
  additionalInformation: Option[AdditionalInformationData] = None,
  documentsProducedData: Option[DocumentsProducedData] = None
) {
  def hasFiscalReferences: Boolean =
    fiscalInformation.exists(_.onwardSupplyRelief == FiscalInformation.AllowedFiscalInformationAnswers.yes)

  def isCompleted: Boolean =
    procedureCodes.isDefined && isFiscalInformationCompleted && itemType.isDefined &&
      packageInformation.nonEmpty && commodityMeasure.isDefined && additionalInformation.isDefined

  private def isFiscalInformationCompleted: Boolean =
    if (fiscalInformation.exists(_.onwardSupplyRelief == yes)) additionalFiscalReferencesData.isDefined
    else fiscalInformation.isDefined
}

object ExportItem {

  implicit val format = Json.format[ExportItem]
}
