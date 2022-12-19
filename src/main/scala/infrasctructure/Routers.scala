package infrasctructure

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, Broadcast, FromConfig, RoundRobinGroup, RoundRobinPool, RoundRobinRoutingLogic, Router}

object Routers extends App {

  class Master extends Actor {
    private val slaves = for(i <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave], s"slave_$i")
      context.watch(slave)
      ActorRefRoutee(slave)
    }
    private val router = Router(RoundRobinRoutingLogic(), slaves)

    override def receive: Receive = {
      case Terminated(ref) => // replace with another slave
        router.removeRoutee(ref)
        var newSlave = context.actorOf(Props[Slave])
        context.watch(newSlave)
        router.addRoutee(newSlave)
      case message =>
        router.route(message, sender())
    }
  }

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("RoutersDemo")
  val master = system.actorOf(Props[Master])

  for (i <- 1 to 10){
    master ! s"[$i] Hello from the world"
  }

  // programmatically
  val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]))
  for (i <- 1 to 10){
    poolMaster ! s"[$i] Hello from the world"
  }

  // from config
  val poolMaster2 = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")
  for (i <- 1 to 10) {
    poolMaster2 ! s"[$i] Hello from the world again"
  }
  val slaveList = (1 to 5).map(i => system.actorOf(Props[Slave], s"slave_$i")).toList

  val slavePaths = slaveList.map(slaveRef => slaveRef.path.toString)
  val groupMaster = system.actorOf(RoundRobinGroup(slavePaths).props())

  val groupMaster2 = system.actorOf(FromConfig.props(), "groupMaster2")
  for (i <- 1 to 10) {
    groupMaster2 ! s"[$i] Hello from the world"
  }

  groupMaster2 ! Broadcast("hello everyone")
}
