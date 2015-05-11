package persistence.dal

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import persistence.entities.{Suppliers, Supplier}

import slick.driver.JdbcProfile
import utils.{DbModule}


object SuppliersDAA {

  case class Save(sup: Supplier)

  case class GetSupplierById(id: Int)

  case class CreateTables()

}


class SuppliersDAA(implicit val db: JdbcProfile#Backend#Database,implicit val profile: JdbcProfile) extends Actor with DbModule with Suppliers with LazyLogging{
  import profile.api._
  import SuppliersDAA._
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case Save(sup) ⇒ sender ! db.run(suppliers += sup)

    case GetSupplierById(id) ⇒ sender ! db.run(suppliers.filter(_.id === id).result)

    case CreateTables =>
      try {
        sender ! db.run(DBIO.seq(suppliers.schema.create))
      }  catch {
        case e: Exception => logger.info("Could not create table of suppliers.... assuming it already exists")
      }
  }
}