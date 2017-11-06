
import math._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class ItemServices(implicit val ec: ExecutionContext) {

  var items = Vector.empty[Item]
  var timeNow: Long = 0
  var itemOffer: String = ""
  var itemCreationTime: String = ""
  var state: String = "" // Offer invalid -> off/ Offer valid -> on
  var timeleftInminutes:Long=0
  var timeleftInseconds:Long=0
  var timeleftInhours:Long=0


  def createItem(item: Item): Future[Option[Int]] = Future {
    items.find(_.itemID == item.itemID) match {
      case Some(t) => None // ID taken
      case None =>
        items = items :+ item
        Some(item.itemID)
    }
  }

  def getAllItems(): Future[List[Item]] = Future {
    for (i <- items) {
      validateItemOfferExpiry(getItemTimeProperties(i.itemID))
      if (state == "off") {
        items = items.filterNot(_.itemID == i.itemID)
      }
      if (state == "on") {
        updateItemOffer(getItem(i.itemID))
      }
    }
    items.toList
  }

  def getItem(id: Int): Future[Option[Item]] = Future {
    if(items.count(item => item.itemID==id)>=1){
    items.find(_.itemID==id)}
    else {
        throw new NoSuchElementException
      }
  }


  def deleteItem(id: Int): Future[Unit] = Future {
    if(items.count(item => item.itemID==id)>=1)
    {items = items.filterNot(_.itemID == id)}
    else {throw new NoSuchElementException}
  }

  def updateItem(id: Int, update: ItemUpdate): Future[Option[Item]] = {

    def updateModel(item: Item): Item = {
      val itemName = update.itemName.getOrElse(item.itemName)
      val itemTimestamp = update.itemTimestamp.getOrElse(item.itemTimestamp)
      val itemPrice = update.itemPrice.getOrElse(item.itemPrice)
      val offerPeriod = update.offerPeriod.getOrElse(item.offerPeriod)
      val offerTimeLeft = update.offerTimeLeft.getOrElse(item.offerTimeLeft) // get offer time left as defined by updateItemOffer method
      Item(id, itemName, itemTimestamp, itemPrice, offerPeriod,offerTimeLeft)
    }

    getItem(id).flatMap({
      someItem =>
        someItem match {
          case None => Future {
            None
          }
          case Some(item) =>
            val updatedItem = updateModel(item)
            deleteItem(id).flatMap(_ =>
              createItem(updatedItem).map(_ => Some(updatedItem)))
        }
    })

  }



  def getItemTimeProperties(Itemid: Int): Array[(String, Long)] = {
    itemOffer = items.find(_.itemID == Itemid).get.offerPeriod
    itemCreationTime = items.find(_.itemID == Itemid).get.itemTimestamp
    val itemTimeProps = (itemOffer, itemCreationTime.toLong)
    val array = Array(itemTimeProps)
    array
  }


  def validateItemOfferExpiry(timearray: Array[(String, Long)]) = {
    timeNow = System.currentTimeMillis() / 1000
    val pattern = """(\d+)([s]|[m]|h|d)""".r
    timearray.map {
      case time => time._1 match {
        case someT => someT match {
          case pattern(offerInt, offerPeriod) =>
            if (offerInt.toInt > 0 && offerPeriod == "m") {
              val timeaggregate = offerInt.toInt * 60 + time._2
              if (timeaggregate - timeNow > 0) {
                timeleftInminutes = abs(timeaggregate - timeNow) / 60
                state = "on"
              state}
              else {state = "off"}
          }
            if (offerInt.toInt > 0 && offerPeriod == "s") {
              val timeaggregate = offerInt.toInt  + time._2
              if (timeaggregate - timeNow > 0) {
                timeleftInseconds = abs(timeaggregate - timeNow)
                state = "on"
              state}
              else {state = "off"}
            }

            if (offerInt.toInt > 0 && offerPeriod == "h") {
              val timeaggregate = (offerInt.toInt * 3600) + time._2
              if (timeaggregate - timeNow > 0) {
                timeleftInhours = abs(timeaggregate - timeNow) / 3600
                state = "on"
              state}
              else {state = "off"}
            }
           /*{throw new RuntimeException("Invalid period string found")}*/
        }
      }
    }

  }



  def updateItemOffer(queryitem:Future[Option[Item]]):Unit =  {
    queryitem.foreach{queryitem =>
      if(queryitem.get.offerPeriod.contains('m'))
      {val itemoffertime = timeleftInminutes.toString
       val updatedItem = Item(queryitem.get.itemID,queryitem.get.itemName,queryitem.get.itemTimestamp,queryitem.get.itemPrice,queryitem.get.offerPeriod,itemoffertime)
      //items = items :+ updatedItem
      getItem(queryitem.get.itemID).flatMap{
        someItem => someItem match {
          case None => Future{None}
          case Some(item) =>
            deleteItem(queryitem.get.itemID).flatMap(_=>
              createItem(updatedItem).map(_=>Some(updatedItem)))}}
      }

      if(queryitem.get.offerPeriod.contains('s'))
      {val itemoffertime=timeleftInseconds.toString
       val updatedItem = Item(queryitem.get.itemID,queryitem.get.itemName,queryitem.get.itemTimestamp,queryitem.get.itemPrice,queryitem.get.offerPeriod,itemoffertime)
        getItem(queryitem.get.itemID).flatMap{
          someItem => someItem match {
            case None => Future{None}
            case Some(item) =>
              deleteItem(queryitem.get.itemID).flatMap(_=>
                createItem(updatedItem).map(_=>Some(updatedItem)))}}}
      if(queryitem.get.offerPeriod.contains('h'))
      {val itemoffertime=timeleftInhours.toString
        val updatedItem = Item(queryitem.get.itemID,queryitem.get.itemName,queryitem.get.itemTimestamp,queryitem.get.itemPrice,queryitem.get.offerPeriod,itemoffertime)
        getItem(queryitem.get.itemID).flatMap{
          someItem => someItem match {
            case None => Future{None}
            case Some(item) =>
              deleteItem(queryitem.get.itemID).flatMap(_=>
                createItem(updatedItem).map(_=>Some(updatedItem)))}}}
    }
  }

}

/* ITEMNAME DOES NOT REMAIN IN SCOPE AFTER SUCCESS
    if(timeNow!=0 && itemtobeValidated.isInstanceOf[Future[Option[Item]]]) {
      itemtobeValidated.onComplete {
        case Success(item) => itemName = item.get.itemName
        case Failure(e) => e.printStackTrace
      }
    }*/
