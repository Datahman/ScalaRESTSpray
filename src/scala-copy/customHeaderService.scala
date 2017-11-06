
import spray.http.HttpHeaders
import spray.routing.HttpService

import scala.concurrent.{ExecutionContext, Future}
import spray.routing._

trait customHeaderService extends HttpService with JsonSupport {
  implicit val ec: ExecutionContext

  def locationHeader[T](resource: Future[Option[T]], definedStatus: Int, EmptyStatus: Int): Route = {
    onSuccess(resource) {
      futureItem =>
        futureItem match {
          case Some(t) => locationHeader(definedStatus,futureItem)
          case None => complete(EmptyStatus,None)
        }
    }
  }

  def locationHeader[T](status: Int, resource: T): Route = {
    requestInstance {
      request =>
        val location = request.uri.copy(path = request.uri.path / resource.toString)
        respondWithHeader(HttpHeaders.Location(location)) {
          complete(status,None)
        }
    }

  }

}

import java.text.SimpleDateFormat

import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Formats}
import spray.httpx.Json4sSupport

trait JsonSupport extends Json4sSupport {

  implicit def json4sFormats: Formats = customDateFormat

  val customDateFormat = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  }

}

