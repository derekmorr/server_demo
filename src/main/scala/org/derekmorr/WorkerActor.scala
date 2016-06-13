package org.derekmorr

import scala.concurrent.duration._
import scala.language.postfixOps
import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import akka.event.LoggingReceive
import akka.util.Timeout
import org.derekmorr.DbActor.{DBResponse, GetResource}
import org.derekmorr.WorkerActor.{DBTimeout, ProcessRequest, WorkerResponse}


/**
  * Worker Actor to process a request
  */
class WorkerActor(dbActor: ActorRef, slaTimeout: Timeout) extends Actor with ActorLogging {

  // give us some breathing room to do our work
  val breathingRoom = 50 milliseconds

  val backendTimeout = Timeout(slaTimeout.duration - breathingRoom)

  var timeoutHandleOption: Option[Cancellable] = None
  var destinationOption: Option[ActorRef] = None

  override def receive = LoggingReceive {
    case ProcessRequest(queryId)        => processRequest(queryId, sender())
    case DBTimeout(queryId)             => handleTimeout(queryId)
    case DBResponse(queryId, response)  => handleSuccess(queryId, response)
  }

  private def processRequest(queryId: String, target: ActorRef) = {

    destinationOption = Option(target)

    // tell the database actor to run the query
    dbActor ! GetResource(queryId)

    // the database might not respond in time (or at all)
    // so schedule a message to ourself to handle the timeout
    import context.dispatcher
    val timeoutId = context.system.scheduler.scheduleOnce(backendTimeout.duration) {
      self ! DBTimeout(queryId)
    }

    timeoutHandleOption = Option(timeoutId)
  }

  private def handleSuccess(queryId: String, response: Option[String]) = {
    for {
      destination   <- destinationOption
      timeoutHandle <- timeoutHandleOption
    } yield {
      destination ! WorkerResponse(queryId, response)

      // cancel the timeout message
      timeoutHandle.cancel()
    }
  }

  private def handleTimeout(queryId: String) = {
    for {
      destination <- destinationOption
    } yield {
      log.error(s"failed to get a response within $backendTimeout ; sending default msg to $destination")
      destination ! WorkerResponse(queryId, None)
    }
  }

}

object WorkerActor {

  /** factory method for WorkerActor. */
  def props(dbActor: ActorRef, slaTimeout: Timeout): Props = Props(new WorkerActor(dbActor, slaTimeout))

  case class ProcessRequest(queryId: String)
  case class WorkerResponse(queryId: String, response: Option[String])

  /** Message indicating that the database timed out */
  case class DBTimeout(queryId: String)
}
