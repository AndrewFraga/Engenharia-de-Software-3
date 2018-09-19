import akka.actor._
import scala.io._

sealed trait Msg
    case object Start           extends Msg
    case class  Ping (qt : Int) extends Msg
    case class  Pong (qt : Int) extends Msg

class PlayerA (opponent : ActorRef) extends Actor {
    override def receive : Receive = {
        case Start     => { println("Go"); opponent ! Ping(0) }
        case Pong (qt) => {
            if (qt == 2000) {
                println("You Win!")
                context.stop(self)
            }
            else {
                println("Ping - " + qt)
                sender ! Ping (qt + 1)
            }
        }
    }
}

class PlayerB extends Actor {
    override def receive : Receive = {
        case Ping (qt) => {
            if (qt == 2000) {
                println("You Win!")
                context.stop(self)
            }
            else {
                println("Pong - " + qt)
                sender ! Pong (qt + 1)
            }
        }
    }
}

object Ex1 {
    def main (args : Array[String]) : Unit = {
        val system = ActorSystem("System")
        
        val playerB = system.actorOf(Props[PlayerB], "PlayerB")
        val playerA = system.actorOf(Props(new PlayerA(playerB)), "PlayerA")
        
        playerA ! Start
    }
}