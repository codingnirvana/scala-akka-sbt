import indix.fetcher.{URLFetched, FetchURL, URLFetcher}
import org.scalatest.matchers._
import org.scalatest._

import akka.actor._
import akka.util.Timeout
import akka.testkit.{ImplicitSender, TestKit}
import akka.testkit.TestActor.AutoPilot

import javax.servlet.http.HttpServletResponse

class URLFetcherSpec extends TestKit(ActorSystem("URLFetcherSpec")) with FlatSpec with ShouldMatchers
with BeforeAndAfterAll with ImplicitSender {
  behavior of "URLFetcher"

  var httpServer: TestHttpServer = null

  override def beforeAll = {
    httpServer = new TestHttpServer()
    httpServer.start()
  }

  override def afterAll = {
    system.shutdown()
    httpServer.stop()
    httpServer = null
  }

  implicit val timeout = Timeout(system.settings.config.getMilliseconds("akka.timeout.test"))

  it should "fetch an url" in {
    val fetcher = system.actorOf(Props[URLFetcher])
    within(timeout.duration) {
      fetcher ! FetchURL(httpServer.resolve("/hello"))
      expectMsgType[URLFetched] match {
        case URLFetched(url, status, headers, body) =>
          status should be(HttpServletResponse.SC_OK)
          body should be("Hello\n")
      }
    }
    system.stop(fetcher)
  }

  it should "handle a 404" in {
    val fetcher = system.actorOf(Props[URLFetcher])
    within(timeout.duration) {
      fetcher ! FetchURL(httpServer.resolve("/nothere"))
      expectMsgType[URLFetched] match {
        case URLFetched(url, status, headers, body) =>
          status should be(HttpServletResponse.SC_NOT_FOUND)
      }
    }
    system.stop(fetcher)
  }

  it should "fetch many urls in parallel" in {
    // the httpServer only has a fixed number of threads so if you make latency
    // or number of requests too high, the futures will start to time out
    httpServer.withRandomLatency(300) {
      val fetcher = system.actorOf(Props[URLFetcher])
      val numToFetch = 500

      within(timeout.duration) {
        var completed: List[Int] = List()

        // check all replies that we get
        setAutoPilot(new AutoPilot {
          var numProcessedMsgs = 0

          def run(sender: ActorRef, msg: Any): Option[AutoPilot] = {

            msg match {
              case URLFetched(url, status, headers, body) =>
                status should be(HttpServletResponse.SC_OK)
                val expected = url.getQuery.split("=").last.toInt
                body should be(expected.toString)
                completed = completed :+ expected
              case whatever =>
                throw new IllegalStateException("Unexpected reply to url fetch: " + whatever)
            }
            numProcessedMsgs = numProcessedMsgs + 1
            if (numProcessedMsgs < numToFetch)
              Option(this)
            else
              None
          }
        })

        // send all messages
        for (i <- 1 to numToFetch)
          fetcher ! FetchURL(httpServer.resolve("/echo", "what", i.toString))
        // wait for all replies
        receiveN(numToFetch)

        completed.length should be(numToFetch)
        // the random latency should mean we completed in semi-random order
        completed should not be (completed.sorted)
      }
      system.stop(fetcher)
    }
  }
}