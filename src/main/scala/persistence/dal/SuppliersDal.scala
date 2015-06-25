package persistence.dal

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import persistence.entities.{Suppliers, Supplier}

import slick.driver.JdbcProfile
import utils.{DbModule}

import scala.concurrent.Future


trait SuppliersDal {
  def save(sup: Supplier) : Future[Int]
  def getSupplierById(id: Int) : Future[Vector[Supplier]]
  def createTables() : Future[Unit]
}


class SuppliersDalImpl(implicit val db: JdbcProfile#Backend#Database,implicit val profile: JdbcProfile) extends SuppliersDal with DbModule with Suppliers with LazyLogging{
  import profile.api._

  override def save(sup: Supplier) : Future[Int] = {  db.run(suppliers += sup).mapTo[Int] }

  override def getSupplierById(id: Int) : Future[Vector[Supplier]] = { db.run(suppliers.filter(_.id === id).result).mapTo[Vector[Supplier]] }

  override def createTables() : Future[Unit] = {
      db.run(DBIO.seq(suppliers.schema.create))
  }

}
