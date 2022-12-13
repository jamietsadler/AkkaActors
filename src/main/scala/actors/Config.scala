package actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Config extends App{

  class SimpleLog extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val configString =
    """
      | akka {
      | loglevel = "DEBUG"
      | }
      |
      |""".stripMargin

  val config = ConfigFactory.parseString(configString)
  val system = ActorSystem("configurationDemo", ConfigFactory.load(config))

  val actor = system.actorOf(Props[SimpleLog])
  actor ! "A message to remember"

  val defaultConfigFileSystem = ActorSystem("DefaultConfigFile")
  val defaultConfigActor = defaultConfigFileSystem.actorOf(Props[SimpleLog])
  defaultConfigActor ! "Remember me"
}
