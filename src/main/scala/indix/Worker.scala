package indix

import akka.actor.Actor
import indix.Messages.{Result, Work}

class Worker extends Actor {

  def receive = {
    case Work(start, noOfElements) => sender ! Result(calculatePiFor(start, noOfElements))
  }

  def calculatePiFor(start: Int, noOfElements: Int): Double = {
    var acc = 0.0
    for (i <- start until (start + noOfElements)) {
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    }
    acc
  }
}