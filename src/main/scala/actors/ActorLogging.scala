package actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object ActorLogging extends App{

  class ExplicitLogging extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => logger.info(message.toString)
    }
  }

  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[ExplicitLogging])

  actor ! "Logging a simple message"

  // Actor Logging
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case (a, b) => log.info("These 2 things: {} and {}", a, b)
      case message => log.info(message.toString)
    }
  }

  val simpleActor = system.actorOf(Props[ActorWithLogging])
  simpleActor ! "Logging simple messge by extending a trait"

  val jsonConfig = ConfigFactory.load("json/jsonConfig.json")
  println(s"json config: ${jsonConfig.getString("akka.loglevel")}")

}
