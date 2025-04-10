package model

import model.BountyHuntersData.AttackPlan
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class BountyHuntersData(countdown: Int,
                             bounty_hunters:Seq[AttackPlan])

object BountyHuntersData {
  case class AttackPlan(planet: String,
                        day: Int)

  object AttackPlan {
    implicit val attackPlanFormat: RootJsonFormat[AttackPlan] = jsonFormat2(AttackPlan.apply)
  }

  implicit val bountyHuntersDataFormat: RootJsonFormat[BountyHuntersData] = jsonFormat2(BountyHuntersData.apply)
}
