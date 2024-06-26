/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.navigation

import controllers.declaration.routes
import controllers.routes.ChoiceController
import forms.declaration._
import forms.declaration.authorisationHolder.{AuthorisationHolder, AuthorisationHolderRequired, AuthorisationHolderSummary}
import forms.declaration.carrier.CarrierDetails
import forms.declaration.commodityMeasure.SupplementaryUnits
import forms.declaration.countries.Countries.{DestinationCountryPage, RoutingCountryPage}
import forms.declaration.officeOfExit.OfficeOfExit
import forms.declaration.procedurecodes.{AdditionalProcedureCode, ProcedureCode}
import forms.{DeclarationPage, Ducr, Lrn}
import models.ExportsDeclaration
import models.declaration.ExportItem
import models.requests.JourneyRequest
import play.api.mvc.Call
import views.helpers.summary.Card2ForParties

trait CommonNavigator extends CacheDependentNavigators {

  val common: PartialFunction[DeclarationPage, Call] = {
    case DeclarationChoice            => ChoiceController.displayPage
    case Mucr                         => routes.LinkDucrToMucrController.displayPage
    case RepresentativeEntity         => routes.RepresentativeAgentController.displayPage
    case RepresentativeStatus         => routes.RepresentativeEntityController.displayPage
    case ProcedureCode                => routes.ItemsSummaryController.displayItemsSummaryPage
    case ExportItem                   => routes.SectionSummaryController.displayPage(4)
    case DocumentChangeOrRemove       => routes.PreviousDocumentsSummaryController.displayPage
    case TransportLeavingTheBorder    => routes.SectionSummaryController.displayPage(5)
    case WarehouseIdentification      => routes.TransportLeavingTheBorderController.displayPage
    case TransportPayment             => routes.ExpressConsignmentController.displayPage
    case CarrierDetails               => routes.CarrierEoriNumberController.displayPage
    case InvoiceAndExchangeRateChoice => routes.SectionSummaryController.displayPage(3)
    case InvoiceAndExchangeRate       => routes.InvoiceAndExchangeRateChoiceController.displayPage
    case TraderReference              => routes.DucrChoiceController.displayPage
    case ConfirmDucr                  => routes.TraderReferenceController.displayPage
    case RoutingCountryPage           => routes.RoutingCountriesController.displayRoutingQuestion
    case DestinationCountryPage       => routes.SectionSummaryController.displayPage(2)
  }

  val commonItem: PartialFunction[DeclarationPage, String => Call] = {
    case AdditionalProcedureCode           => routes.ProcedureCodesController.displayPage
    case FiscalInformation                 => routes.AdditionalProcedureCodesController.displayPage
    case AdditionalFiscalReferencesSummary => routes.FiscalInformationController.displayPage
    case UNDangerousGoodsCode              => routes.CommodityDetailsController.displayPage
    case StatisticalValue                  => routes.NactCodeSummaryController.displayPage
    case SupplementaryUnits                => routes.CommodityMeasureController.displayPage
  }

  def commonCacheDependent(implicit request: JourneyRequest[_]): PartialFunction[DeclarationPage, ExportsDeclaration => Call] = {
    case Ducr                              => ducrEntryPreviousPage
    case Lrn                               => lrnPreviousPage
    case AuthorisationHolderRequired       => authorisationHolderRequiredPreviousPage
    case AuthorisationHolder               => authorisationHolderAddPreviousPage
    case AuthorisationHolderSummary        => authorisationHolderRequiredPreviousPage
    case Card2ForParties                   => card2ForPartiesPreviousPage
    case SupervisingCustomsOffice          => supervisingCustomsOfficePreviousPage
    case AuthorisationProcedureCodeChoice  => authorisationProcedureCodeChoicePreviousPage
    case OfficeOfExit                      => officeOfExitPreviousPage
    case NatureOfTransaction               => natureOfTransactionPreviousPage
    case ThirdPartyGoodsTransportationPage => thirdPartyGoodsTransportationPreviousPage
  }

  val commonCacheItemDependent: PartialFunction[DeclarationPage, (ExportsDeclaration, String) => Call] = {
    case CommodityDetails          => commodityDetailsPreviousPage
    case AdditionalFiscalReference => additionalFiscalReferencesPreviousPage
    case IsLicenceRequired         => isLicenseRequiredPreviousPage
    case AdditionalInformation     => additionalInformationAddPreviousPage
    case ZeroRatedForVat           => cusCodeOrDangerousGoodsPage
  }
}
