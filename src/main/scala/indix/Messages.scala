package indix

import akka.util.Duration

object Messages {

  sealed trait PiMessage

  case object Calculate extends PiMessage

  case class Work(start: Int, noOfElements: Int) extends PiMessage

  case class Result(value: Double) extends PiMessage

  case class PiApproximation(value: Double, duration: Duration) extends PiMessage

}