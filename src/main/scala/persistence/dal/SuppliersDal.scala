package persistence.dal

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import persistence.entities.{SuppliersTable, Supplier}
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.JdbcProfile
import utils.{DbModule}

import scala.concurrent.Future


trait SuppliersDal {
  def save(sup: Supplier) : Future[Long]
  def getSupplierById(id: Int) : Future[Vector[Supplier]]
  def createTables() : Future[Unit]
}


class SuppliersDalImpl(implicit val db: JdbcProfile#Backend#Database,implicit val profile: JdbcProfile) extends SuppliersDal with BaseDal[SuppliersTable,Supplier] with DbModule with LazyLogging{
  import profile.api._

  protected val tableQ = TableQuery[SuppliersTable]

  override def save(sup: Supplier) : Future[Long] = insert(sup)

  override def getSupplierById(id: Int) : Future[Vector[Supplier]] = findById(id) map {
    x => x match {
      case Some(sup) => Vector(sup)
      case None =>  Vector()
    }
  }

  override def createTables() : Future[Unit] = {
      db.run(DBIO.seq(tableQ.schema.create))
  }

}
