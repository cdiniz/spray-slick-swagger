package persistence.dal

import slick.driver.H2Driver.api._
import utils.{DbModule, Profile}
import scala.concurrent.Future


trait BaseEntity {
  val id : Long
}


abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}

trait BaseDal[T <: BaseTable[A], A <: BaseEntity] extends Profile with DbModule {
  import profile.api._
  protected val tableQ: TableQuery[T]

  def insert(row : A): Future[Long] ={
    db.run((tableQ returning tableQ.map(_.id) += row))
  }

  def update(row : A): Future[Int] = {
    db.run(tableQ.filter(_.id === row.id).update(row))
  }

  def findById(id : Long): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def delete(id : Long): Future[Int] = {
    db.run(tableQ.filter(_.id === id).delete)
  }

}

