package com.refresh.pos.domain.sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.refresh.pos.domain.inventory.LineItem;
import com.refresh.pos.domain.inventory.Product;

/**
 * Sale represents sale operation.
 * 
 * @author Refresh Team
 *
 */
public class Sale {
	
	private final int id;
	private String startTime;
	private String endTime;
	private String status;
	private Double discount;
	private Long mobile;
	private List<LineItem> items;
	

	public Sale(int id, String startTime,Long mobile,Double discount) {
		this(id, startTime, startTime, "",mobile,discount, new ArrayList<LineItem>());
	}
	
	public Sale(int id, String startTime, String endTime, String status,Long mobile,Double discount, List<LineItem> items) {
		this.id = id;
		this.startTime = startTime;
		this.status = status;
		this.endTime = endTime;
		this.items = items;
		this.mobile = mobile;
		this.discount = discount;
	}
	
	public List<LineItem> getAllLineItem(){
		return items;
	}
	
	public LineItem addLineItem(Product product, int quantity) {
		
		for (LineItem lineItem : items) {
			if (lineItem.getProduct().getId() == product.getId()) {
				lineItem.addQuantity(quantity);
				return lineItem;
			}
		}
		
		LineItem lineItem = new LineItem(product, quantity);
		items.add(lineItem);
		return lineItem;
	}
	
	public int size() {
		return items.size();
	}
	
	public LineItem getLineItemAt(int index) {
		if (index >= 0 && index < items.size())
			return items.get(index);
		return null;
	}


	public double getTotal() {
		double amount = 0;
		for(LineItem lineItem : items) {
			amount += lineItem.getTotalPriceAtSale();
		}
		return amount;
	}

	public double getcgst() {
		double tax = 0.09;
		double cgst = 0;
		double amount = 0;
		for (LineItem lineItem : items) {
			amount += lineItem.getTotalPriceAtSale();
		}
		if (amount < 500){
			tax=0.025;
	}
	cgst=amount*tax;
		return cgst;
	}

	public double getdiscount(){

		return discount;

	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}

	public double gettender(){
		double amount=getTotal();
		double sgst=getcgst();
		double cgst=getcgst();

		double tender=amount+sgst+cgst;
		tender = Double.parseDouble(String.format("%.2f",tender));
		return tender;

	}
	public long mobile(){
		return mobile;
	}

	public int getId() {
		return id;
	}

	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}

	public String getPayment() {
		return "CASH";
	}

	public String getStatus() {
		return status;
	}
	public int getOrders() {
		int orderCount = 0;
		for (LineItem lineItem : items) {
			orderCount += lineItem.getQuantity();
		}
		return orderCount;
	}

	public Map<String, String> toMap() {	
		Map<String, String> map = new HashMap<String, String>();
		map.put("id",id + "");
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("status", getStatus());
//		map.put("tender", String.valueOf(gettender())+"");
		map.put("total", getTotal() + "");
		map.put("orders", getOrders() + "");
		
		return map;
	}

	public void removeItem(LineItem lineItem) {
		items.remove(lineItem);
	}

}