import spray.routing.{HttpServiceActor, Route}

import scala.concurrent.ExecutionContext

class RestInterface(implicit val ec: ExecutionContext) extends HttpServiceActor with Resources {
  def receive = runRoute(itemRoutes) // Receive routes after execution of CRUD operations
  val itemService = new ItemServices
  val routes: Route = itemRoutes

}
trait Resources extends ItemResource
