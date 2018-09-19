import akka.actor._
import scala.io._

sealed trait Message
    case class Start (start : String)       extends Message
    case class Mensagem (mensagem : String) extends Message

class DataSource (prox : ActorRef) extends Actor {
    override def receive : Receive = {
        case Start (txt) => prox ! Mensagem (txt)
    }
}

class LowerCase (prox : ActorRef) extends Actor {
    override def receive : Receive = {
        case Mensagem (txt) => {
            println (txt.toLowerCase)
            
            if(prox == null){
                println("Fim do Processo!")
                context.stop (self)
            }
            else
                prox ! Mensagem(txt.toLowerCase)
        }
    }
}

class UpperCase (prox : ActorRef) extends Actor {
    override def receive: Receive = {
        case Mensagem (txt) => {
            println (txt.toUpperCase)
            if (prox == null) {
                println("Fim do Processo!")
                context.stop(self)
            }
            else
                prox ! Mensagem(txt.toUpperCase)
        }
    }
}

class Duplicate (prox : ActorRef) extends Actor {
    override def receive : Receive = {
        case Mensagem (txt) => {
            println(txt + txt)
            if (prox == null) {
                println("Fim do Processo!")
                context.stop (self)
            }
            else
               prox ! Mensagem (txt + txt)
        }
    }
}

class FilterVowels (prox : ActorRef) extends Actor {
    override def receive : Receive = {
        case Mensagem (txt) => {
            println (txt.replaceAll ("[AEIOUaeiou]", ""))
            
            if (prox == null) {
                println ("Fim do Processo!")
                context.stop (self)
            }
            else
               prox ! Mensagem(txt.replaceAll("[AEIOUaeiou]", ""))
        }
    }
}
   
object Ex3{
    def main (args : Array[String]) : Unit = {
        val system = ActorSystem("System")
        
        println("Digite o texto: ")
        val txt = StdIn.readLine
        
        val filterVowels = system.actorOf(Props(new FilterVowels(null)),"filterVowels")
        val duplicate    = system.actorOf(Props(new Duplicate(filterVowels)),"duplicate")
        val upperCase    = system.actorOf(Props(new UpperCase(duplicate)),"upperCase")
        val lowerCase    = system.actorOf(Props(new LowerCase(upperCase)),"lowerCase")
        
        val dataSource = system.actorOf(Props(new DataSource(lowerCase)),"dataSource")
        
        dataSource ! Start (txt)
    }
}