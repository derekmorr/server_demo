package org.derekmorr

import scala.annotation.tailrec

import org.scalatest.{MustMatchers, WordSpecLike}

/**
 * Unit tests for CrashyTendencies
 */
class CrashyTendenciesTest extends MustMatchers with WordSpecLike {

  "CrashyTendencies" must {
    "have a biasedBoolean" which {
      "return true ~20% of the time" in new CrashyTendencies {

        @tailrec def counter(remaining: Int, trueCount: Int): Int = {
          if (remaining == 0) trueCount
          else {
            val newCount = if (biasedBoolean()) trueCount + 1 else trueCount
            counter(remaining - 1, newCount)
          }
        }

        val count = 10000
        val fudgeFactor = 0.05

        val trueCount = counter(count, 0) / (count.toDouble)

        trueCount must equal (0.2 +- fudgeFactor)
      }
    }
  }

}
