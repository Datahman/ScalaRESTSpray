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
          itemService.validateItemOfferExpiry(itemService.getItemTimeProperties(id.toInt),itemService.getItem(id.toInt))
          if(itemService.state=="on"){
            itemService.updateItemOffer(itemService.getItem(id.toInt))
            complete(itemService.getItem(id.toInt))
          }
          else if (itemService.state=="off") {
            itemService.deleteItem(id.toInt)
            redirectHttp("/items")
          }
          else {complete("Invalid offer state!")}
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
