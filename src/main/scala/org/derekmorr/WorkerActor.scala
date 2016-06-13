package org.derekmorr

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import org.derekmorr.WorkerActor.ProcessRequest


/**
 * Worker Actor to process a request
 */
class WorkerActor(dbActor: ActorRef, slaTimeout: Timeout) extends Actor with ActorLogging {

  override def receive = {
    case ProcessRequest(queryId) => ???
  }
  
}

object WorkerActor {

  /** factory method for WorkerActor. */
  def props(dbActor: ActorRef, slaTimeout: Timeout): Props = Props(new WorkerActor(dbActor, slaTimeout))

  case class ProcessRequest(queryId: String)
  case class WorkerResponse(queryId: String, response: Option[String])
}
