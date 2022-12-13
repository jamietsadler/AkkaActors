package actors

import actors.ActorCapabilities.system
import actors.ChangingActorBehaviour.Mum.{MomStart, VEGETABLE}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import javax.security.auth.callback.ChoiceCallback

object ChangingActorBehaviour extends App {

  object FussyKid {
    case object KidAccept
    case object KidReject
    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {
    import FussyKid._
    import Mum._

    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  object Mum {
    case class MomStart(kidRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mum extends Actor {
    import Mum._
    import FussyKid._
    override def receive: Receive = {
      case MomStart(kidRef) =>
        // test our interaction
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("Do you want to play?")
      case KidAccept => println("Happy!!!")
      case KidReject => println("Not happy")
    }
  }

  // STATELESS fussyKid

  class StatelessFussyKid extends Actor {

    import FussyKid._
    import Mum._

    override def receive: Receive = happyReceive

    def happyReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive)
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) => context.become(sadReceive, false)
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_) => sender() ! KidReject
    }

  }

  val system = ActorSystem("changingActorBehaviour")

  val fussyKid = system.actorOf(Props[FussyKid], "fussyKid")
  val statelessFussyKid = system.actorOf(Props[FussyKid], "fussyKidStateless")

  val mum = system.actorOf(Props[Mum], "mum")

  mum ! MomStart(fussyKid)
  mum ! MomStart(statelessFussyKid)




  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {

    import Counter._

    var count = 0

    override def receive: Receive = countReceive(0)

   def countReceive(currentCount: Int): Receive = {
     case Increment =>
       println(s"[$currentCount] incrementing")
       context.become(countReceive(currentCount + 1))
     case Decrement =>
       println(s"[$currentCount] decrementing")
       context.become(countReceive(currentCount - 1))
     case Print => println(s"[counter] my current one is $currentCount")
   }
  }

  import Counter._

  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 3).foreach(_ => counter ! Decrement)

  counter ! Print



  case class Vote(candidate: String)
  case object VoteStatusRequest
  /*class Citizen extends Actor {
    var candidate: Option[String] = None
    override def receive: Receive = {
      case Vote(c) => candidate = context.become(voted(Some(c)))
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
    }
    def voted(candidate: String): Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
    }
  }

  case class VoteStatusReply(candidate: Option[String])
  case class AggregateVotes(citizens: Set[ActorRef])
  class VoteAggregator extends Actor {
    var stillWaiting: Set[ActorRef] = Set()
    var currentStats: Map[String, Int] = Map()

    override def receive: Receive = {
      case AggregateVotes(citizens) =>
        stillWaiting = citizens
        citizens.foreach(citizenRef => citizenRef ! VoteStatusRequest)
      case VoteStatusReply(None) =>
        // citizen hasnt voted yet
        sender() ! VoteStatusRequest // could end up in infinite loop
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate, 0)
        currentStats = currentStats + (candidate -> (currentVotesOfCandidate + 1))
        if (newStillWaiting.isEmpty) {
          println(s"[aggregator] poll stats: $currentStats")
        } else {
          stillWaiting = newStillWaiting
        }
    }
  }

  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])*/
/*
  alice ! Vote("Martin")
  bob ! Vote("Charlie")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = system.actorOf(Props[VoteAggregator])
  voteAggregator ! AggregateVotes(Set(alice, charlie, bob, daniel))*/
}
