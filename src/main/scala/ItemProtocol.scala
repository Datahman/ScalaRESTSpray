
// model
case class Item(itemID:Int,itemName:String,itemTimestamp:String,itemPrice:Double,offerPeriod:String,offerTimeLeft:String)
case class ItemUpdate(itemID: Option[Int], itemName: Option[String],itemTimestamp:Option[String],itemPrice:Option[Double],offerPeriod:Option[String],offerTimeLeft:Option[String])

