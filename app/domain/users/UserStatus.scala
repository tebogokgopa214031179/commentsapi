package domain.users

import org.joda.time.DateTime


/**
  * Created by hashcode on 2016/10/19.
  */
case class UserStatus(siteId:String,
                      userId:String,
                      status:String,
                      date: DateTime)
object UserStatus{

}
