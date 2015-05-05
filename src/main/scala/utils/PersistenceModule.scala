package utils

import akka.actor.{Props, ActorRef}
import persistence.dal.{SuppliersDAA}
import slick.backend.DatabaseConfig
import slick.driver.{JdbcProfile}



trait Profile {
	val profile: JdbcProfile
}


trait DbModule extends Profile{
	val db: JdbcProfile#Backend#Database
}

trait PersistenceModule {
	val suppliersDAA: ActorRef
}


trait PersistenceModuleImpl extends PersistenceModule with DbModule{
	this: ActorModule with Configuration  =>

	// use an alternative database configuration ex:
	// private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("pgdb")
	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2db")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

  override val suppliersDAA = system.actorOf(Props(new SuppliersDAA()), "suppliersDAA")

	val self = this
}
