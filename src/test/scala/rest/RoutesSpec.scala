package rest

import entities.JsonProtocol
import persistence.dal.SuppliersDAA.GetSupplierById
import persistence.entities.Supplier
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

  }

}