package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorsIn extends App {

  // actor systems
  val actorSystem = ActorSystem("firstActorSystem") // controls number of threads under the hood
  println(actorSystem.name)

  // create actors
  class WordCountActor extends Actor {
    var totalWords = 0
    def receive: PartialFunction[Any, Unit] = {
      case message: String => totalWords += message.split(" ").length
      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }

  // Instantiate actors
  val wordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "wordCounter") // can only speak to actor through reference
  val wordCounter2: ActorRef = actorSystem.actorOf(Props[WordCountActor], "wordCounter2") // can only speak to actor through reference

  // communicate
  wordCounter ! "Akka is interesting"
  wordCounter2 ! "A different message"
  // akka sends messages to actors asynchronously

  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"Hi, my name is $name")
      case _ =>
    }
  }

  object Person {
    def props(name: String) = Props(new Person(name))
  }

  val person = actorSystem.actorOf((Person.props("Bob")))
  person ! "hi"
}
