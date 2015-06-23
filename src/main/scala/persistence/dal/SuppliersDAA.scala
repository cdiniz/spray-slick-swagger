package persistence.dal

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import persistence.entities.{Suppliers, Supplier}

import slick.driver.JdbcProfile
import utils.{DbModule}

import scala.concurrent.Future


trait SuppliersDAA {
  def save(sup: Supplier) : Future[Int]
  def getSupplierById(id: Int) : Future[Vector[Supplier]]
  def createTables()
}


class SuppliersDAAImpl(implicit val db: JdbcProfile#Backend#Database,implicit val profile: JdbcProfile) extends SuppliersDAA with DbModule with Suppliers with LazyLogging{
  import profile.api._

  override def save(sup: Supplier) : Future[Int] = {  db.run(suppliers += sup).mapTo[Int] }

  override def getSupplierById(id: Int) : Future[Vector[Supplier]] = { db.run(suppliers.filter(_.id === id).result).mapTo[Vector[Supplier]] }

  override def createTables() = {
    try {
      db.run(DBIO.seq(suppliers.schema.create))
    }  catch {
      case e: Exception => logger.info("Could not create table of suppliers.... assuming it already exists")
    }
  }

}