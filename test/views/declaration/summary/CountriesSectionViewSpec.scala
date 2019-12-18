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

package views.declaration.summary

import models.DeclarationType._
import services.cache.ExportsTestData
import views.declaration.spec.UnitViewSpec
import views.html.declaration.summary.countries_section

class CountriesSectionViewSpec extends UnitViewSpec with ExportsTestData {

  "Countries section" should {

    onEveryDeclarationJourney { request =>
      "display empty routing countries" in {

        val data = aDeclaration(withoutRoutingCountries())

        val view = countries_section(data)(messages, request)

        view.getElementById("countriesOfRouting-label").text() mustBe messages("declaration.summary.countries.routingCountries")
        view.getElementById("countriesOfRouting").text() mustBe empty
      }

      "display single routing country" in {

        val countryCode = "GB"
        val data = aDeclaration(withRoutingCountries(Seq(countryCode)))

        val view = countries_section(data)(messages, request)

        val expectedCountry = "United Kingdom (GB)"

        view.getElementById("countriesOfRouting-label").text() mustBe messages("declaration.summary.countries.routingCountries")
        view.getElementById("countriesOfRouting").text() mustBe expectedCountry
      }

      "display multiple routing countries separated by comma" in {

        val firstCountryCode = "GB"
        val secondCountryCode = "PL"
        val data = aDeclaration(withRoutingCountries(Seq(firstCountryCode, secondCountryCode)))

        val view = countries_section(data)(messages, request)

        val firstExpectedCountry = "United Kingdom (GB)"
        val secondExpectedCountry = "Poland (PL)"

        view.getElementById("countriesOfRouting-label").text() mustBe messages("declaration.summary.countries.routingCountries")
        view.getElementById("countriesOfRouting").text() mustBe s"$firstExpectedCountry, $secondExpectedCountry"
      }

      "display empty country of destination" in {

        val data = aDeclaration(withoutDestinationCountry())

        val view = countries_section(data)(messages, request)

        view.getElementById("countryOfDestination-label").text() mustBe messages("declaration.summary.countries.countryOfDestination")
        view.getElementById("countryOfDestination").text() mustBe empty
      }

      "display country of destination" in {

        val countryCode = "GB"
        val data = aDeclaration(withDestinationCountry(countryCode))

        val view = countries_section(data)(messages, request)

        val expectedCountry = "United Kingdom (GB)"

        view.getElementById("countryOfDestination-label").text() mustBe messages("declaration.summary.countries.countryOfDestination")
        view.getElementById("countryOfDestination").text() mustBe expectedCountry
      }

      "display change button for countries of routing" in {

        val data = aDeclaration(withoutRoutingCountries())

        val view = countries_section(data)(messages, request)

        val List(change, accessibleChange) = view.getElementById("countriesOfRouting-change").text().split(" ").toList

        change mustBe messages("site.change")
        accessibleChange mustBe messages("declaration.summary.countries.routingCountries.change")

        view.getElementById("countriesOfRouting-change") must haveHref(controllers.declaration.routes.RoutingCountriesSummaryController.displayPage())
      }

      "display change button for country of destination" in {

        val data = aDeclaration(withoutOriginationCountry())

        val view = countries_section(data)(messages, request)

        val List(change, accessibleChange) = view.getElementById("countryOfDestination-change").text().split(" ").toList

        change mustBe messages("site.change")
        accessibleChange mustBe messages("declaration.summary.countries.countryOfDestination")

        view.getElementById("countryOfDestination-change") must haveHref(controllers.declaration.routes.DestinationCountryController.displayPage())
      }
    }

    onJourney(STANDARD, SUPPLEMENTARY, CLEARANCE) { request =>
      "display country of dispatch" in {

        val countryCode = "GB"
        val data = aDeclaration(withOriginationCountry(countryCode))

        val view = countries_section(data)(messages, request)

        val expectedCountry = "United Kingdom (GB)"

        view.getElementById("countryOfDispatch-label").text() mustBe messages("declaration.summary.countries.countryOfDispatch")
        view.getElementById("countryOfDispatch").text() mustBe expectedCountry
      }

      "display empty country of dispatch" in {

        val data = aDeclaration(withoutOriginationCountry())

        val view = countries_section(data)(messages, request)

        view.getElementById("countryOfDispatch-label").text() mustBe messages("declaration.summary.countries.countryOfDispatch")
        view.getElementById("countryOfDispatch").text() mustBe empty
      }

      "display change button for country of dispatch" in {

        val data = aDeclaration(withoutOriginationCountry())

        val view = countries_section(data)(messages, request)

        val List(change, accessibleChange) = view.getElementById("countryOfDispatch-change").text().split(" ").toList

        change mustBe messages("site.change")
        accessibleChange mustBe messages("declaration.summary.countries.countryOfDispatch.change")

        view.getElementById("countryOfDispatch-change") must haveHref(controllers.declaration.routes.OriginationCountryController.displayPage())
      }
    }

    onJourney(SIMPLIFIED, OCCASIONAL) { request =>
      "not display country of dispatch" in {

        val data = aDeclaration()

        val view = countries_section(data)(messages, request)

        view.getElementById("countryOfDispatch-label") mustBe null
        view.getElementById("countryOfDispatch") mustBe null
        view.getElementById("countryOfDispatch-change") mustBe null
      }
    }
  }
}
