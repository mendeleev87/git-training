package com.taxeo.sbus.srxp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Deprecated
public class SRXP {
	
	private static Client client = new Client();
	
	static String urlReports;
	static String urlExpense;
	
	static int invoiceNumber = 1;
	static HashMap<String, String> sapMapping;
	static {
		sapMapping = new HashMap<String,String>();
		sapMapping.put("Business literature", "EU100008:10.8");
		sapMapping.put("Car rental", "EU020001:2.2.2");
		sapMapping.put("Car rental re-fill (fuel)", "EU020020:1.2.1");
		sapMapping.put("Conferences and Seminars", "EU100092:10.9.2");
		sapMapping.put("Consumptions (group)", "EU070001:7.1");
		sapMapping.put("Consumptions (individual)", "EU070001:7.1");
		sapMapping.put("Gifts to colleagues (goods, articles)", "EU100001:10.7");
		sapMapping.put("Gifts to colleagues (vouchers, gift certificates)", "EU100001:10.7");
		sapMapping.put("Gifts to customers", "EU100001:10.7");
		
		sapMapping.put("Billable travel expenses hotel", "EU060001:6.1");
		sapMapping.put("Hotel", "EU060001:6.1");
		sapMapping.put("Hotel: internet connection fees", "EU060005:10.12");
		sapMapping.put("Hotel: Laundry", "EU060004:0");
		sapMapping.put("Hotel: lodging", "EU060001:6.1");
		sapMapping.put("Hotel: parking", "EU060008:32.4");
		sapMapping.put("Hotel: Taxes", "EU060008:0");
		sapMapping.put("Meals (group)", "EU070001:7.1.1");
		sapMapping.put("Meals (individual)", "EU070001:7.1.1");
		sapMapping.put("Food etc. Travel & Business lunches", "EU070001:7.1.1");
		sapMapping.put("Food etc. Travel & Business lunches ", "EU070001:7.1.1");
		sapMapping.put("Food, drink, cake etc. Re. customer/suppliers", "EU070001:7.1.1");
		
		
		sapMapping.put("Flight Tickets", "0:0");
		sapMapping.put("Home-charging electric hybrid car", "0:0");
		sapMapping.put("Business use of private internet", "0:0");
		sapMapping.put("Luggage storage", "0:0");
		sapMapping.put("Memberships", "EU100092:10.9");
		sapMapping.put("Bank charges", "0:0");
		
		
		sapMapping.put("Other staff cost", "EU100008:10.8");
		sapMapping.put("Project, External fonded cost", "EU100008:10.8");
		sapMapping.put("Project, internall staff fonded cost", "EU100008:10.8");
		sapMapping.put("Software maintenance", "EU100008:10.8");
		
		sapMapping.put("Marketing Costs", "EU100008:10.8");
		sapMapping.put("Passport and Visa Feeds", "0:0");
		
		sapMapping.put("Mobile telephony", "EU100002:10.8");
		sapMapping.put("Office supplies", "EU100008:10.8");
		sapMapping.put("Parking Airport", "EU030001:3.2.4");
		sapMapping.put("Parking costs", "EU030001:3.2.4");
		sapMapping.put("Parking costs (lease car, pool car, rental car)", "EU030001:3.2.4");
		sapMapping.put("Parking costs (private car)", "EU030001:3.2.4");
		sapMapping.put("Parking costs airport (lease car)", "EU030001:3.2.4");
		sapMapping.put("Parking costs airport (private car)", "EU030001:3.2.4");
		
		sapMapping.put("Private lodging", "EU060001:6.1");
		sapMapping.put("Public transport (Train/Tram/Bus)", "EU050003:5.1");
		sapMapping.put("Road tolls", "EU040001:4.2.1");
		sapMapping.put("Taxi", "EU050001:5.1");
		sapMapping.put("Tips", "EU010001:1.2.1");
		sapMapping.put("Training and Education", "EU100093:10.9.3");
		sapMapping.put("Taxi", "EU050001:5.1");
		sapMapping.put("Billable travel expenses other", "EU050001:5.1");
		
		
		
	}
	
	static Map<Integer, String> categoryMap = new HashMap<Integer, String>();
	static Map<Integer, String> currencyMap = new HashMap<Integer, String>();
	//String urlExpenses = "https:/";
	
	public static void main(String[] args) throws IOException, JSONException {
		Calendar cal = Calendar.getInstance();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		
		
		//53
		for (int i=1; i<53; i++) {
			cal.setTime(new Date());
			cal.set(Calendar.YEAR, 2015);
			//cal.set(Calendar.MONTH, i-1);
			cal.set(Calendar.WEEK_OF_YEAR, i);
			
			//cal.set(Calendar.DAY_OF_MONTH, 1);
			date = cal.getTime();
			
			String startDate = sdf.format(date);
			
			//cal.set(Calendar.MONTH, i);
			cal.set(Calendar.WEEK_OF_YEAR, i + 1);
			date = cal.getTime();
			String endDate = sdf.format(date);
			
			
			//System.out.println(startDate + " " + endDate);
			urlReports = "https://portal.srxp.com/api/1/reports?filter=date_from:" + startDate +"+and+date_until:" + endDate;// + "+and+customer_id:1632";
				
			
			String  response = client.doGet(urlReports);
			JSONObject o = new JSONObject(response);

			if (o.has("reports")) {
				JSONArray reportsArray = (JSONArray) o.get("reports");
				//System.out.println("array length is: " + reportsArray.length());
				for (int j = 0; j < reportsArray.length(); j++) {
					ids.add(reportsArray.getJSONObject(j).getInt("id"));
					//System.out.println(reportsArray.getJSONObject(j).getInt("id"));
					//System.out.println(reportsArray.getJSONObject(j).getString("created"));
					//System.out.println(reportsArray.get(i));
				}
			}
			
			if (o.has("accounts")) {
				JSONArray accountsArray = (JSONArray) o.get("accounts");
				//System.out.println("array length is: " + reportsArray.length());
				for (int j = 0; j < accountsArray.length(); j++) {
					//ids.add(accountsArray.getJSONObject(j).getInt("customer_id"));
					//System.out.println(reportsArray.getJSONObject(j).getInt("id"));
					//System.out.println(reportsArray.getJSONObject(j).getString("created"));
					//System.out.println(reportsArray.get(i));
					//System.out.println(accountsArray.getJSONObject(j).getInt("customer_id"));
				}
			}
			
			//System.out.println("ids size is :" + ids.size());
		}
		
		ArrayList<Integer> vatAmounts = new ArrayList<Integer>();
		int vatCounter = 0;
		int endCounter = 0;
		String output = "";
		for ( Integer id : ids) {
			
			String urlExpense = "https://portal.srxp.com/api/1/reports/" + id + "/expenses";
			String result = client.doGet(urlExpense);
			JSONObject o = new JSONObject(result);
			
			JSONArray expensesArray = (JSONArray) o.get("expenses");
			JSONArray categoriesArray = (JSONArray) o.get("categories");
			JSONArray currenciesArray = (JSONArray) o.get("currencies");
			
			fillCategoryMap(categoriesArray);
			fillCurrencyMap(currenciesArray);
			
			JSONArray payment_methodsArray = (JSONArray) o.get("payment_methods");
			int idToExclude = 0;
			int exludeNonEu = 0;
			for (int k = 0; k < payment_methodsArray.length(); k++) {
				String country = payment_methodsArray.getJSONObject(k).getString("name");
				//System.out.println("county is: " + country);
				/*if (country.equals("Netherlands")) {
					idToExclude = payment_methodsArray.getJSONObject(k).getInt("id");
				}*/
				if (country.equals("1. non- EU")) {
					exludeNonEu = payment_methodsArray.getJSONObject(k).getInt("id");
				}
			}
			
			int currencyIdDenmark = 6;
			
			
			//System.out.println("Payment iDs to exclude: " + idToExclude + " " + exludeNonEu);	
			
			
			for (int k = 0; k < expensesArray.length(); k++) {
				JSONObject expense = expensesArray.getJSONObject(k);
				JSONArray amountsArray = (JSONArray) expensesArray.getJSONObject(k).get("amounts");
				
				int paymnetMethodId = -1;
				if (expensesArray.getJSONObject(k).has("payment_method_id")) {
					paymnetMethodId = expensesArray.getJSONObject(k).getInt("payment_method_id");
				}
				else {
					//System.out.println("no payment");
					//System.out.println(expensesArray.getJSONObject(k));
					continue;
				}
				//paymnetMethodId == idToExclude ||
				if ( paymnetMethodId == exludeNonEu) {
					//System.out.println("expense skipped with id: " + paymnetMethodId);
					continue;
				}			
				
				if (amountsArray.length() > 1) {
					//System.out.println("more than 1 amount");
					System.out.println("more than 1 amount: " + result);
					endCounter++;
				}
				//System.out.println(o);
				System.out.println(expensesArray.getJSONObject(k));
				
				String expenseDate = expense.getString("expense_date");
				expenseDate = expenseDate.substring(0, 10);
				
				//System.out.println("expenseDate is" + expenseDate );
				
				
				JSONObject amountObject = (JSONObject) amountsArray.get(0);
				
				
				String fileUrl = getFileUrl(expense);
				String description = getExpenseDescription(amountObject);
				String amount = getAmount(amountObject);
				System.out.println(description);
				//System.out.println(amount);
				String currency = getCurrency(expense);
				
				if (currency.equals("CHF") || currency.equals("NOK") || currency.equals("USD")) {
					continue;
				}
				
				
				String lineItem = generateXML(description, expenseDate, fileUrl, currency, amount);
				if (lineItem != null) {
					output += lineItem;
				} else {
					//not refundable expenses
					System.out.println("not refundable");
					continue;
				}

				
				if (amountObject.has("vat_amount")) {
					
					if (amountObject.has("display_inclusive")) {
						boolean displayInclusive = ((JSONObject) amountsArray.get(0)).getBoolean("display_inclusive");
						if (displayInclusive) {
							int vat_amount = ((JSONObject) amountsArray.get(0)).getInt("vat_amount");
							if (vat_amount == 0 ) {
								vatCounter++;
							} else {
								vatAmounts.add(vat_amount);
							}
						} else {
							System.out.println("display_inclusive is false");
						}
					} else {
						System.out.println("display_inclusive missing");
					}
						
				}	
				
			}

		}
		System.out.println("vat zeros are: " + vatCounter);
		System.out.println(vatAmounts);
		
		//System.out.println("ids size is :" + ids.size());
		//System.out.println(ids);
		output = "<module name=\"vat_invoices_retyping\" descr=\"Insert Invoices\">" + output + "</module>";
		System.out.println(output);*/
		}
		
	}
	
	static void fillCategoryMap(JSONArray categoriesArray) throws JSONException {
		
		for (int i = 0; i < categoriesArray.length(); i++) {
			JSONObject category  = (JSONObject)categoriesArray.get(i);
			categoryMap.put(category.getInt("id"), category.getString("name"));
		}
	}
	
	static void fillCurrencyMap(JSONArray currenciesArray) throws JSONException {
		
		for (int i = 0; i < currenciesArray.length(); i++) {
			JSONObject currency  = (JSONObject)currenciesArray.get(i);
			currencyMap.put(currency.getInt("id"), currency.getString("short"));
		}
	}
	
	static String getCurrency(JSONObject expense) throws JSONException {
		return currencyMap.get(expense.getInt("currency_id"));
	}
	
	static String getExpenseDescription(JSONObject amountObject) throws JSONException {

		int categoryId  = amountObject.getInt("category_id");
		return categoryMap.get(categoryId);
	}
	
	static String getAmount(JSONObject amountObject) throws JSONException {

		Double amount  = amountObject.getDouble("amount");
		return amount.toString();
	}
	
	static String getFileUrl(JSONObject expense) throws JSONException {
		
		JSONObject picture  = (JSONObject)expense.get("picture");
		JSONObject urls  = (JSONObject)picture.get("urls");
		String fileUrl = urls.getString("original");
		
		return fileUrl;
	}
	
	static String generateXML(String description, String date, String fileUrl, String currency, String amount) {

		//System.out.println("Codes are : " + invoiceCode + " " + prestationCode);
		if (sapMapping.get(description).split(":")[0].equals("0")) {
			//not refundable expenses
			return null;
		}
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("<module_item>");
		sb.append("<upload_invoice>" + fileUrl + "</upload_invoice>");
		sb.append("<invoice_number>" + invoiceNumber++ + "</invoice_number>");
		sb.append("<invoice_date>" + date + "</invoice_date>");
		
		sb.append("<invoiceLines>");
		sb.append("<invoiceLine>");
		sb.append("<invoice_code>"+ sapMapping.get(description).split(":")[1] + "</invoice_code>");
		sb.append("<prestation_code>" + sapMapping.get(description).split(":")[0] + "</prestation_code>");
		sb.append("<invoice_quantity>1</invoice_quantity>");
		sb.append("</invoiceLine>");
		sb.append("</invoiceLines>");
		sb.append("<invoiceretyping_currency>"+currency+"</invoiceretyping_currency>");
		sb.append("<amount_total>"+amount+"</amount_total>");
		sb.append("</module_item>");
		return sb.toString();
	}
	
	
	
	
}
