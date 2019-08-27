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

package views.declaration

import base.TestHelper
import controllers.util.SaveAndReturn
import forms.common.Address
import forms.declaration.{CarrierDetails, EntityDetails}
import helpers.views.declaration.{CarrierDetailsMessages, CommonMessages}
import models.Mode
import play.api.data.Form
import play.twirl.api.Html
import views.declaration.spec.AppViewSpec
import views.html.declaration.carrier_details
import views.tags.ViewTest

@ViewTest
class CarrierDetailsViewSpec extends AppViewSpec with CarrierDetailsMessages with CommonMessages {

  val form: Form[CarrierDetails] = CarrierDetails.form()
  private val carrierDetailsPage = app.injector.instanceOf[carrier_details]
  private def createView(form: Form[CarrierDetails] = form): Html = carrierDetailsPage(Mode.Normal, form)

  "Carrier Details View on empty page" should {

    "display page title" in {

      createView().getElementById("title").text() must be(messages(title))
    }

    "display empty input with label for EORI" in {

      val view = createView()

      view.getElementById("details_eori-label").text() must be(messages(eoriInfo))
      view.getElementById("details_eori-hint").text() must be(messages(carrierEoriHint))
      view.getElementById("details_eori").attr("value") must be("")
    }

    "display empty input with label for Full name" in {

      val view = createView()

      view.getElementById("details_address_fullName-label").text() must be(messages(fullName))
      view.getElementById("details_address_fullName").attr("value") must be("")
    }

    "display address label" in {

      val view = createView()

      view.getElementById("address-header").text() must be(messages(addressInfo))
      view.getElementById("details_address_fullName").attr("value") must be("")
    }

    "display empty input with label for Address" in {

      val view = createView()

      view.getElementById("details_address_addressLine-label").text() must be(messages(addressLine))
      view.getElementById("details_address_addressLine").attr("value") must be("")
    }

    "display empty input with label for Town or City" in {

      val view = createView()

      view.getElementById("details_address_townOrCity-label").text() must be(messages(townOrCity))
      view.getElementById("details_address_townOrCity").attr("value") must be("")
    }

    "display empty input with label for Postcode" in {

      val view = createView()

      view.getElementById("details_address_postCode-label").text() must be(messages(postCode))
      view.getElementById("details_address_postCode").attr("value") must be("")
    }

    "display empty input with label for Country" in {

      val view = createView()

      view.getElementById("details.address.country-label").text() mustBe "Country"
      view.getElementById("details.address.country").attr("value") mustBe ""
    }

    "display 'Back' button that links to 'Representative Details' page" in {

      val backButton = createView().getElementById("link-back")

      backButton.text() must be(messages(backCaption))
      backButton.attr("href") must be("/customs-declare-exports/declaration/representative-details")
    }

    "display 'Save and continue' button on page" in {
      val view = createView()
      view.getElementById("submit").text() must be(messages(saveAndContinueCaption))
    }

    "display 'Save and return' button on page" in {
      val view = createView()
      view.getElementById("submit_and_return").text() must be(messages(saveAndReturnCaption))
      view.getElementById("submit_and_return").attr("name") must be(SaveAndReturn.toString)
    }
  }

  "Carrier Details View with invalid input" should {

    "display error when both EORI and business details are empty" in {

      val view = createView(CarrierDetails.form().bind(Map[String, String]()))

      checkErrorsSummary(view)
      checkErrorLink(view, "details-error", eoriOrAddressEmpty, "#details")

      view.getElementById("error-message-details-input").text() must be(messages(eoriOrAddressEmpty))
    }

    "display error when EORI is provided, but is incorrect" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(CarrierDetails(EntityDetails(Some(TestHelper.createRandomAlphanumericString(18)), None)))
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.eori-error", eoriError, "#details_eori")

      view.getElementById("error-message-details_eori-input").text() must be(messages(eoriError))
    }

    "display error for empty Full name" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(EntityDetails(None, Some(Address("", "Test Street", "Leeds", "LS18BN", "England"))))
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.fullName-error", fullNameEmpty, "#details_address_fullName")

      view.getElementById("error-message-details_address_fullName-input").text() must be(messages(fullNameEmpty))
    }

    "display error for incorrect Full name" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(
                None,
                Some(
                  Address(TestHelper.createRandomAlphanumericString(71), "Test Street", "Leeds", "LS18BN", "England")
                )
              )
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.fullName-error", fullNameError, "#details_address_fullName")

      view.getElementById("error-message-details_address_fullName-input").text() must be(messages(fullNameError))
    }

    "display error for empty Address" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(EntityDetails(None, Some(Address("Marco Polo", "", "Leeds", "LS18BN", "England"))))
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.addressLine-error", addressLineEmpty, "#details_address_addressLine")

      view.getElementById("error-message-details_address_addressLine-input").text() must be(messages(addressLineEmpty))
    }

    "display error for incorrect Address" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(
                None,
                Some(Address("Marco Polo", TestHelper.createRandomAlphanumericString(71), "Leeds", "LS18BN", "England"))
              )
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.addressLine-error", addressLineError, "#details_address_addressLine")

      view.getElementById("error-message-details_address_addressLine-input").text() must be(messages(addressLineError))
    }

    "display error for empty Town or city" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(EntityDetails(None, Some(Address("Marco Polo", "Test Street", "", "LS18BN", "England"))))
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.townOrCity-error", townOrCityEmpty, "#details_address_townOrCity")

      view.getElementById("error-message-details_address_townOrCity-input").text() must be(messages(townOrCityEmpty))
    }

    "display error for incorrect Town or city" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(
                None,
                Some(
                  Address(
                    "Marco Polo",
                    "Test Street",
                    TestHelper.createRandomAlphanumericString(71),
                    "LS18BN",
                    "England"
                  )
                )
              )
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.townOrCity-error", townOrCityError, "#details_address_townOrCity")

      view.getElementById("error-message-details_address_townOrCity-input").text() must be(messages(townOrCityError))
    }

    "display error for empty Postcode" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(EntityDetails(None, Some(Address("Marco Polo", "Test Street", "Leeds", "", "England"))))
          )
      )
      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.postCode-error", postCodeEmpty, "#details_address_postCode")

      view.getElementById("error-message-details_address_postCode-input").text() must be(messages(postCodeEmpty))
    }

    "display error for incorrect Postcode" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(
                None,
                Some(
                  Address(
                    "Marco Polo",
                    "Test Street",
                    "Leeds",
                    TestHelper.createRandomAlphanumericString(71),
                    "England"
                  )
                )
              )
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.postCode-error", postCodeError, "#details_address_postCode")

      view.getElementById("error-message-details_address_postCode-input").text() must be(messages(postCodeError))
    }

    "display error for empty Country" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(EntityDetails(None, Some(Address("Marco Polo", "Test Street", "Leeds", "LS18BN", ""))))
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.country-error", countryEmpty, "#details_address_country")

      view.select("span.error-message").text() must be(messages(countryEmpty))
    }

    "display error for incorrect Country" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(None, Some(Address("Marco Polo", "Test Street", "Leeds", "LS18BN", "Barcelona")))
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.country-error", countryError, "#details_address_country")

      view.select("span.error-message").text() must be(messages(countryError))
    }

    "display errors when everything except Full name is empty" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(CarrierDetails(EntityDetails(None, Some(Address("Marco Polo", "", "", "", "")))))
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.addressLine-error", addressLineEmpty, "#details_address_addressLine")
      checkErrorLink(view, "details.address.townOrCity-error", townOrCityEmpty, "#details_address_townOrCity")
      checkErrorLink(view, "details.address.postCode-error", postCodeEmpty, "#details_address_postCode")
      checkErrorLink(view, "details.address.country-error", countryEmpty, "#details_address_country")

      view.getElementById("error-message-details_address_addressLine-input").text() must be(messages(addressLineEmpty))
      view.getElementById("error-message-details_address_townOrCity-input").text() must be(messages(townOrCityEmpty))
      view.getElementById("error-message-details_address_postCode-input").text() must be(messages(postCodeEmpty))
      view.select("span.error-message").text() must be(messages(countryEmpty))

    }

    "display errors when everything except Country is empty" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(CarrierDetails(EntityDetails(None, Some(Address("", "", "", "", "Ukraine")))))
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.fullName-error", fullNameEmpty, "#details_address_fullName")
      checkErrorLink(view, "details.address.addressLine-error", addressLineEmpty, "#details_address_addressLine")
      checkErrorLink(view, "details.address.townOrCity-error", townOrCityEmpty, "#details_address_townOrCity")
      checkErrorLink(view, "details.address.postCode-error", postCodeEmpty, "#details_address_postCode")

      view.getElementById("error-message-details_address_fullName-input").text() must be(messages(fullNameEmpty))
      view.getElementById("error-message-details_address_addressLine-input").text() must be(messages(addressLineEmpty))
      view.getElementById("error-message-details_address_townOrCity-input").text() must be(messages(townOrCityEmpty))
      view.getElementById("error-message-details_address_postCode-input").text() must be(messages(postCodeEmpty))
    }

    "display errors when everything except Full name is incorrect" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(
                None,
                Some(
                  Address(
                    "Marco Polo",
                    TestHelper.createRandomAlphanumericString(71),
                    TestHelper.createRandomAlphanumericString(71),
                    TestHelper.createRandomAlphanumericString(71),
                    TestHelper.createRandomAlphanumericString(71)
                  )
                )
              )
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.addressLine-error", addressLineError, "#details_address_addressLine")
      checkErrorLink(view, "details.address.townOrCity-error", townOrCityError, "#details_address_townOrCity")
      checkErrorLink(view, "details.address.postCode-error", postCodeError, "#details_address_postCode")
      checkErrorLink(view, "details.address.country-error", countryError, "#details_address_country")

      view.getElementById("error-message-details_address_addressLine-input").text() must be(messages(addressLineError))
      view.getElementById("error-message-details_address_townOrCity-input").text() must be(messages(townOrCityError))
      view.getElementById("error-message-details_address_postCode-input").text() must be(messages(postCodeError))
      view.select("span.error-message").text() must be(messages(countryError))
    }

    "display errors when everything except Country is incorrect" in {

      val view = createView(
        CarrierDetails
          .form()
          .fillAndValidate(
            CarrierDetails(
              EntityDetails(
                None,
                Some(
                  Address(
                    TestHelper.createRandomAlphanumericString(71),
                    TestHelper.createRandomAlphanumericString(71),
                    TestHelper.createRandomAlphanumericString(71),
                    TestHelper.createRandomAlphanumericString(71),
                    "Ukraine"
                  )
                )
              )
            )
          )
      )

      checkErrorsSummary(view)
      checkErrorLink(view, "details.address.fullName-error", fullNameError, "#details_address_fullName")
      checkErrorLink(view, "details.address.addressLine-error", addressLineError, "#details_address_addressLine")
      checkErrorLink(view, "details.address.townOrCity-error", townOrCityError, "#details_address_townOrCity")
      checkErrorLink(view, "details.address.postCode-error", postCodeError, "#details_address_postCode")

      view.getElementById("error-message-details_address_fullName-input").text() must be(messages(fullNameError))
      view.getElementById("error-message-details_address_addressLine-input").text() must be(messages(addressLineError))
      view.getElementById("error-message-details_address_townOrCity-input").text() must be(messages(townOrCityError))
      view.getElementById("error-message-details_address_postCode-input").text() must be(messages(postCodeError))
    }
  }

  "Carrier Details View when filled" should {

    "display data in EORI input" in {

      val form = CarrierDetails.form().fill(CarrierDetails(EntityDetails(Some("1234"), None)))
      val view = createView(form)

      view.getElementById("details_eori").attr("value") must be("1234")
      view.getElementById("details_address_fullName").attr("value") must be("")
      view.getElementById("details_address_addressLine").attr("value") must be("")
      view.getElementById("details_address_townOrCity").attr("value") must be("")
      view.getElementById("details_address_postCode").attr("value") must be("")
      view.getElementById("details.address.country").attr("value") must be("")
    }

    "display data in Business address inputs" in {

      val form = CarrierDetails
        .form()
        .fill(CarrierDetails(EntityDetails(None, Some(Address("test", "test1", "test2", "test3", "test4")))))
      val view = createView(form)

      view.getElementById("details_eori").attr("value") must be("")
      view.getElementById("details_address_fullName").attr("value") must be("test")
      view.getElementById("details_address_addressLine").attr("value") must be("test1")
      view.getElementById("details_address_townOrCity").attr("value") must be("test2")
      view.getElementById("details_address_postCode").attr("value") must be("test3")
      view.getElementById("details.address.country").attr("value") must be("test4")
    }

    "display data in both EORI and Business address inputs" in {

      val form = CarrierDetails
        .form()
        .fill(CarrierDetails(EntityDetails(Some("1234"), Some(Address("test", "test1", "test2", "test3", "test4")))))
      val view = createView(form)

      view.getElementById("details_eori").attr("value") must be("1234")
      view.getElementById("details_address_fullName").attr("value") must be("test")
      view.getElementById("details_address_addressLine").attr("value") must be("test1")
      view.getElementById("details_address_townOrCity").attr("value") must be("test2")
      view.getElementById("details_address_postCode").attr("value") must be("test3")
      view.getElementById("details.address.country").attr("value") must be("test4")
    }
  }
}
