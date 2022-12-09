package rsppr

import scala.concurrent.Future

object GeneralRec extends App {

  val aCondition: Boolean = false

  // types
  // -Unit
  val theUnit = println("Akka")

  def aFunction(x: Int): Int = x + 1

  // recursion - TAIL recursion
  def factorial(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else factorial(n - 1, acc * n)

  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("Crunch")
  }

  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("roar")
  }

  aCarnivore eat aDog

  // generics
  abstract class MyList[+A]

  object MyList

  case class Person(name: String, age: Int)

  // Exceptions
  val aPotentialException = try {
    throw new RuntimeException("asdadasdad")
  } catch {
    case e: Exception => " I caught an exception"
  } finally {
    println("some logs")
  }

  // Functional Programming
  // functions are treated as objects, allows for functional programming on top of the JVM

  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val incrementer2 = new ((Int) => Int) {
    override def apply(v1: Int): Int = v1 + 2
  }

  val incremented = incrementer(42)
  // incrementer.apply(42)

  val anonymousIncrementer = (x: Int) => x + 1

  // FP is all about working with functions as first class
  List(1, 2, 3).map(incrementer)

  println(List(1, 2, 3).map(num => List("a", "b", "c")))
  println(List(1, 2, 3).flatMap(num => List("a", "b", "c")))

  val bob = Person("Bob", 30)
  val greeting = bob match {
    case Person(n, _) => s"Hi my name is $n"
    case _ => "Blah"
  }

  // partial functions - operate only on subset of given domain, based on pattern matching
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 3 => 999
  }

  val pf = (x: Int) => x match {
    case 1 => 42
    case 2 => 65
    case 3 => 999
  }

  val function: (Int => Int) = partialFunction
  val modifiedList = List(1,2,3).map {
    case 1 => 42
    case _ => 0
  }

  // lifting
  val lifted = partialFunction.lift
  lifted(2) // Some(65)
  lifted(500) // None

  //orElse
  val pfChain = partialFunction.orElse[Int, Int] {
    case 60 => 9000
  }

  // type aliases
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case 1 => println("hello")
    case _ => println("confused")
  }

  // implicits
  implicit val timeout = 3000
  def setTimeout(f: () => Unit)(implicit timeout: Int) = f()

  setTimeout(() => println("timeout"))

  case class Person2 (name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(string: String): Person2 = Person2(string)
  "peter".greet

  implicit class Dog2(name: String) {
    def bark = println("bark!")
  }

  "Lassie".bark

  // organise
  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  List(1,2,3).sorted

  // imported scope
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future {
    println("hello future")
  }


}