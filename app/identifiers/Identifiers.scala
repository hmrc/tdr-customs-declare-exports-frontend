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

package identifiers

sealed trait Identifier

case object SubmitDeclarationId extends Identifier {
	override def toString: String = "submitDeclaration"
}

case object ConsignmentId extends Identifier {
	override def toString: String = "consignment"
}

case object DeclarationSummaryId extends Identifier {
	override def toString: String = "declarationSummary"
}

case object EnterEORIId extends Identifier {
	override def toString: String = "enterEORI"
}

case object HaveRepresentativeId extends Identifier {
	override def toString: String = "haveRepresentative"
}

case object OwnDescriptionId extends Identifier {
	override def toString: String = "ownDescription"
}

case object RepresentativesAddressId extends Identifier {
	override def toString: String = "representativesAddress"
}

case object SelectRoleId extends Identifier {
	override def toString: String = "selectRole"
}

case object WhoseDeclarationId extends Identifier {
	override def toString: String = "whoseDeclaration"
}

case object UnknownIdentifier extends Identifier