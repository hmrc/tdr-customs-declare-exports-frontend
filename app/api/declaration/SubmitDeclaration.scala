/*
 * Copyright 2018 HM Revenue & Customs
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

package api.declaration

import javax.inject.Inject
import play.api.{Environment, Play}
import play.api.libs.ws._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

/*
'<?xml version="1.0" encoding="UTF-8"?>
<md:MetaData xmlns:md="urn:wco:datamodel:WCO:DocumentMetaData-DMS:2" xmlns="urn:wco:datamodel:WCO:DEC-DMS:2"
             xmlns:udt="urn:wco:datamodel:WCO:Declaration_DS:DMS:2">
    <md:WCODataModelVersionCode>3.6</md:WCODataModelVersionCode>
    <md:WCOTypeName>DEC-DMS</md:WCOTypeName>
    <md:ResponsibleCountryCode>GB</md:ResponsibleCountryCode>
    <md:ResponsibleAgencyName>Agency ABC</md:ResponsibleAgencyName>
    <md:AgencyAssignedCustomizationVersionCode>v1.2</md:AgencyAssignedCustomizationVersionCode>
    <!--
    Import Declaration including:
    - DV1 elements
    - Quota / Preference (Add.Info with type "TRR", DutyTaxFee)
    - VAT transfer
    - Additional costs (DutyTaxFee)
    - Direct representation
    - Arrival transport means
    - Payer / Surety
    - UCR
    - Warehouse reference
    - CDIU document with quantity/amount
    - Special mention (Add.Info with type "CUS")
    - National classification
    - Relief amount (DutyTaxFee)
    - Method of payment
    - Supplementary units
    - Additional calculation units
    - Previous document
    -->
    <Declaration>
        <AcceptanceDateTime>
            <udt:DateTimeString formatCode="304">20161207010101Z</udt:DateTimeString>
        </AcceptanceDateTime>
        <FunctionCode>9</FunctionCode>
        <FunctionalReferenceID>DemoUK20161207_010</FunctionalReferenceID>
        <TypeCode>IMZ</TypeCode>
        <DeclarationOfficeID>0051</DeclarationOfficeID>
        <TotalPackageQuantity>1</TotalPackageQuantity>
        <Agent>
            <ID>ZZ123456789001</ID>
            <FunctionCode>2</FunctionCode>
        </Agent>
        <CurrencyExchange> <!-- 1094 new section-->
            <RateNumeric>1.234</RateNumeric>
        </CurrencyExchange>
        <Declarant>
            <ID>ZZ123456789000</ID>
        </Declarant>
        <GoodsShipment>
            <ExitDateTime>
                <udt:DateTimeString formatCode="304">20161207010101Z</udt:DateTimeString>
            </ExitDateTime> <!-- 1094 -->
            <TransactionNatureCode>1</TransactionNatureCode>
            <Buyer>
                <Name>Buyer name Part1Buyername Part2</Name>
                <Address>
                    <CityName>Buyer City name</CityName>
                    <CountryCode>NL</CountryCode>
                    <Line>Buyerstreet Part1BuyerStreet Part2 7C</Line>
                    <PostcodeID>8603 AV</PostcodeID>
                </Address>
            </Buyer>
            <Consignee>
                <ID>ZZ123456789002</ID>
            </Consignee>
            <Consignment>
                <ArrivalTransportMeans>
                    <Name>Titanic II</Name>
                    <TypeCode>1</TypeCode>
                </ArrivalTransportMeans>
                <GoodsLocation>
                    <Name>3016 DR, Loods 5</Name>
                </GoodsLocation>
                <LoadingLocation>  <!-- 1094 -->
                    <Name>Neverland</Name>
                    <ID>1234</ID>
                </LoadingLocation>
                <TransportEquipment>
                    <SequenceNumeric>1</SequenceNumeric>
                    <ID>CONTAINERNUMBER17</ID>
                </TransportEquipment>
                <TransportEquipment>
                    <SequenceNumeric>2</SequenceNumeric>
                    <ID>CONTAINERNUMBER22</ID>
                </TransportEquipment>
            </Consignment>
            <DomesticDutyTaxParty>
                <ID>ZZ123456789003</ID>
            </DomesticDutyTaxParty>
            <ExportCountry>
                <ID>CA</ID>
            </ExportCountry>
            <GovernmentAgencyGoodsItem>
                <SequenceNumeric>1</SequenceNumeric>
                <StatisticalValueAmount>1234567</StatisticalValueAmount>
                <AdditionalDocument>
                    <CategoryCode>I</CategoryCode>
                    <EffectiveDateTime>
                        <udt:DateTimeString formatCode="304">20130812091112Z</udt:DateTimeString>
                    </EffectiveDateTime> <!-- 1094 -->
                    <ID>I003INVOERVERGEU</ID> <!-- CDD-1094 an..70 -->
                    <Name>NAME_HERE</Name> <!-- CDD-1094 ADDED -->
                    <TypeCode>003</TypeCode>
                    <LPCOExemptionCode>123</LPCOExemptionCode> <!-- 1094 -->
                </AdditionalDocument>
                <AdditionalDocument>
                    <CategoryCode>N</CategoryCode>
                    <ID>N861UnivCertOrigin</ID>
                    <TypeCode>861</TypeCode>
                </AdditionalDocument>
                <AdditionalInformation>
                    <StatementCode>1</StatementCode><!-- not affiliated -->
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>3</StatementCode><!-- no price influence -->
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>5</StatementCode><!-- no approximate value -->
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>8</StatementCode><!-- special restrictions -->
                    <StatementDescription>Special Restrictions</StatementDescription>
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>9</StatementCode><!-- no price conditions -->
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>11</StatementCode><!-- no royalties or license fees -->
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>13</StatementCode><!-- no other revenue -->
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>16</StatementCode><!-- customs decisions -->
                    <StatementDescription>11NL12345678901234</StatementDescription>
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>17</StatementCode><!-- contract information -->
                    <StatementDescription>Contract 12123, 24-11-2011</StatementDescription>
                    <StatementTypeCode>ABC</StatementTypeCode>
                </AdditionalInformation>
                <AdditionalInformation>
                    <StatementCode>90010</StatementCode>
                    <StatementDescription>VERVOERDER: InterTrans</StatementDescription>
                    <StatementTypeCode>CUS</StatementTypeCode>
                </AdditionalInformation>
                <Commodity>
                    <Description>Inertial navigation systems</Description>
                    <Classification>
                        <ID>901420209000000000</ID>
                        <IdentificationTypeCode>SRZ</IdentificationTypeCode>
                    </Classification>
                    <Classification>
                        <ID>9002</ID>
                        <IdentificationTypeCode>GN</IdentificationTypeCode><!--Not representative for this commodity-->
                    </Classification>
                    <DutyTaxFee>
                        <AdValoremTaxBaseAmount currencyID="EUR">900</AdValoremTaxBaseAmount>
                        <DutyRegimeCode>
                            100<!--Specified a Tariff Quota preference (not representative)--></DutyRegimeCode>
                        <TypeCode>B00</TypeCode>
                        <Payment>
                            <MethodCode>M</MethodCode>
                        </Payment>
                    </DutyTaxFee>
                </Commodity>
                <GovernmentProcedure>
                    <CurrentCode>40</CurrentCode>
                    <PreviousCode>91</PreviousCode>
                </GovernmentProcedure>
                <GovernmentProcedure>
                    <CurrentCode>C30</CurrentCode>
                </GovernmentProcedure>
                <Origin>
                    <CountryCode>US</CountryCode>
                </Origin>
                <Packaging>
                    <SequenceNumeric>1</SequenceNumeric>
                    <MarksNumbersID>SHIPPING MARKS PART1 SHIPPING</MarksNumbersID>
                    <QuantityQuantity>9</QuantityQuantity>
                    <TypeCode>CT</TypeCode>
                </Packaging>
                <PreviousDocument>
                    <ID>X355ID13</ID> <!-- 1094 -->
                    <LineNumeric>1</LineNumeric>
                </PreviousDocument>
                <ValuationAdjustment>
                    <AdditionCode>155</AdditionCode>
                </ValuationAdjustment>
            </GovernmentAgencyGoodsItem>
            <Invoice>
                <ID>INVOICENUMBER</ID>
                <IssueDateTime>
                    <udt:DateTimeString formatCode="304">20130812091112Z</udt:DateTimeString>
                </IssueDateTime>    <!-- 1094 -->
            </Invoice>
            <Payer>
                <ID>ZZ123456789003</ID>
            </Payer>
            <Seller>
                <Name>Seller name Part1Sellername Part2</Name>
                <Address>
                    <CityName>Seller City name</CityName>
                    <CountryCode>MX</CountryCode>
                    <Line>Sellerstreet Part1SellerStreet Part2 7C</Line>
                    <PostcodeID>8603 AV</PostcodeID>
                </Address>
            </Seller>
            <Surety>
                <ID>ZZ123456789003</ID>
            </Surety>
            <TradeTerms>
                <ConditionCode>CIP</ConditionCode>
                <CountryRelationshipCode>158</CountryRelationshipCode>
                <LocationName>Rotterdam</LocationName>
            </TradeTerms>
            <UCR>
                <ID>UN1234567893123456789</ID>
                <TraderAssignedReferenceID>P198R65Q29</TraderAssignedReferenceID>
            </UCR>
            <Warehouse>
                <ID>A123456ZZ</ID>
            </Warehouse>
        </GoodsShipment>
    </Declaration>
</md:MetaData>'
https://customs-declarations.protected.mdtp/
 */
class SubmitDeclaration(ws: WSClient, baseUrl: String) {
  @Inject def this(ws: WSClient, env: Environment) = this(ws, "https://customs-declarations.protected.mdtp")

  def submit(declaration: Declaration, authToken: String): Future[Int] = {
    ws.url(s"$baseUrl/")
      .withHeaders(
        "Accept" -> "application/vnd.hmrc.1.0+xml",
        "Content-Type" -> "application/xml; charset=UTF-8",
        "X-Client-ID" -> "RMaFZKEe45nkUwn4R0w1Wa6pBJUa",     //Is this a dynamic thing from user auth?
        "Authorization" -> s"Bearer $authToken"
      )
      .post(declaration.toXml)
      .map(_.status)
  }
}
