package org.derekmorr

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class ActorTest extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with ImplicitSender
  with MustMatchers
  with BeforeAndAfterAll {

  /** shutdown the test actor system after all tests finish. */
  override protected def afterAll {
    super.afterAll
    system.terminate()
  }

}
