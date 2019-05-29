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

package services

import forms.Choice
import uk.gov.hmrc.http.cache.client.CacheMap

class WcoMetadataMapper {

  self: WcoMetadataMappingStrategy =>

  def getMetaData(cacheMap: CacheMap, choice: Choice): Any =
    self.produceMetaData(cacheMap, choice)

  def getDeclarationDucr(metaData: Any): Option[String] = self.declarationUcr(metaData)

  def getDeclarationLrn(metaData: Any): Option[String] = self.declarationLrn(metaData)

  def serialise(metaData: Any): String = toXml(metaData)
}