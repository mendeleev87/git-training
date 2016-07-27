package com.taxeo.mw.srxp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.taxeo.sbus.srxp.SRXPFacade;
import com.taxeo.tips.services.InvoiceContent;
import com.taxeo.tips.services.UploadInvoicesRequest;

public class SRXPFacadeTest {

	private SRXPFacade facade;

	private Marshaller m;

	@Before
	public void init() throws JAXBException {
		facade = new SRXPFacade();
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

		JAXBContext jaxbContext = JAXBContext.newInstance(new Class[] { UploadInvoicesRequest.class });
		m = jaxbContext.createMarshaller();
		m.setProperty("eclipselink.media-type", "application/json");
	}

	@Test
	public void testListAllReportIds() throws IOException {
		Set<Integer> reportIds = facade.listReportIdsForYear(2015);
		System.out.println(reportIds.size());
		// reportIds.forEach(System.out::println);
	}

	@Test
	@Ignore
	public void testListAllImages() {
		List<String> urls = facade.listAllImageUrls();
		urls.forEach(System.out::println);
	}

	@Test
	public void testListAllExpensesAsJsonStrings() throws IOException {
		FileUtils.writeLines(new File("expensesTest"), facade.listAllExpensesAsJsonStrings());
	}

	@Test
	public void testListUniqueCustomerIds() throws IOException {
		Set<Integer> ids = new HashSet<>();
		for (String str : facade.listAllExpensesAsJsonStrings()) {
			JSONObject expenseWrapper = new JSONObject(str);
			JSONArray accountArr = expenseWrapper.getJSONArray("accounts");
			accountArr.forEach(a -> ids.add(((JSONObject) a).getInt("customer_id")));
		}
	}

	@Test
	public void testListAllCustomersAsJsonStrings() throws IOException {
		FileUtils.writeLines(new File("customersTest"), facade.listAllCustomersAsJsonStrings());
	}

	@Test
	public void testReportsJson() throws IOException {
		FileUtils.writeStringToFile(new File("reportsTest"), facade.getReportsJsonString(), Charset.forName("UTF-8"));
	}

	@Test
	public void testDumpAllInvoices() throws JAXBException {
		Map<String, String> clientId2taxeoId = new HashMap<>();
		clientId2taxeoId.put("2000000087", "6363120070000009");
		clientId2taxeoId.put("2000000002", "6363120210000141");

		List<InvoiceContent> invoices = facade.listAllInvoicesForYear(2015);

		int count = 0;
		
		for (InvoiceContent ic : invoices) {
			UploadInvoicesRequest req = new UploadInvoicesRequest();
			req.setSource("MW");
			req.setClientId(ic.getInvoice().getClientId());
			req.setTaxeoId(clientId2taxeoId.get(req.getClientId()));
			req.addInvoice(ic);
			m.marshal(req, new File("output/allExpensesRequest" + count++ + ".json"));
		}

	}

	private void marshallNewBatchRequest(Map<String, String> clientId2taxeoId, int count1, InvoiceContent ic,
			List<InvoiceContent> invoiceList) throws JAXBException {
		UploadInvoicesRequest req = new UploadInvoicesRequest();
		req.setSource("MW");
		req.setClientId(ic.getInvoice().getClientId());
		req.setTaxeoId(clientId2taxeoId.get(req.getClientId()));
		req.setInvoices(invoiceList);
		
		int batchIndex = count1/101;
		m.marshal(req, new File("output/allExpensesRequest" + batchIndex + ".json"));
	}

	@Test
	public void testPostAllInvoices() {
		facade.postAllInvoicesForYear(2015);
	}

}
