package persistence.dal


import akka.actor.{ActorSystem, Props}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import spray.testkit.ScalatestRouteTest
import utils._
import org.scalatest.Suite
trait AbstractPersistenceTest extends ScalatestRouteTest{  this: Suite =>


  trait Modules extends ConfigurationModuleImpl  with PersistenceModuleTest {
  }


  trait PersistenceModuleTest extends PersistenceModule with DbModule{
    this: Configuration  =>

    private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2test")

    override implicit val profile: JdbcProfile = dbConfig.driver
    override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

    override val suppliersDal = new SuppliersDalImpl()

    val self = this

  }

}