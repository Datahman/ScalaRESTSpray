import scala.concurrent.duration.DurationInt
import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.{ExecutionContext, Future}

object Main extends App {

  val config = ConfigFactory.load()
  val host = config.getString("service.host")
  val port = config.getInt("service.port")

  // ActorSystem to host application in
  implicit val  system = ActorSystem("item-offer-service")
  implicit val ec = system.dispatcher

  // Create and start Actor services
  val api = system.actorOf(Props(new RestInterface)) // Collection of routes and operations
  implicit val timeout = Timeout.durationToTimeout(15000.millis)

  //IO(Http) ? Http.Bind(api,interface=host,port=port)
  IO(Http).ask(Http.Bind(api,interface = host,port=port)).flatMap{
    case a: Http.Bound ⇒ Future.successful(a)
    case Http.CommandFailed(a: Http.Bind) ⇒
      Future.failed(new RuntimeException("Rest interface binding failed at" + s"$host:$port")) }


}
