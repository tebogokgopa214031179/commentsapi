package repositories.comments

import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.keys.PartitionKey
import com.websudos.phantom.reactivestreams._
import conf.connection.DataConnection
import domain.comments.Abuse

import scala.concurrent.Future

/**
  * Created by hashcode on 2016/12/22.
  */
class AbuseByUserRepository extends CassandraTable[AbuseByUserRepository, Abuse]{

  object siteId extends StringColumn(this) with PartitionKey[String]
  object emailId extends StringColumn(this) with PrimaryKey[String]
  object commentIdOrResponseId extends StringColumn(this) with PrimaryKey[String]
  object date extends DateTimeColumn(this) with PrimaryKey[DateTime]
  object details extends StringColumn(this)

  override def fromRow(r:Row): Abuse = {

    Abuse(siteId(r),commentIdOrResponseId(r),date(r),details(r),emailId(r))
  }
}

object AbuseByUserRepository extends AbuseByUserRepository with RootConnector {

  override lazy val tableName = "userabuse"

  override implicit def space: KeySpace = DataConnection.keySpace

  override implicit def session: Session = DataConnection.session

  def save(abuse: Abuse): Future[ResultSet] = {
    insert
      .value(_.siteId, abuse.siteId)
      .value(_.commentIdOrResponseId,abuse.commentIdOrResponseId)
      .value(_.details, abuse.details)
      .value(_.emailId, abuse.emailId)
      .value(_.date, abuse.date)
      .future()
  }

  def getUserAbusiveComments(siteId: String,emailId:String): Future[Seq[Abuse]] = {
    select
      .where(_.siteId eqs siteId)
      .and(_.emailId eqs emailId)
      .fetchEnumerator() run Iteratee.collect()
  }
}
