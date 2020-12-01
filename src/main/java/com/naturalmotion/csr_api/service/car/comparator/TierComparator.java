package com.naturalmotion.csr_api.service.car.comparator;

import java.util.Comparator;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class TierComparator implements Comparator<Integer> {

	private JsonArray caow;

	private List<String> eliteCars;

	public TierComparator(JsonArray caow, List<String> eliteCars) {
		this.caow = caow;
		this.eliteCars = eliteCars;
	}

	@Override
	public int compare(Integer o1, Integer o2) {

		int result = 0;
		if (o1 == -1) {
			result = 1;
		}
		if (o2 == -1) {
			result = -1;
		}
		if (o1 != -1 && o2 != -1) {
			JsonObject carLeft = caow.getJsonObject(o1);
			JsonObject carRight = caow.getJsonObject(o2);

			int dynoLeft = carLeft.getInt("tthm");
			int dynoRight = carRight.getInt("tthm");

			String tierLeft = carLeft.getString("ctie");
			String tierRight = carRight.getString("ctie");

			int legendLevelLeft = carLeft.getInt("cmlv");
			int legendLevelRight = carRight.getInt("cmlv");

			if (eliteCars.isEmpty() || legendLevelLeft == legendLevelRight) {
				if (isElite(carLeft) && !isElite(carRight)) {
					result = -1;
				} else if (!isElite(carLeft) && isElite(carRight)) {
					result = 1;
				} else if (tierLeft.equals(tierRight)) {
					result = Integer.compare(dynoLeft, dynoRight);
				} else {
					result = tierRight.compareTo(tierLeft);
				}
			} else {
				result = Integer.compare(legendLevelRight, legendLevelLeft);
			}
		}
		return result;
	}

	private boolean isElite(JsonObject jsonCar) {
		return eliteCars.contains(jsonCar.getString("crdb"));
	}

}
