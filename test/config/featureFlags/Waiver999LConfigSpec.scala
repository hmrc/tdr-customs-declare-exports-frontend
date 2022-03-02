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

package config.featureFlags

import base.UnitWithMocksSpec
import com.typesafe.config.ConfigFactory
import play.api.Configuration

class Waiver999LConfigSpec extends UnitWithMocksSpec {

  private val configWithWaiverEnabled: Configuration =
    Configuration(ConfigFactory.parseString("microservice.services.features.waiver999L=enabled"))
  private val configWithWaiverDisabled: Configuration =
    Configuration(ConfigFactory.parseString("microservice.services.features.waiver999L=disabled"))
  private val emptyConfig: Configuration =
    Configuration(ConfigFactory.parseString("microservice.services.features.default=disabled"))

  private def waiver999LConfig(configuration: Configuration) = new Waiver999LConfig(new FeatureSwitchConfig(configuration))

  "BetaBannerConfig on isBetaBannerEnabled" should {

    "return true" when {

      "the feature is enabled" in {

        waiver999LConfig(configWithWaiverEnabled).is999LEnabled mustBe true
      }
    }

    "return false" when {

      "the feature is disabled" in {

        waiver999LConfig(configWithWaiverDisabled).is999LEnabled mustBe false
      }

      "there is no config for the feature" in {

        waiver999LConfig(emptyConfig).is999LEnabled mustBe false
      }
    }
  }
}
