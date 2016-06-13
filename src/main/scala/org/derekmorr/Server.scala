package org.derekmorr

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}
import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import akka.routing.FromConfig
import akka.util.Timeout
import org.derekmorr.WorkerActor.{ProcessRequest, WorkerResponse}

/**
 * Dummy server using actors.
 */
object Server extends App {

  val actorSystem = ActorSystem("server")
  import actorSystem.dispatcher

  implicit val timeout = Timeout(150 milliseconds)

  (1 to 30) foreach { i =>

    val db = actorSystem.actorOf(Props[DbActor])
    val worker = actorSystem.actorOf(WorkerActor.props(db, timeout))

    // currently, actors are untyped, so we have to cast the Future we get back.
    // The .mapTo[] bit at the end of the next line is casting; it's not map()'ing over anything.
    val f: Future[WorkerResponse] = (worker ? ProcessRequest("foobar")).mapTo[WorkerResponse]
    f onComplete {
      case Success(WorkerResponse(id, Some(x))) =>
        println(s"Got response: $x")

      case Success(WorkerResponse(id, None)) =>
        println("DB Actor didn't respond in time, but Worker met its SLA.")

      case Failure(ex)  =>
        println("Worker violated its SLA.")
    }

    // cleanup the actor
    f.onComplete { _ => worker ! PoisonPill }
  }

  Thread.sleep(2000)
  actorSystem.terminate()
}
