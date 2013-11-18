package com.sears.SYWL.p2p.api;

public class P2PAPIImpl implements P2PAPI {

	@Override
	public IJSONable checkDeliveryAvailability(double lat_dest,
			double lng_dest, int store_id, int numOfGoods, double pickUpRange,
			long orderDate, long dueTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean holdIntent(int intentID, int numOfGoods) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IJSONable releaseIntent(int intentID, int numOfGoods) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable getLocationHistoryByUserId_buyer(int user_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable postSummary(String jsonfile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable getSummaryPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable confirmSummary(int summaryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable registerDeliveryIntent(int user_id, int capacity,
			long date, double lat_dest, double lng_dest, int store_id,
			int reward) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable activateDeliveryIntent(long estimated_expire_time,
			int intent_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable getLocationHistoryByUserId_Deliverer(int user_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable sendOrders(int[] order_ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJSONable confirmPickUp(int intent_id, boolean isSuccess) {
		// TODO Auto-generated method stub
		return null;
	}

}
