package org.derekmorr

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

import akka.actor.ActorRef
import akka.testkit.TestActor.AutoPilot
import akka.testkit.{TestActor, TestProbe}
import org.derekmorr.DbActor.{DBResponse, GetResource}
import org.derekmorr.WorkerActor.{WorkerResponse, ProcessRequest}

/**
 * Response time tests for WorkerActor
 */
class WorkerActorTest extends ActorTest {

  val slaTimeout = 1 second

  "WorkerActor" must {
    "query the database" in {
      val mockDB = TestProbe()
      val workerActor = system.actorOf(WorkerActor.props(mockDB.ref, slaTimeout))

      workerActor ! ProcessRequest("foobar")

      mockDB.expectMsgClass(classOf[GetResource])
    }

    "respond within its SLA" when {
      "the DB fails to respond at all" in {
        val mockDB = TestProbe()
        val workerActor = system.actorOf(WorkerActor.props(mockDB.ref, slaTimeout))

        within(slaTimeout) {
          workerActor ! ProcessRequest("foobar")
          expectMsgClass(classOf[WorkerResponse])
        }
      }

      "the DB takes too long to respond" in {
        val mockDB = TestProbe()
        val sleepTime = slaTimeout + (4 seconds)

        // Wire in an autopilot to our mock database.
        // When it gets an GetResource message, sleep for up to 5 seconds before sending a reply.
        // We can call Thread.sleep here b/c it's just a test.
        mockDB.setAutoPilot(new SleepyAutoPilot(sleepTime, Option("foobar")))

        val workerActor = system.actorOf(WorkerActor.props(mockDB.ref, slaTimeout))

        // finally, the actual test
        within(slaTimeout) {
          workerActor ! ProcessRequest("foobar")
          expectMsgClass(classOf[WorkerResponse])
        }
      }
    }
  }
}

class SleepyAutoPilot(sleepTime: FiniteDuration, response: Option[String]) extends AutoPilot {
  override def run(sender: ActorRef, msg: Any): AutoPilot = msg match {
    case GetResource(x) => {
      Thread.sleep(sleepTime.toMillis)
      sender.tell(response, sender)
      TestActor.KeepRunning
    }
  }
}
