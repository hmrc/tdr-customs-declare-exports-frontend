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

package views.pdf

import play.api.i18n.Messages

object DeclarationType extends Enumeration {
  type DeclarationType = Value
  val EXA, EXB, EXC, EXD, EXE, EXF, EXJ, EXK, EXY, EXZ = Value

  def translate(declarationType: String)(implicit messages: Messages): String =
    if (DeclarationType.values.exists(_.toString == declarationType))
      messages(s"pdf.template.declarationType.$declarationType")
    else
      declarationType
}
