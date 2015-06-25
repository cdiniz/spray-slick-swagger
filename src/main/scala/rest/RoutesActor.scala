package rest

import akka.actor.{ Actor}
import com.wordnik.swagger.annotations._
import entities.JsonProtocol
import persistence.entities._
import scala.concurrent.Future
import com.typesafe.scalalogging.LazyLogging
import spray.httpx.SprayJsonSupport
import spray.routing._
import spray.http._
import MediaTypes._
import utils.{PersistenceModule, Configuration}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import spray.http.StatusCodes._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.gettyimages.spray.swagger._
import com.wordnik.swagger.model.ApiInfo
import scala.reflect.runtime.universe._

class RoutesActor(modules: Configuration with PersistenceModule) extends Actor with HttpService with LazyLogging {
  import JsonProtocol._
  import SprayJsonSupport._

  def actorRefFactory = context

  implicit val timeout = Timeout(5.seconds)

  // create table for suppliers if the table didn't exist (should be removed, when the database wasn't h2)
  modules.suppliersDal.createTables()

  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[SupplierHttpService])
    override def apiVersion = "2.0"
    override def baseUrl = "/"
    override def docsPath = "api-docs"
    override def actorRefFactory = context
    override def apiInfo = Some(new ApiInfo("Spray-Slick-Swagger Sample", "A scala rest api.", "TOC Url", "Cláudio Diniz cfpdiniz@gmail.com", "Apache V2", "http://www.apache.org/licenses/LICENSE-2.0"))
  }

  val suppliers = new SupplierHttpService(modules){
    def actorRefFactory = context
  }


  def receive = runRoute( suppliers.SupplierPostRoute ~ suppliers.SupplierGetRoute ~ swaggerService.routes ~
    get {
      pathPrefix("") { pathEndOrSingleSlash {
        getFromResource("swagger-ui/index.html")
      }
      } ~
        getFromResourceDirectory("swagger-ui")
    })
}



@Api(value = "/supplier", description = "Operations about suppliers")
abstract class SupplierHttpService(modules: Configuration with PersistenceModule) extends HttpService {

  import JsonProtocol._
  import SprayJsonSupport._

  implicit val timeout = Timeout(5.seconds)

  @ApiOperation(httpMethod = "GET", response = classOf[Supplier], value = "Returns a supplier based on ID")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "supplierId", required = true, dataType = "integer", paramType = "path", value = "ID of supplier that needs to be fetched")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Ok")))
  def SupplierGetRoute = path("supplier" / IntNumber) { (supId)      =>
    get {
      respondWithMediaType(`application/json`) {
        onComplete((modules.suppliersDal.getSupplierById(supId)).mapTo[Vector[Supplier]]) {
          case Success(photos) => complete(photos)
          case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      }
    }}

  @ApiOperation(value = "Add Supplier", nickname = "addSuplier", httpMethod = "POST", consumes = "application/json", produces = "text/plain; charset=UTF-8")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "Supplier Object", dataType = "persistence.entities.SimpleSupplier", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Bad Request"),
    new ApiResponse(code = 201, message = "Entity Created")
  ))
  def SupplierPostRoute = path("supplier"){
    post {
      entity(as[SimpleSupplier]){ supplierToInsert =>  onComplete((modules.suppliersDal.save(Supplier(None,supplierToInsert.name,supplierToInsert.desc)))) {
        // ignoring the number of insertedEntities because in this case it should always be one, you might check this in other cases
        case Success(insertedEntities) => complete(StatusCodes.Created)
        case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
      }
      }
    }
  }
}
