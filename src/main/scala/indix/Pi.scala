package indix

import akka.actor._
import indix.Messages.Calculate

object Pi extends App {

  calculate(noOfWorkers = 4, noOfElements = 1000, noOfMessages = 1000)

  def calculate(noOfWorkers: Int, noOfElements: Int, noOfMessages: Int) {
    // Create an akka system
    val system = ActorSystem("PiSystem")

    // create the result listener, which will print the result and
    // shutdown the system
    val listener = system.actorOf(Props[Listener], name = "listener")

    // Create the master
    val master = system.actorOf(Props(new Master(
      noOfWorkers, noOfMessages, noOfElements, listener
    )), name = "Master")

    // Start the calculation
    master ! Calculate
  }
}
