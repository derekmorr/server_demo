package org.derekmorr

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

import akka.actor.{Actor, ActorRef, Props}
import org.derekmorr.DbActor.{GetResource, DBResponse}

/**
 * Actor for the database.
 *
 * This is a very badly behaving actor.
 * Randomly, it will crash and not send a response.
 * If it doesn't crash, it will delay before sending a response.
 */
class DbActor extends Actor with CrashyTendencies {

  override def receive = {
    case GetResource(id) => getResource(id, sender())
  }

  private def getResource(id: String, destination: ActorRef) = {
    
    // maybe throw an unhandled runtime exception
    maybeCrash()

    val rand = Math.abs(Random.nextGaussian * 500).toInt
    val randomDelay = rand milliseconds

    // after randomDelay milliseconds, send a response back to the caller
    // we have a hard-coded response of "howdy"
    import context.dispatcher
    context.system.scheduler.scheduleOnce(randomDelay) {
      destination ! DBResponse(id, Option("howdy"))
    }
  }
}

object DbActor {
  /** DbActor's protocol */
  case class GetResource(resourceId: String)
  case class DBResponse(resourceId: String, value: Option[String])

  /** factory method */
  def props: Props = Props(new DbActor)
}
