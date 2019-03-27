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

package models.declaration

import forms.MetadataPropertiesConvertable
import forms.declaration._
import uk.gov.hmrc.http.cache.client.CacheMap

case class Locations(
  destinationCountries: Option[DestinationCountries] = None,
  goodsLocation: Option[GoodsLocation] = None,
  supervisingCustomsOffice: Option[SupervisingCustomsOffice] = None,
  warehouseIdentification: Option[WarehouseIdentification] = None,
  officeOfExit: Option[OfficeOfExit] = None
) extends SummaryContainer with MetadataPropertiesConvertable {

  override def toMetadataProperties(): Map[String, String] =
    Seq(
      destinationCountries.map(_.toMetadataProperties()),
      goodsLocation.map(_.toMetadataProperties()),
      supervisingCustomsOffice.map(_.toMetadataProperties()),
      warehouseIdentification.map(_.toMetadataProperties()),
      officeOfExit.map(_.toMetadataProperties())
    ).flatten.fold(Map.empty)(_ ++ _)

  def isEmpty: Boolean =
    destinationCountries.isEmpty &&
      goodsLocation.isEmpty &&
      supervisingCustomsOffice.isEmpty &&
      warehouseIdentification.isEmpty &&
      officeOfExit.isEmpty
}

object Locations {
  val id = "Locations"

  def apply(cacheMap: CacheMap): Locations = Locations(
    destinationCountries = cacheMap.getEntry[DestinationCountries](DestinationCountries.formId),
    goodsLocation = cacheMap.getEntry[GoodsLocation](GoodsLocation.formId),
    supervisingCustomsOffice = cacheMap.getEntry[SupervisingCustomsOffice](SupervisingCustomsOffice.formId),
    warehouseIdentification = cacheMap.getEntry[WarehouseIdentification](WarehouseIdentification.formId),
    officeOfExit = cacheMap.getEntry[OfficeOfExit](OfficeOfExit.formId)
  )
}