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

package models

import java.time.{Clock, Instant}

import forms.declaration._
import forms.declaration.additionaldeclarationtype.AdditionalDeclarationType.AdditionalDeclarationType
import models.DeclarationStatus.DeclarationStatus
import models.DeclarationType.DeclarationType
import models.declaration._
import play.api.libs.json._

case class ExportsDeclaration(
  id: String,
  status: DeclarationStatus,
  createdDateTime: Instant,
  updatedDateTime: Instant,
  sourceId: Option[String],
  `type`: DeclarationType,
  dispatchLocation: Option[DispatchLocation] = None,
  additionalDeclarationType: Option[AdditionalDeclarationType] = None,
  consignmentReferences: Option[ConsignmentReferences] = None,
  transport: Transport = Transport(),
  parties: Parties = Parties(),
  locations: Locations = Locations(),
  items: Set[ExportItem] = Set.empty,
  totalNumberOfItems: Option[TotalNumberOfItems] = None,
  previousDocuments: Option[PreviousDocumentsData] = None,
  natureOfTransaction: Option[NatureOfTransaction] = None
) {
  def addOrUpdateContainer(container: Container): ExportsDeclaration = {
    copy(transport = transport.addOrUpdateContainer(container))
  }

  val lrn: Option[String] = consignmentReferences.map(_.lrn.value)
  val ducr: Option[String] = consignmentReferences.map(_.ducr.ducr)

  def updateDepartureTransport(departure: DepartureTransport): ExportsDeclaration = {
    copy(transport = transport.copy(
      borderModeOfTransportCode = Some(departure.borderModeOfTransportCode),
      meansOfTransportOnDepartureType = Some(departure.meansOfTransportOnDepartureType),
      meansOfTransportOnDepartureIDNumber = Some(departure.meansOfTransportOnDepartureIDNumber)
    ))
  }

  def updateBorderTransport(formData: BorderTransport): ExportsDeclaration = {
    copy(transport = transport.copy(
      meansOfTransportCrossingTheBorderType = Some(formData.meansOfTransportCrossingTheBorderType),
      meansOfTransportCrossingTheBorderIDNumber = Some(formData.meansOfTransportCrossingTheBorderIDNumber),
      meansOfTransportCrossingTheBorderNationality = formData.meansOfTransportCrossingTheBorderNationality
    ))
  }

  def isComplete: Boolean = status == DeclarationStatus.COMPLETE

  def updatedItem(itemId: String, update: ExportItem => ExportItem): ExportsDeclaration =
    copy(items = items.map(item => if (item.id == itemId) update(item) else item))

  def updateType(`type`: DeclarationType): ExportsDeclaration = copy(`type` = `type`)

  def updateCountriesOfRouting(routingCountries: Seq[String]): ExportsDeclaration =
    copy(locations = locations.copy(routingCountries = routingCountries))

  def removeCountryOfRouting(countryCode: String): ExportsDeclaration =
    copy(locations = locations.copy(routingCountries = locations.routingCountries.filterNot(_ == countryCode)))

  def updateOriginationCountry(originationCountry: String): ExportsDeclaration =
    copy(locations = locations.copy(originationCountry = Some(originationCountry)))

  def updateDestinationCountry(destinationCountry: String): ExportsDeclaration =
    copy(locations = locations.copy(destinationCountry = Some(destinationCountry)))

  def updateRoutingQuestion(answer: Boolean): ExportsDeclaration =
    copy(locations = locations.copy(hasRoutingCountries = Some(answer)))

  def clearRoutingCountries(): ExportsDeclaration =
    copy(locations = locations.copy(hasRoutingCountries = Some(false), routingCountries = Seq.empty))

  def updateContainers(containers: Seq[Container]) =
    copy(transport = transport.copy(containers = containers))

  def updateTransportPayment(payment: TransportPayment) =
    copy(transport = transport.copy(transportPayment = Some(payment)))

  def containRoutingCountries(): Boolean = locations.routingCountries.nonEmpty

  def itemBy(itemId: String): Option[ExportItem] = items.find(_.id.equalsIgnoreCase(itemId))

  def containerBy(containerId: String): Option[Container] = containers.find(_.id.equalsIgnoreCase(containerId))

  def hasContainers: Boolean = containers.nonEmpty

  def containers: Seq[Container] = transport.containers

  def amend()(implicit clock: Clock = Clock.systemUTC()): ExportsDeclaration = {
    val currentTime = Instant.now(clock)
    this.copy(status = DeclarationStatus.DRAFT, createdDateTime = currentTime, updatedDateTime = currentTime, sourceId = Some(id))
  }
}

object ExportsDeclaration {

  implicit val format: OFormat[ExportsDeclaration] = Json.format[ExportsDeclaration]
}
