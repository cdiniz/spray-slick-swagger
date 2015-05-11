package persistence.dal


import akka.actor.{ActorSystem, Props}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import spray.testkit.ScalatestRouteTest
import utils._
import org.scalatest.Suite
trait AbstractPersistenceTest extends ScalatestRouteTest{  this: Suite =>
  def actorRefFactory = system


  trait Modules extends ConfigurationModuleImpl with ActorModule with PersistenceModuleTest {
  }


  trait PersistenceModuleTest extends PersistenceModule with DbModule{
    this: ActorModule with Configuration  =>
    val system = AbstractPersistenceTest.this.system

    private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2test")

    override implicit val profile: JdbcProfile = dbConfig.driver
    override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

    system.actorOf(Props(new SuppliersDAA()), "suppliersDAA")

    override val suppliersDAA = system.actorSelection("/user/suppliersDAA")

    val self = this

  }

}