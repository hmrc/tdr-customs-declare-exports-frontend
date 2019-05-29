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

package services.mapping.goodsshipment

import forms.declaration.{TransactionType, TransactionTypeSpec}
import org.scalatest.{Matchers, WordSpec}
import uk.gov.hmrc.http.cache.client.CacheMap

class TransactionTypeBuilderSpec extends WordSpec with Matchers {

  "TransactionTypeBuilder" should {
    "correctly map to the WCO-DEC GoodsShipment.TransactionNatureCodeType instance" when {
      "'identifier' has been supplied" in {
        implicit val cacheMap =
          CacheMap("CacheID", Map(TransactionType.formId -> TransactionTypeSpec.correctTransactionTypeJSON))
        val transactionNatureCodeType = GoodsShipmentTransactionTypeBuilder.build(cacheMap)
        transactionNatureCodeType.getValue should be("11")
      }
      "'identifier' has not been supplied" in {
        implicit val cacheMap =
          CacheMap("CacheID", Map(TransactionType.formId -> TransactionTypeSpec.emptyTransactionTypeJSON))
        GoodsShipmentTransactionTypeBuilder.build(cacheMap) should be(null)
      }
    }
  }
}