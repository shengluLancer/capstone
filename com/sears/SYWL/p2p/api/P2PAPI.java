package com.sears.SYWL.p2p.api;


public interface P2PAPI {
	

	//------------------------------------------------------------------
	// Buyer API
	//------------------------------------------------------------------
	
	
	// After the user hit the "Get Delivery Now button" and finished with the Location page
	//
	// lat_dest : 		latitude of destination
	// lng_dest: 		longitude of destination
	// Store : 			the id of the store
	// numOfGoods: 	the number of goods that needed to be delivered
	// pickUpRange: the range that this buyer agreed to go to pick up
	// orderDate : 	milliseconds time value of the time user gave order
	// dueTime : 	milliseconds time value of the time order is due
	//
	// output message: 
	//             0 -> available
	//			   1 -> not available
	//			   if available, also return the intentID, the id of the DeliveryIntent which has been hold
	// @GET  @AJAX
	public IJSONable checkDeliveryAvailability( double lat_dest, double lng_dest, int store_id, int numOfGoods,
			double pickUpRange, long orderDate, long dueTime );
	
	// lock a order spot
	// return true if successfully hold a spot in a DeliveryIntent
	// @ only for internal use
	public boolean holdIntent( int intentID, int numOfGoods );

	// The user didn't finally confirm the delivery, then release the according delivery intent
	// @POST  @AJAX
	public IJSONable releaseIntent( int intentID, int numOfGoods ) ;
	
	// A list of locations that this user has been using for previous orders
	// @GET  @AJAX
	public IJSONable getLocationHistoryByUserId_buyer( int user_id );
	
	// When a user "check out", post all his data to the backend
	// This method will parse the json and then create several SummaryEntry objects and one Summary object
	// @POST @AJAX
	public IJSONable postSummary( String jsonfile );
	
	// POST the summary page to user
	// @internal use
	public IJSONable getSummaryPage();
	
	// Summary confirmation by id
	// @POST
	public IJSONable confirmSummary( int summaryId );
	
	
	
	//------------------------------------------------------------------
	// Deliverer API
	//------------------------------------------------------------------
	
	
	// When a user register a intent, he is given an intent id. The intent is not activated when registered
	// When a delivery intent is fullfilled, the reward will be issued
	//
	// @POST
	public IJSONable registerDeliveryIntent( int user_id, int capacity, long date,
			double lat_dest, double lng_dest, int store_id, int reward );
	
	// When the pick-up food is ready, the seller sent the message to our backend system
	// Then this Intent is put into the pool of intents
	//
	// @POST
	public IJSONable activateDeliveryIntent( long estimated_expire_time, int intent_id );
	
	// get the location history of deliver destination for a specific user
	//
	// @GET
	public IJSONable getLocationHistoryByUserId_Deliverer( int user_id );
	
	

	//------------------------------------------------------------------
	// Seller API
	//------------------------------------------------------------------
	
	// This method send orders to 
	public IJSONable sendOrders( int[] order_ids );
	
	public IJSONable confirmPickUp( int intent_id, boolean isSuccess );
	
	
}
