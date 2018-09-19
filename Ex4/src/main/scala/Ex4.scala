import scala.io._
import akka.actor._
import akka.routing.RoundRobinPool

sealed trait Msg
    case class Calculate (fatorial : Int)    extends Msg
    case class Work (start : Int, end : Int) extends Msg
    case class Result (valorParcial : Int)   extends Msg
    case class Show (valorFinal : Int)       extends Msg

class Worker extends Actor {
    def calculate (start : Int, end : Int) : Int = {
        var result = 1
        
        for (i <- start until (end + 1))
            result *= i
        
        result
    }
    
    override def receive : Receive = {
        case Work (start, end) => sender ! Result (calculate (start, end))
    }    
}

class Master (listener : ActorRef) extends Actor {
    val worker = context.actorOf (RoundRobinPool(2).props(Props[Worker]), "Worker")
    
     override def receive : Receive = {
        case Calculate (fatorial) => {
            if (fatorial <= 1)
                if (fatorial < 0)
                    listener ! Show (-1)
                else
                    listener ! Show (1)
            else 
                worker ! Work (1, fatorial)
        }
        
        case Result (res) => {
            listener ! Show (res)
            context.stop (self)
        }
    }  
}

class Listener extends Actor {
    def receive : Receive = {
        case Show (res) => {
            if (res == -1)
                println ("O fatorial nao existe")
            else
                println (res)
            
            context.stop (self)  
        }
    }
}

object Ex4 {
    def main (args : Array[String]) : Unit = {
        val system = ActorSystem ("System")
        
        println ("Digite o Fatorial: ")
        val fatorial = StdIn.readInt
        
        val listener = system.actorOf (Props[Listener], "Listener")
        val master = system.actorOf (Props (new Master (listener)), "Master")
        
        master ! Calculate (fatorial)
    }
}
