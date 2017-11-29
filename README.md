## An example of developing REST API in Scala based on Spray and Akka with partial implementation of TTD.

Project: Aim is to produce an API which enables user to view and manipulate offers with time constraints/expiry period.

### API capabilities:
* Implementation of the CRUD procedures
* Usage of JSON on PUT/POST
* A timestamp is assigned during object creation (POST)
* Subsequent querying on an object level  updates the time left on the offer (Expiry)  
* Subsequent querying at a list level (i.e list of objects) updates expiration time on all objects.
* Auto-remove (DELETE) offer after pre-defined expiry period
* Remove (DELETE) objects before expiry period
* Acceptable expiry period format are seconds(s), minutes(m),hours(h).
* Update (PUT) any field of the object  

## Future to dos:
* Implement CRON job side effect free. Done. 
* Remove the need of having timestamp on objects, and associated need on items. Contain item creation time on a hash MAP ?
* Implement restriction on the PUT procedure
* Implement (somehow) an extra description of the offer at a detail level
* Consider refactorisation on time based properties


## API Endpoints
* **/items - Detail view
* **/items/ID -List view

## JSON requests

Using POSTMAN plugin:

{
	"itemID":1,
	"itemName":"item1",
	"itemTimestamp":"{{$timestamp}}",
	"itemPrice":4.65,
	"offerPeriod":"4m",
	"offerTimeLeft":"NA"
}
