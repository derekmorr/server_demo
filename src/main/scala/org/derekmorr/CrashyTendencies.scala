package org.derekmorr

import scala.util.Random

trait CrashyTendencies {

  def biasedBoolean(): Boolean = Math.abs(Random.nextGaussian()) < 0.2

  def maybeCrash() = {
    if (biasedBoolean()) throw new SomeRandomRuntimeException("yolo")
  }

}

class SomeRandomRuntimeException(msg: String) extends Exception

