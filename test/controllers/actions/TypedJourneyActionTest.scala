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

package controllers.actions

import models.{DeclarationType, IdentityData, SignedInUser}
import models.requests.{AuthenticatedRequest, JourneyRequest}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito._
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContentAsEmpty, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.cache.{ExportsCacheService, ExportsDeclarationBuilder}
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TypedJourneyActionTest extends WordSpec with MustMatchers with MockitoSugar with BeforeAndAfterEach with ExportsDeclarationBuilder {

  private val cache = mock[ExportsCacheService]
  private val block = mock[JourneyRequest[_] => Future[Result]]
  private val user = SignedInUser("eori", Enrolments(Set.empty), IdentityData())
  private val declaration = aDeclaration(withType(DeclarationType.STANDARD))

  private def request(declarationId: Option[String]): AuthenticatedRequest[AnyContentAsEmpty.type] = declarationId match {
    case Some(id) =>
      new AuthenticatedRequest(FakeRequest().withSession("declarationId" -> id), user)
    case None => new AuthenticatedRequest(FakeRequest(), user)
  }

  private val refiner = new JourneyAction(cache)

  override def afterEach(): Unit = {
    reset(cache, block)
    super.afterEach()
  }

  "refine" should {
    "permit request" when {
      "answers found" when {
        "on unshared journey" in {
          given(block.apply(any())).willReturn(Future.successful(Results.Ok))
          given(cache.get(refEq("id"))(any[HeaderCarrier])).willReturn(Future.successful(Some(declaration)))

          await(refiner(DeclarationType.STANDARD).invokeBlock(request(Some("id")), block)) mustBe Results.Ok

          val response = theRequestBuilt
          response.cacheModel mustBe declaration
        }

        "on shared journey" in {
          given(block.apply(any())).willReturn(Future.successful(Results.Ok))
          given(cache.get(refEq("id"))(any[HeaderCarrier])).willReturn(Future.successful(Some(declaration)))

          await(refiner(DeclarationType.STANDARD, DeclarationType.SUPPLEMENTARY).invokeBlock(request(Some("id")), block)) mustBe Results.Ok

          val response = theRequestBuilt
          response.cacheModel mustBe declaration
        }
      }

      def theRequestBuilt: JourneyRequest[_] = {
        val captor = ArgumentCaptor.forClass(classOf[JourneyRequest[_]])
        verify(block).apply(captor.capture())
        captor.getValue
      }
    }

    "block request" when {
      "id not found" in {
        await(refiner(DeclarationType.STANDARD).invokeBlock(request(None), block)) mustBe Results.Redirect(
          controllers.routes.StartController.displayStartPage()
        )
      }

      "answers not found" in {
        given(cache.get(refEq("id"))(any[HeaderCarrier])).willReturn(Future.successful(None))

        await(refiner(DeclarationType.STANDARD).invokeBlock(request(Some("id")), block)) mustBe Results.Redirect(
          controllers.routes.StartController.displayStartPage()
        )
      }

      "answers found of a different type" in {
        given(cache.get(refEq("id"))(any[HeaderCarrier])).willReturn(Future.successful(Some(declaration)))

        await(refiner(DeclarationType.OCCASIONAL).invokeBlock(request(Some("id")), block)) mustBe Results.Redirect(
          controllers.routes.StartController.displayStartPage()
        )
      }
    }
  }

}