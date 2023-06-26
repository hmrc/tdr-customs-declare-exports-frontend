/*
 * Copyright 2023 HM Revenue & Customs
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

package utils

import utils.HashingUtils.generateHashOfValue

object GenerateTdrSecret {
  def main(args: Array[String]): Unit =
    if (args.length != 2) {
      Console.err.println("Usage: GenerateTdrSecret <tdrHashSalt> <eori>")
      sys.exit(1)
    } else {
      val tdrHashSalt = args(0)
      val eoris = args(1).split(",")

      eoris.foreach(eori => println(s"${eori},${generateHashOfValue(eori, tdrHashSalt)}"))
    }
}
