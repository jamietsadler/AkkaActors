package playground

import akka.actor.ActorSystem

object Playground extends App {

  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem)

  val testMap = Map[String, String](
    "+" -> "plus",
    "-" -> "minus",
    "/" -> "divide"

  )

  println(testMap("+"))

  var token = "+"
  var operation = token match {
    case "+" =>
      (a: Int, b: Int) => a + b

    case _ =>
      println("Not sure")
  }


  def matchPattern(a: String) = a match {
    case "+" =>
       (a: Int, b: Int) => a + b
  }
  println(matchPattern(token)(4,5))


  val operations = List("+", "/", "-", "*")
  println(operations.contains("+"))
  println(operations.contains("hh"))

}
