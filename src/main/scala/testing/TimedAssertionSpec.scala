package testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._

import scala.util.Random

class TimedAssertionSpec extends TestKit(ActorSystem("TimedAssertionSpec"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
{
  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import TimedAssertionSpec._

  "A worker actor" should {
    val workerActor = system.actorOf(Props[WorkerActor])
    "reply with the meaning of life" in {
      within(500 millis, 1 second){
        workerActor ! "work"
        expectMsg(WorkResult(42))
      }
    }
    "reply with valid work at a reasonable cadence" in {
      within (1 second) {
        workerActor ! "workSequence"
        val results: Seq[Int] = receiveWhile[Int](max=2 seconds, idle = 500 millis, messages = 10) {
          case WorkResult(result) => result
        }

        assert(results.sum > 5)
      }

    }
  }

}


object TimedAssertionSpec {
  case class WorkResult(result: Int)

  class WorkerActor extends Actor {
    override def receive: Receive = {
      case "work" =>
        Thread.sleep(500)
        sender() ! WorkResult(42)

      case "workSequence" =>
        val r = new Random()
        for (_ <- 1 to 10) {
          Thread.sleep(r.nextInt(50))
          sender() ! WorkResult(1)
        }
    }

  }

}
