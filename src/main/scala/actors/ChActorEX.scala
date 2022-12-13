package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChActorEX extends App{

  // Distributed word counting
  object WordCounterMaster {
    case class Initialize(nChildren: Int)
    case class WordCountTask(id: Int, text: String)
    case class WordCountReply(id: Int, count: Int)
  }
  class WordCounterMaster extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case Initialize(nChildren) =>
        val childrenRefs = for (i <- 1 to nChildren) yield context.actorOf(Props[WordCounterWorker], s"wcw_$i")
        context.become(withChildren(childrenRefs, 0, 0, Map()))
    }

    def withChildren(childrenRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskID: Int, requestMap: Map[Int, ActorRef]): Receive = {
      case text: String =>
        println(s"[master] I have received: $text - sending to ch $currentChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskID, text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskID = currentTaskID + 1
        val newRequestMap = requestMap + (currentTaskID -> originalSender)
        context.become(withChildren(childrenRefs, nextChildIndex, newTaskID, newRequestMap))
      case WordCountReply(id, count) =>
        val originalSender = requestMap(id)
        originalSender ! count
        context.become(withChildren(childrenRefs, currentChildIndex, currentTaskID, requestMap - id))
    }
  }

  class WordCounterWorker extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case WordCountTask(id, text) =>
        println(s"${self.path} I have received task $id with $text")
        sender() ! WordCountReply(id, text.split(" ").length)
    }

  }


  class TestActor extends Actor {
    import WordCounterMaster._

    override def receive: Receive = {
      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)
        val texts = List("assada", "dsf  dsfsaf  erwgw ad f sdf sg sdg s dg", "asd sdf sf gher h")
        texts.foreach(text => master ! text)
      case count: Int =>
        println(s"[test actor] I received a reply: $count")
    }

  }

  val system = ActorSystem("roundrobinwordcount")
  val testActor = system.actorOf(Props[TestActor], "testactor")
  testActor ! "go"


}
