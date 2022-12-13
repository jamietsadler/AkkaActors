package actors

import actors.ChActors.CreditCard.{AttachToAccount, CheckStatus}
import actors.ChActors.NaiveBankAccount
import actors.ChActors.Parent.CreateChild
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChActors extends App {

  // Actors can create other actors
  object Parent {
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }
  class Parent extends Actor {
    import Parent._

    override def receive: Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child")
        // create new actor here
        val childRef = context.actorOf(Props[Child], name)
        context.become(withChild(childRef))
    }

    def withChild(childRef: ActorRef): Receive = {
      case TellChild(message) => childRef forward message
    }

  }

  class Child extends Actor {
    override def receive: Receive = {
      case message => println(s"${self.path} I got: $message")
    }

  }

  import Parent._

  val system = ActorSystem("ParentChildDemo")
  val par = system.actorOf(Props[Parent], "parent")
  par ! CreateChild("child")
  par ! TellChild("hey")

  /*
  Guardian Actors:
    - /system = system guardian
    - /user = user-level guardian
    - / = the root guardian
   */

  // Actor Selection
  val childSelection = system.actorSelection("/user/parent/child")
  childSelection ! "Helloooo!"

  // Never pass mutable actor states or the 'this' reference to child actors
  // could break actor encapsulate
  object NaiveBankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object InitialiseAccount
  }

  class NaiveBankAccount extends Actor {
    import NaiveBankAccount._
    import CreditCard._

    var amount = 0
    override def receive: Receive = {
      case InitialiseAccount =>
        val creditCardRef = context.actorOf(Props[CreditCard])
        creditCardRef ! AttachToAccount(this)
      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)
    }

    def deposit(funds: Int) = amount += funds
    def withdraw(funds: Int) = amount -= funds

  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount)
    case object CheckStatus
  }
  class CreditCard extends Actor {
    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachedTo(account))
    }

    def attachedTo(account: NaiveBankAccount): Receive = {
      case CheckStatus =>
        println(s"${self.path} your message has been processed")
        account.withdraw(1)
    }
  }

  import NaiveBankAccount._
  import CreditCard._

  val bankAccountRef = system.actorOf(Props[NaiveBankAccount], "account")
  bankAccountRef ! InitialiseAccount
  bankAccountRef ! Deposit(100)

  Thread.sleep(500)
  val ccSelection = system.actorSelection("/user/account/card")
  ccSelection ! CheckStatus


}
