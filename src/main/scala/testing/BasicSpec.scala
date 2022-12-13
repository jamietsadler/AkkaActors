package testing

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import testing.BasicSpec.SimpleActor

import scala.concurrent.duration._
import scala.util.Random

class BasicSpec extends TestKit(ActorSystem("BasicSpec")) // Normal name testing 'something'Spec
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
{
  // basic setup
  override def afterAll(): Unit = {
        TestKit.shutdownActorSystem(system)
  }

  /*"The thing being tested" should {
    "do this" in {
      // testing scenario
    }
    "do another thing..."{
      // testing scenario
    }
  }*/
  import BasicSpec._
  "A simple actor" should {
    "send back the same message" in {
      val echoActor = system.actorOf(Props[SimpleActor])
      val message = "hello, test"
      echoActor ! message
      expectMsg(message)
    }
  }

  "A Blackhole actor" should {
    "send back some message" in {
      val blackhole = system.actorOf(Props[Blackhole])
      val message = "hello, test"
      blackhole ! message
      //expectMsg(message)
      expectNoMessage(1 second)
    }
  }

  // message assertions
  "A lab test actor" should {
    val labTestActor = system.actorOf(Props[LabTestActor])
    "turn a string into uppercase" in {
      labTestActor ! "I love Akka"
      val reply = expectMsgType[String]

      assert (reply == "I LOVE AKKA")
    }

    "reply to a greeting" in {
      labTestActor ! "greeting"
      expectMsgAnyOf("hi", "hello")
    }

    "reply with favourite tech" in {
      labTestActor ! "favouriteTech"
      expectMsgAllOf("Scala", "Akka")
    }

    "reply in different way" in {
      labTestActor ! "favouriteTech"
      val messages = receiveN(2)  // Seq[Any]
    }
  }

}


object BasicSpec {
  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => sender() ! message
    }
  }

  class Blackhole extends Actor {
    override def receive: Receive = Actor.emptyBehavior
  }

  class LabTestActor extends Actor {
    val random = new Random()

    override def receive: Receive = {
      case "greeting" =>
        if (random.nextBoolean()) sender() ! "hi" else sender() ! "hello"
      case message: String => sender() ! message.toUpperCase()
      case "favouriteTech" =>
        sender() ! "Scala"
        sender() ! "Akka"
    }

  }
}