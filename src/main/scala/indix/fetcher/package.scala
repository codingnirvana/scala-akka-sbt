package indix

import akka.util.Duration
import actors.threadpool.TimeoutException
import akka.dispatch.{ExecutionContext, Promise}

package object fetcher {

  /** Create a [[akka.dispatch.Promise]] that will completed after [[akka.util.Duration]].
   *
   * Schedule a handler that completes the [[akka.dispatch.Promise]] after [[akka.util.Duration]],
   * and also try to cancel the scheduled handler if the [[akka.dispatch.Promise]] completes.
   *
   * @return [[akka.dispatch.Promise]]
   */
  def withTimeout[T](at: Duration, scheduler: akka.actor.Scheduler)(implicit executor: ExecutionContext): Promise[T] = {
    val p = Promise[T]()
    val cancellable = scheduler.scheduleOnce(at) { p.tryComplete(Left(new TimeoutException("Scheduled timeout"))) }
    p.onComplete(_ => cancellable.cancel)
    p
  }
}
