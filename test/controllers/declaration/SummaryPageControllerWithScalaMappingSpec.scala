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

package controllers.declaration

import connectors.{CustomsDeclareExportsConnector, NrsConnector}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import services._
import uk.gov.hmrc.auth.core.AuthConnector

class SummaryPageControllerWithScalaMappingSpec extends SummaryPageControllerSpec {

  override lazy val app: Application = GuiceApplicationBuilder()
    .overrides(
      bind[AuthConnector].to(mockAuthConnector),
      bind[CustomsCacheService].to(mockCustomsCacheService),
      bind[CustomsDeclareExportsConnector].to(mockCustomsDeclareExportsConnector),
      bind[NrsConnector].to(mockNrsConnector),
      bind[NRSService].to(mockNrsService),
      bind[ItemsCachingService].to(mockItemsCachingService),
      bind[WcoMetadataMapper].to(new WcoMetadataMapper with WcoMetadataScalaMappingStrategy)
    )
    .build()
}