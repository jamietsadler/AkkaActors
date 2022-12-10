package rsppr

import scala.concurrent.Future

object ThreadLimitations extends App{

  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.amount -= money
    def deposit(money: Int) = this.amount += money
    def getAmount = amount
  }

  // OOP encapsulation is broken in multithreaded environments
  var task: Runnable = null

  val runningThread: Thread = new Thread(() => {
    while(true) {
      while (task == null) {
        runningThread.synchronized {
          println("[background] waiting for a task")
          runningThread.wait()
        }
      }
      task.synchronized {
        println("[background] I have a task")
        task.run()
        task = null
      }
    }
  })

  def delegateToBackgroundThread(r: Runnable) = {
    if (task == null) task = r
    runningThread.synchronized {
      runningThread.notify()
    }
  }

  runningThread.start()
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println(42))
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println("This should run in the background"))

  // 1M numbers inbetween 10 threads
  import scala.concurrent.ExecutionContext.Implicits.global

  val futures = (0 to 9)
    .map(i => 100000*i until 100000*(i + 1))
    .map(range => Future {
      if (range.contains(546735)) throw new RuntimeException("Invalid number")
      range.sum
    })

  val sumFuture = Future.reduceLeft(futures)(_ + _)
  sumFuture.onComplete(println)

  // Reason threading sucks
  // -OOP not encapsulated
  // -deadlocks, livelocks
  // -difficult in distributed environments
  // -hard, error-prone
  // -never feels first-class
  // -tracing and root-causing errors is difficult


}
