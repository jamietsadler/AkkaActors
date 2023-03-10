package infrasctructure

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.util.Random

object Dispatchers extends App{
  class Counter extends Actor with ActorLogging{
    var count = 0

    override def receive: Receive = {
      case message =>
        count += 1
        log.info(s"[$count] $message")
    }
  }

  val system = ActorSystem("DispatchersDemo", ConfigFactory.load().getConfig("DisptachersDemo"))

  val actors = for (i <- 1 to 10) yield system.actorOf(Props[Counter].withDispatcher("myDispatcher"), s"counter_$i")

  val r = new Random()
  for (i <- 1 to 1000) {
    actors(r.nextInt(10)) ! i
  }
}
