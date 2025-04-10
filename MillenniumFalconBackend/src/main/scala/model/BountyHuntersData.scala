package model

import model.BountyHuntersData.AttackPlan

case class BountyHuntersData(countDown: Int,
                             BountyHunters:Seq[AttackPlan])

object BountyHuntersData {
  case class AttackPlan(planet: String,
                        day: Int)}
