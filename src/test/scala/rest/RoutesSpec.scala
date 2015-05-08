package rest

import entities.JsonProtocol
import persistence.dal.SuppliersDAA.{Save, GetSupplierById}
import persistence.entities.{SimpleSupplier, Supplier}
import spray.httpx.SprayJsonSupport
import spray.http._
import StatusCodes._
import scala.concurrent.Future
import JsonProtocol._
import SprayJsonSupport._

class RoutesSpec  extends AbstractRestTest {
  sequential

  def actorRefFactory = system

  val modules = new Modules {}

  val suppliers = new SupplierHttpService(modules){
    override def actorRefFactory = system
  }

  "Supplier Routes" should {

    "return an empty array of suppliers" in {
      addProbeBehaviour(suppliersActor) {
        case (sender,  GetSupplierById(1)) =>
          sender.tell(Future(Seq()), suppliersActor.ref)
      }
      Get("/supplier/1") ~> suppliers.SupplierGetRoute ~> check {
        handled must beTrue
        status mustEqual OK
        responseAs[Seq[Supplier]].length == 0
      }
    }

    "return an array with 2 suppliers" in {
      addProbeBehaviour(suppliersActor) {
        case (sender,  GetSupplierById(1)) =>
          sender.tell(Future(Seq(Supplier(Some(1),"name 1", "desc 1"),Supplier(Some(2),"name 2", "desc 2"))), suppliersActor.ref)
      }
      Get("/supplier/1") ~> suppliers.SupplierGetRoute ~> check {
        handled must beTrue
        status mustEqual OK
        responseAs[Seq[Supplier]].length == 2
      }
    }

    "create a supplier with the json in post" in {
      addProbeBehaviour(suppliersActor) {
        case (sender,  Save(Supplier(None,"name 1","desc 1"))) =>
          sender.tell(Future(1), suppliersActor.ref)
      }
      Post("/supplier",SimpleSupplier("name 1","desc 1")) ~> suppliers.SupplierPostRoute ~> check {
        handled must beTrue
        status mustEqual Created
      }
    }

    "not handle the invalid json" in {
      Post("/supplier","{\"name\":\"1\"}") ~> suppliers.SupplierPostRoute ~> check {
        handled must beFalse
      }
    }

    "not handle an empty post" in {
      Post("/supplier") ~> suppliers.SupplierPostRoute ~> check {
        handled must beFalse
      }
    }

  }

}