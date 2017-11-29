import spray.http.StatusCodes.PermanentRedirect
import spray.routing._
trait ItemResource extends  customHeaderService  {

  val itemService: ItemServices

  def redirectHttp(uri: String): Route = redirect(uri, PermanentRedirect)

  def itemRoutes : Route = pathPrefix("items") {
    pathEnd{
      post {
        entity(as[Item]) {
          item => locationHeader(resource=itemService.createItem(item),definedStatus = 201,EmptyStatus =409)
        }
      }~
      get {
        complete(itemService.getAllItems())
      }
    } ~
    path(Segment) {
      id =>
        get {
          complete(itemService.getItem(id.toInt))
        } ~
        put {
          entity(as[ItemUpdate]) {
            update =>
              complete(itemService.updateItem(id.toInt,update))
          }
        } ~
          delete{
              complete(itemService.deleteItem(id.toInt))
          }
    }
  }
}
/*
  def logHeaders(innerRoute: Route): (RequestContext => Unit) = extract(_.request.headers) { headers =>
    headers.foreach(h => logger.info("header: {} = {}", h.name, h.value))
    innerRoute
  }
*/
