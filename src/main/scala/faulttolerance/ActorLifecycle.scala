package faulttolerance

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object ActorLifecycle extends App {



  object StartChild
  class LifecycleActor extends Actor with ActorLogging {
    override def preStart(): Unit = log.info("I am starting")
    override def postStop(): Unit = log.info(" I have stopped")
    override def receive: Receive = {
      case StartChild => context.actorOf(Props[LifecycleActor], "child")
    }
  }

  val system = ActorSystem("LifecycleDemo")
  val parent = system.actorOf(Props[LifecycleActor])

  parent ! StartChild
  parent ! PoisonPill
  object Fail
  object FailChild
  object Check

  class Parent extends Actor {
    private val child = context.actorOf(Props[Child], "supervised child")
    override def receive: Receive = {
      case FailChild => child ! Fail
    }
  }

  class Child extends Actor with ActorLogging {
    override def preStart(): Unit = log.info("I am starting")
    override def postStop(): Unit = log.info(" I have stopped")
    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(s"supervised actor restarting because of ${reason.getMessage}")

    override def receive: Receive = {
      case Fail =>
        log.warning("child will fail now")
        throw new RuntimeException(" I failed")
      case Check =>
        log.info("alive and kicking")

    }
  }

  val supervisor = system.actorOf(Props[Parent], "supervisor")
  supervisor ! FailChild
  supervisor ! Check

}
