package infrasctructure

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

object Mailboxes extends App {

  val system = ActorSystem("MailboxDemo")

  class SimpleActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case message =>
        log.info(message.toString)
    }
  }

  class SupportTicketPriorityMailbox(settings: ActorSystem.Settings, config: Config)
    extends UnboundedPriorityMailbox(
      PriorityGenerator {
        case message: String if message.startsWith("[P0]") => 0
        case message: String if message.startsWith("[P1]") => 1
        case message: String if message.startsWith("[P2]") => 2
        case message: String if message.startsWith("[P3]") => 3
        case _ => 4
      })

  val supportTicketLogger = system.actorOf(Props[SimpleActor].withDispatcher("support-ticket-dispatcher"))
  supportTicketLogger ! "[P3] this thing would be nice to know"
  supportTicketLogger ! "[P0] this needs to be solved now"

  case object ManagementTicket extends ControlMessage



}
