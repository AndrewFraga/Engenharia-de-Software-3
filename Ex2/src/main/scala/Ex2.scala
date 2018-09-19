import scala.io._
import scala.util._
import akka.actor._
import akka.routing.RoundRobinPool

sealed trait Msg
    case object Gerar                extends Msg
    case class  Chute (numero : Int) extends Msg
    case class  Show (qnt : Int)     extends Msg

class Jogador extends Actor {
    override def receive : Receive = {
        case Gerar => {
            val n = 1 + Random.nextInt (199)
            sender ! Chute (n)
        }
    }
}

class Master (listener: ActorRef) extends Actor {
    var acertos = 0
    var chutes  = 0
    val n = 1 + Random.nextInt (199)
    
    val jogador = context.actorOf (RoundRobinPool(32).props(Props[Jogador]), "Jogador")
    
    override def receive : Receive = {
        case Gerar => {
            for (i <- 0 until 32)
                jogador ! Gerar
        }
        
        case Chute (num) => {
            chutes += 1
            if (num == n)
                acertos += 1
            
            if (chutes == 32) {
                listener ! Show (acertos)
                context.stop (self)
            }
        }
    }
}

class Listener extends Actor {
    def receive : Receive = {
        case Show (res) => {
            if (res == 0) {
                println ("Nenhum jogador acertou o numero")
                context.stop (self)
            }
            else {
                println ("Acertaram " + res + " vezes")
                context.stop(self)
            }
        }
    }
}

object Ex2 {
    def main (args : Array[String]) : Unit = {
        val system = ActorSystem ("System")
 
        val listener = system.actorOf (Props[Listener], "Listener")
        val master = system.actorOf (Props (new Master (listener)), "Master")
 
        master ! Gerar
    }
}