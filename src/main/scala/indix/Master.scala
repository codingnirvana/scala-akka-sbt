package indix

import akka.actor.{Props, Actor, ActorRef}
import akka.routing.RoundRobinRouter
import indix.Messages.{PiApproximation, Result, Work, Calculate}
import akka.util.duration._

class Master(noOfWorkers: Int,
             noOfMessages: Int,
             noOfElements: Int,
             listener: ActorRef) extends Actor {

  var pi: Double = _
  var noOfResults: Int = _
  val start: Long = System.currentTimeMillis

  val workerRouter = context.actorOf(
    Props[Worker].withRouter(RoundRobinRouter(noOfWorkers)),
    name = "workerRouter"
  )

  def receive = {
    case Calculate ⇒
      for (i ← 0 until noOfMessages)
        workerRouter ! Work(i * noOfElements, noOfElements)

    case Result(value) ⇒
      pi += value
      noOfResults += 1
      if (noOfResults == noOfMessages) {
        // Send the result to the listener
        listener ! PiApproximation(pi, duration = (System.currentTimeMillis - start).millis)
        // Stops this actor and all its supervised children
        context.stop(self)
      }
  }
}

