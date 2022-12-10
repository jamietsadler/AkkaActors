package rsppr

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MTR extends App {

  val aThread = new Thread(() => println("running in parallel"))
  aThread.start()
  aThread.join()

  val threadHello = new Thread(() => (1 to 1000).foreach(_ => println("Hello")))
  val threadGoodbye = new Thread(() => (1 to 1000).foreach(_ => println("Goodbye")))
  threadHello.start()
  threadGoodbye.start()

  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.amount -= money

    def safeWithdraw(money: Int) = this.synchronized {
      this.amount -= money
    } // makes thread safe

    // inter-thread communication

    import scala.concurrent.ExecutionContext.Implicits.global

    val future = Future {
      // long computation on another thread
      42
    }

    // callbacks
    future.onComplete {
      case Success(42) => println(" I found the meaning of life")
      case Failure(_) => println("Something hapened with the meaning of life")
    }

    val aProcessedFuture = future.map(_ + 1)
    val aFlatFuture = future.flatMap { value =>
      Future(value + 2)
    }

    val filteredFuture = future.filter(_ % 2 == 2)


  }
}
