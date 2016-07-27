package com.taxeo.sbus.srxp;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taxeo.tips.services.Country;
import com.taxeo.tips.services.Currency;
import com.taxeo.tips.services.Invoice;
import com.taxeo.tips.services.InvoiceContent;
import com.taxeo.tips.services.InvoiceItem;
import com.taxeo.tips.services.UploadInvoicesRequest;

public class SRXPFacade {

	private Client client = new Client();

	public static final String BASE_URI = "https://portal.srxp.com/api/1";

	public static final String REPORTS_ENDPOINT = "/reports";

	public static final String EXPENSES_ENDPOINT = "/expenses";

	public static final String CUSTOMERS_ENDPOINT = "/customers";

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static final BigDecimal ZERO = new BigDecimal("0");

	static HashMap<String, String> sapMapping;
	static {
		sapMapping = new HashMap<String, String>();
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
		sapMapping.put("Hotel: Taxes", "EU000002:0");
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

	static HashMap<Integer, String> clientIdMappings = new HashMap<>();
	static {
		clientIdMappings.put(1631, "2000000087");
		clientIdMappings.put(1632, "2000000002");
	}
	
	static HashMap<String, String> isoCodeMappings = new HashMap<>();
	static {
		isoCodeMappings.put("AUT", "AT");
		isoCodeMappings.put("BEL", "BE");
		isoCodeMappings.put("HRV", "HR");
		isoCodeMappings.put("CZE", "CZ");
		isoCodeMappings.put("DNK", "DK");
		isoCodeMappings.put("FIN", "FI");
		isoCodeMappings.put("FRA", "FR");
		isoCodeMappings.put("DEU", "DE");
		isoCodeMappings.put("GRC", "GR");
		isoCodeMappings.put("IRL", "IE");
		isoCodeMappings.put("ITA", "IT");
		isoCodeMappings.put("LUX", "LU");
		isoCodeMappings.put("NLD", "NL");
		isoCodeMappings.put("NOR", "NO");
		isoCodeMappings.put("POL", "PL");
		isoCodeMappings.put("PRT", "PT");
		isoCodeMappings.put("ESP", "ES");
		isoCodeMappings.put("SWE", "SE");
		isoCodeMappings.put("CHE", "CH");
		isoCodeMappings.put("GBR", "GB");
		
	}

	public List<Integer> listAllReportIds() {
		List<Integer> reportIds = new ArrayList<>();
		String allReportsResponse = getReportsJsonString();
		JSONObject allReportsJson = new JSONObject(allReportsResponse);

		if (allReportsJson.has("reports")) {
			JSONArray reportsArray = (JSONArray) allReportsJson.get("reports");
			reportsArray.forEach(r -> reportIds.add(((JSONObject) r).getInt("id")));
		}
		return reportIds;
	}

	public String getReportsJsonString() {
		String allReportsResponse = client.doGet(BASE_URI + REPORTS_ENDPOINT, Client.ACCESS_KEY);
		return allReportsResponse;
	}

	public List<String> listAllExpensesAsJsonStrings() {
		String reportsUri = BASE_URI + REPORTS_ENDPOINT + "/";
		List<String> result = new ArrayList<>();
		listReportIdsForYear(2015)
				.forEach(id -> result.add(client.doGet(reportsUri + id + EXPENSES_ENDPOINT, Client.ACCESS_KEY)));
		return result;
	}

	public List<String> listAllImageUrls() {
		List<String> result = new ArrayList<>();
		listAllReportIds().forEach(id -> result.addAll(getPictureUrlsPerReport(id)));
		return result;
	}

	public List<String> getPictureUrlsPerReport(Integer reportId) {
		List<String> result = new ArrayList<>();

		String uri = BASE_URI + REPORTS_ENDPOINT + "/" + reportId + EXPENSES_ENDPOINT;
		String expensesResponse = client.doGet(uri, Client.ACCESS_KEY);
		JSONObject expensesJson = new JSONObject(expensesResponse);

		JSONArray expensesJsonArray = expensesJson.getJSONArray("expenses");
		expensesJsonArray.forEach(exp -> result
				.add(((JSONObject) exp).getJSONObject("picture").getJSONObject("urls").getString("original")));

		return result;
	}

	public List<String> listAllCustomersAsJsonStrings() {
		List<String> result = new ArrayList<>();
		String customersUri = BASE_URI + CUSTOMERS_ENDPOINT + "/1631/accounts";
		String allCustomersResponse = client.doGet(customersUri, Client.ACCESS_KEY);

		System.out.println(allCustomersResponse);

		JSONObject allCustomersJson = new JSONObject(allCustomersResponse);
		JSONArray customersArray = allCustomersJson.getJSONArray("customers");
		customersArray.forEach(customer -> result.add(customer.toString()));

		return result;
	}

	public List<Integer> listReportIdsForDateRange(Date startDate, Date endDate) {
		List<Integer> ids = new ArrayList<>();
		String start = dateFormat.format(startDate);
		String end = dateFormat.format(endDate);
		String uri = BASE_URI + REPORTS_ENDPOINT + "?filter=date_from:" + start + "+and+date_until:" + end;
		String respStr = client.doGet(uri, Client.ACCESS_KEY);
		JSONObject resp = new JSONObject(respStr);

		if (resp.has("reports")) {
			JSONArray reportsArray = (JSONArray) resp.get("reports");
			reportsArray.forEach(r -> ids.add(((JSONObject) r).getInt("id")));
		}
		return ids;
	}

	public Set<Integer> listReportIdsForYear(Integer year) {
		Set<Integer> ids = new HashSet<>();

		Calendar cal = Calendar.getInstance();
		cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
		cal.set(Calendar.WEEK_OF_YEAR, 1);

		for (int i = 49; i < 53; i++) {
			cal.set(Calendar.WEEK_OF_YEAR, i);
			cal.set(Calendar.YEAR, year);
			Date start = cal.getTime();

			cal.set(Calendar.WEEK_OF_YEAR, i + 1);
			cal.set(Calendar.YEAR, year);
			Date end = cal.getTime();

			// System.out.println(start+"-"+end);
			ids.addAll(listReportIdsForDateRange(start, end));
		}
		return ids;
	}

	public List<InvoiceContent> listAllInvoicesForYear(Integer year) {
		List<InvoiceContent> allInvoices = new ArrayList<>();

		for (Integer id : listReportIdsForYear(year)) {
			List<InvoiceContent> ics = listInvoicesPerReport(id);
			allInvoices.addAll(ics);
		}
		return allInvoices;
	}

	public void postAllInvoicesForYear(Integer year) {
		Map<String, String> clientId2taxeoId = new HashMap<>();
		clientId2taxeoId.put("2000000087", "6363120070000009");
		clientId2taxeoId.put("2000000002", "6363120210000141");

		for (Integer id : listReportIdsForYear(year)) {
			List<InvoiceContent> ics = listInvoicesPerReport(id);
			for (InvoiceContent ic : ics) {
				UploadInvoicesRequest req = new UploadInvoicesRequest();
				req.setClientId(ic.getInvoice().getClientId());
				req.setTaxeoId(clientId2taxeoId.get(req.getClientId()));
				req.addInvoice(ic);
				req.setSource("MW");
				client.tipsPost(req);
			}
		}

	}

	private List<InvoiceContent> listInvoicesPerReport(Integer id) {
		String uri = BASE_URI + REPORTS_ENDPOINT + "/" + id + EXPENSES_ENDPOINT;
		String expensesResponse = client.doGet(uri, Client.ACCESS_KEY);
		JSONObject expensesJson = new JSONObject(expensesResponse);
		List<InvoiceContent> ics = srxpExpenseToTipsInvoice(expensesJson);
		return ics;
	}

	private List<InvoiceContent> srxpExpenseToTipsInvoice(JSONObject expenseWrapper) {
		
		
		List<InvoiceContent> invoices = new ArrayList<>();

		JSONArray expensesArray = expenseWrapper.getJSONArray("expenses");
		JSONArray categoriesArray = expenseWrapper.getJSONArray("categories");
		JSONArray currenciesArray = expenseWrapper.getJSONArray("currencies");
		JSONArray paymentMethodsArray = expenseWrapper.has("payment_methods")
				? expenseWrapper.getJSONArray("payment_methods") : null;
		//JSONArray vatsArray = expenseWrapper.has("vats") ? expenseWrapper.getJSONArray("vats") : null;
		JSONArray accountsArray = expenseWrapper.getJSONArray("accounts");

		Map<Integer, Country> countryMap = fillCountryMap(paymentMethodsArray);
		Map<Integer, String> categoryMap = fillCategoryMap(categoriesArray);
		Map<Integer, Currency> currencyMap = fillCurrencyMap(currenciesArray);
		//Map<Integer, BigDecimal> vatMap = fillVatMap(vatsArray);
		Map<Integer, Integer> accountsMap = fillAccountsMap(accountsArray);

		
		for (int i = 0; i < expensesArray.length(); i++) {
			JSONObject expenseObj = expensesArray.getJSONObject(i);
			Integer paymentMethodId = expenseObj.has("payment_method_id") ? expenseObj.getInt("payment_method_id")
					: null;
			Country country = countryMap.get(paymentMethodId);
			if (country == null) {
				continue;
			}
			Integer currencyId = expenseObj.getInt("currency_id");
			Currency currency = currencyMap.get(currencyId);
			if (currency.getName().matches("NOK|CHF|USD")) {
				continue;
			}
			
			Integer accountId = expenseObj.getInt("account_id");
			Integer customerId = accountsMap.get(accountId);
			if ((country.getIsoCode().equals("DK") && customerId == 1631) || (country.getIsoCode().equals("NL") && customerId == 1632)) {
				continue;
			}

			Invoice invoice = new Invoice();
			JSONArray amountsArray = expenseObj.getJSONArray("amounts");
			addItemsToInvoice(amountsArray, categoryMap, invoice);
			if (invoice.getInvoiceItems().isEmpty()) {
				continue;
			}
			
			
			
			String clientId = clientIdMappings.get(customerId);

			

			String expenseDate = expenseWrapper.has("expense_date")
					? expenseWrapper.getString("expense_date").substring(0, 10) : null;

			invoice.setClientId(clientId);

			invoice.setDate(expenseDate);
			invoice.setCurrency(currency);
			invoice.setCountry(country);
			invoice.setValid(false);

			InvoiceContent ic = new InvoiceContent();
			Integer reportId = expenseObj.getInt("report_id");
			JSONObject picture = expenseObj.getJSONObject("picture");
			String fileName = picture.getString("name");
			String fileUrl = picture.getJSONObject("urls").getString("original");

			byte[] fileContent = client.doGetBytes(fileUrl);

			ic.setReportId(reportId.toString());
			ic.setFileContent(fileContent);
			ic.setFileName(fileName);
			ic.setFileUrl(fileUrl);
			ic.setInvoice(invoice);

			invoices.add(ic);
		}
		
		
		return invoices;
	}

	private Map<Integer, Integer> fillAccountsMap(JSONArray accountsArray) {
		Map<Integer, Integer> result = new HashMap<>();
		for (int i = 0; i < accountsArray.length(); i++) {
			JSONObject category = (JSONObject) accountsArray.get(i);
			Integer id = category.getInt("id");
			result.put(id, category.getInt("customer_id"));
		}
		return result;
	}

	private void addItemsToInvoice(JSONArray amountsArray, Map<Integer, String> categoryMap,
			Invoice invoice) {
		for (int j = 0; j < amountsArray.length(); j++) {
			JSONObject amountObj = amountsArray.getJSONObject(j);
			String category = categoryMap.get(amountObj.getInt("category_id"));
			String sapCodes = sapMapping.get(category);
			//BigDecimal vatPercentage = vatMap.get(amountObj.getInt("vat_id"));
			BigDecimal vatPaid = amountObj.getBigDecimal("vat_amount");
			if (sapCodes != null /*&& vatPaid.compareTo(ZERO) != 0*/) {
				
				String[] codes = sapCodes.split(":");
				String prestationCode = codes[0];
				String portalCode = codes[1];
				if (prestationCode.equals("0")) {
					continue;
				}

				BigDecimal vatBase = amountObj.getBigDecimal("amount");
				BigDecimal vatPercentage = vatPaid.divide(vatBase, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
				BigDecimal grossPaid = vatBase.add(vatPaid);
				InvoiceItem item = new InvoiceItem(grossPaid, portalCode, prestationCode, new BigDecimal("1"),
						vatBase, vatPaid, vatPercentage);
				item.setExpenseTypeId(prestationCode);
				invoice.addInvoiceItem(item);
			}
		}
	}

	private Map<Integer, String> fillCategoryMap(JSONArray categoriesArray) {
		Map<Integer, String> result = new HashMap<>();
		for (int i = 0; i < categoriesArray.length(); i++) {
			JSONObject category = (JSONObject) categoriesArray.get(i);
			Integer id = category.getInt("id");
			result.put(id, category.getString("name"));
		}
		return result;
	}

//	private Map<Integer, BigDecimal> fillVatMap(JSONArray vatsArray) {
//		Map<Integer, BigDecimal> result = new HashMap<>();
//		if (vatsArray == null) {
//			return result;
//		}
//		for (int i = 0; i < vatsArray.length(); i++) {
//			JSONObject vat = (JSONObject) vatsArray.get(i);
//			Integer id = vat.getInt("id");
//			Integer percentage = vat.getInt("percentage");
//			Boolean displayInclusive = vat.has("display_inclusive") ? vat.getBoolean("display_inclusive") : false;
//			if (!displayInclusive) {
//				displayInclusiveFalse++;
//			}
//			boolean hasVat = percentage != 0;
//			if (!hasVat) {
//				emptyVats++;
//			}
//			if (displayInclusive && hasVat) {
//				result.put(id, new BigDecimal(percentage));
//			}
//		}
//		return result;
//	}

	private Map<Integer, Country> fillCountryMap(JSONArray paymentMethodsArray) throws JSONException {
		Map<Integer, Country> countryMap = new HashMap<>();
		if (paymentMethodsArray == null) {
			return countryMap;
		}
		for (int i = 0; i < paymentMethodsArray.length(); i++) {
			JSONObject method = (JSONObject) paymentMethodsArray.get(i);
			String methodName = method.getString("name");
			if (methodName.equals("1. non- EU")) {
				continue;
			}
			Integer methodId = method.getInt("id");
			String iso3code = method.getString("code");
			String iso2code = isoCodeMappings.get(iso3code);
			countryMap.put(methodId, new Country(iso2code, true, methodName));
		}
		return countryMap;
	}

	private Map<Integer, Currency> fillCurrencyMap(JSONArray currenciesArray) throws JSONException {
		Map<Integer, Currency> currencyMap = new HashMap<>();
		for (int i = 0; i < currenciesArray.length(); i++) {
			JSONObject currency = (JSONObject) currenciesArray.get(i);
			Integer currencyId = currency.getInt("id");
			currencyMap.put(currencyId, new Currency(currencyId.toString(), currency.getString("short")));
		}
		return currencyMap;
	}

}
