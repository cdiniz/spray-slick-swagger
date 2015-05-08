package utils

import akka.actor.{ActorPath, ActorSelection, Props, ActorRef}
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
	val suppliersDAA: ActorSelection
}


trait PersistenceModuleImpl extends PersistenceModule with DbModule{
	this: ActorModule with Configuration  =>

	// use an alternative database configuration ex:
	// private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("pgdb")
	private val dbConfig : DatabaseConfig[JdbcProfile]  = DatabaseConfig.forConfig("h2db")

	override implicit val profile: JdbcProfile = dbConfig.driver
	override implicit val db: JdbcProfile#Backend#Database = dbConfig.db

  system.actorOf(Props(new SuppliersDAA()), "suppliersDAA")

	override val suppliersDAA = system.actorSelection("/user/suppliersDAA")


	val self = this

}
