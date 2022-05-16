/*
 * Copyright 2022 HM Revenue & Customs
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

package forms.declaration.declarationHolder

import config.featureFlags.MerchandiseInBagConfig
import controllers.helpers.DeclarationHolderHelper.declarationHolders
import models.ExportsDeclaration
import models.codes.HolderOfAuthorisationCode
import models.requests.JourneyRequest

object AuthorizationTypeCodes {

  val codeThatOverrideInlandOrBorderSkip = "FP"
  val codeThatSkipLocationOfGoods = "MOU"

  val mutuallyExclusiveAuthCodes = List("CSE", "EXRR")
  val containAtLeastOneOfAuthCodes = List("CSE", "EXRR", "MIB")

  val authCodesThatSkipInlandOrBorder = mutuallyExclusiveAuthCodes

  def codesFilteredFromView(merchandiseInBagConfig: MerchandiseInBagConfig) =
    if (merchandiseInBagConfig.isMerchandiseInBagEnabled) List("EORI")
    else List("EORI", "MIB")

  def isAuthCode(code: String)(implicit request: JourneyRequest[_]): Boolean =
    declarationHolders.exists(_.authorisationTypeCode.exists(_ == code))

  def isAuthCode(declaration: ExportsDeclaration, code: String): Boolean =
    declaration.declarationHolders.exists(_.authorisationTypeCode.exists(_ == code))

  def isAuthCode(declaration: ExportsDeclaration, codes: Seq[String]): Boolean =
    declaration.declarationHolders.exists(_.authorisationTypeCode.exists(codes.contains))

  val codesRequiringDocumentation = Set(
    "ACE",
    "ACP",
    "ACR",
    "ACT",
    "AEOC",
    "AEOF",
    "AEOS",
    "MIB",
    "BOI",
    "BTI",
    "CCL",
    "CGU",
    "CSE",
    "CVA",
    "CW1",
    "CW2",
    "CWP",
    "DEP",
    "DPO",
    "EIR",
    "EPSS",
    "ETD",
    "EUS",
    "EXW",
    "EXWH",
    "FAS",
    "FZ",
    "GGA",
    "IPO",
    "MOU",
    "OPO",
    "REP",
    "REX",
    "RSS",
    "SDE",
    "SIVA",
    "SSE",
    "TEA",
    "TEAH",
    "TRD",
    "TST",
    "UKCS"
  )
}
