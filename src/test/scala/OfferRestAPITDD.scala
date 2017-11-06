import akka.actor.Status.Success
import org.scalatest.{BeforeAndAfter, FlatSpec, FunSuite, Matchers}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}

@RunWith(classOf[JUnitRunner])
class OfferRestAPITests extends FunSuite  with BeforeAndAfter  with Matchers {

  var itemservices: ItemServices = _
  var dummyoffer1: Item = _
  var dummyoffer2: Item = _
  var dummyupdate: ItemUpdate = _
  before {
    itemservices = new ItemServices()
  }

  // GET procedure: Return exception when item (offer) not present

  test("WhenanItemUsedThenReturnItself") {
    assertThrows[NoSuchElementException] {
      val futureofferitem = itemservices.getItem(1)
      val result = Await.result(futureofferitem, 6.seconds)
      val id = result.get.itemID
    }
  }

  // POST procedure will use a an array to store items.

  // Case (i): No POST call has been done so expect array to be empty.
  test("WhenPOSTNotCalledExpectOfferStorageToBeEmpty") {
    assert(itemservices.items.length == 0)
  }
  // Case (ii): Implement POST call to store an offer and validate

  test("WhenPOSTCallMakeOnceExpectArrayLengthSizeOne") {
    dummyoffer1 = Item(1, "OrangeJuice", "1002020", 1.23, "30s", "NA")
    val waitforresult = Await.result(itemservices.createItem(dummyoffer1), 5.seconds)
    assert(itemservices.items.length == 1)
  }


  // GET procedure detail view: Throw an exception when GET called on offer NOT present.
  test("WhenGETCallMadeOnAOfferNOTPresentThenThrowNoElementException") {
    dummyoffer1 = Item(1, "OrangeJuice", "1002020", 1.23, "30s", "NA")
    val waitforcreateresult = Await.result(itemservices.createItem(dummyoffer1), 5.seconds)
    assertThrows[NoSuchElementException] {
      val waitforgetresult = Await.result(itemservices.getItem(2), 5.seconds)
    }
  }


  // GET procedure detail view: For a given ID, when present should return item, in our case the ID.
  test("WhenGETCallMadeOnAOfferPresentThenReturnOfferID") {
    dummyoffer1 = Item(1, "OrangeJuice", "1002020", 1.23, "30s", "NA")
    val waitforcreateresult = Await.result(itemservices.createItem(dummyoffer1), 5.seconds)
    val waitforGETresult = Await.result(itemservices.getItem(1), 5.seconds)
    assert(waitforGETresult.get.itemID == 1)
  }


  // GET list view: Validate list of all offers
  test("WhenMultipleOfferPresentShowTotalCountOfItems") {
    dummyoffer1 = Item(1, "OrangeJuice", "1002020", 1.23, "30s", "NA")
    dummyoffer2 = Item(2, "AppleJuice", "10020", 2.23, "30m", "NA")
    Await.result(itemservices.createItem(dummyoffer1), 5.seconds)
    Await.result(itemservices.createItem(dummyoffer2), 5.seconds)
    assert(itemservices.items.length == 2)
  }


  // DELETE procedure
  test("WhenNoOfferPresentThenOnDeleteThrowRunTimeException") {
    assertThrows[NoSuchElementException] {
      Await.result(itemservices.deleteItem(1), 5.seconds)
    }
  }
  test("WhenOfferPresentThenDelete") {
    dummyoffer1 = Item(1, "OrangeJuice", "1002020", 1.23, "30s", "NA")
    dummyoffer2 = Item(2, "AppleJuice", "10020", 2.23, "30m", "NA")
    Await.result(itemservices.createItem(dummyoffer1), 5.seconds)
    Await.result(itemservices.createItem(dummyoffer2), 5.seconds)
    //val numberOfOfferBeforeDelete  = Await.result(itemservices.getAllItems(),5.seconds) <-- getAllItem sends the vector over to val
    val numberOfOfferBeforeDeleteLength = itemservices.items.length
    assert(numberOfOfferBeforeDeleteLength == 2)
    Await.result(itemservices.deleteItem(1), 2.seconds)
    val numberOfOfferAfterDeleteLength = itemservices.items.length
    assert(numberOfOfferAfterDeleteLength == 1)
  }

  // PUT procedure

  test("PUTMethod") {

    dummyoffer1 = Item(1, "OrangeJuice", "1002020", 1.23, "30s", "NA")
    dummyupdate = ItemUpdate(Some(2), Some("AppleJuice"), Some("10020"), Some(2.23), Some("30m"), Some("NA"))
    Await.result(itemservices.createItem(dummyoffer1), 5.seconds)

    // Case update item ID does not match any offer then throw exception else change attributes
    assertThrows[NoSuchElementException] {
      Await.result(itemservices.updateItem(2, dummyupdate), 5.seconds)
    }

    Await.result(itemservices.updateItem(1, dummyupdate), 5.seconds)
    // Confirm attribute have been changed for a successful PUT
    assert(itemservices.items.map { case item => if (item.itemID == 1) item.itemName }.contains("AppleJuice") == true)
  }

  //  Acquire offer time period and offer creation timestamp
  test("GetOfferTimeProperties") {
    dummyoffer1 = Item(1, "OrangeJuice", "1509990045", 1.23, "2m", "NA")
    Await.result(itemservices.createItem(dummyoffer1), 3.seconds)
    assert(itemservices.getItemTimeProperties(1).map(x => x._1).contains("2m"))
    assert(itemservices.getItemTimeProperties(1).map(x => x._2).contains("1509990045".toLong))
  }
  // Acceptable offer period char are: * m: minutes, * s: seconds, * h: hours
  test("DuringOfferPeriodValidationThrowExceptionOnInvalidPeriod") {
    dummyoffer1 = Item(1, "OrangeJuice", "1509990045", 1.23, "2f", "NA")
    Await.result(itemservices.createItem(dummyoffer1), 3.seconds)

    assertThrows[RuntimeException] {
      itemservices.validateItemOfferExpiry(itemservices.getItemTimeProperties(1))
    }
  }

    // Perform period validation on all acceptable time period format

  test("WhenOfferISValidThenStateShouldbeON"){
    dummyoffer1 = Item(1, "OrangeJuice",  ((System.currentTimeMillis()/1000)+10).toString, 1.23, "60s", "NA")
    Await.result(itemservices.createItem(dummyoffer1), 3.seconds)
    itemservices.validateItemOfferExpiry(itemservices.getItemTimeProperties(1))
    assert(itemservices.state=="on")
  }





}
