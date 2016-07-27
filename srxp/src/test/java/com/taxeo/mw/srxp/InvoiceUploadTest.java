package com.taxeo.mw.srxp;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.taxeo.sbus.srxp.Client;
import com.taxeo.tips.services.Country;
import com.taxeo.tips.services.Currency;
import com.taxeo.tips.services.Invoice;
import com.taxeo.tips.services.InvoiceContent;
import com.taxeo.tips.services.InvoiceItem;
import com.taxeo.tips.services.UploadInvoicesRequest;

public class InvoiceUploadTest {

	private static final String mockPicturePath = "src/test/resources/mockPicture.jpg";

	private UploadInvoicesRequest mockRequest;
	
	private Client client;

	@Before
	public void init() throws IOException, JAXBException {
		client = new Client();
		String clientId = "2000000058";
		mockRequest = new UploadInvoicesRequest();
		mockRequest.setClientId(clientId);
		mockRequest.setTaxeoId("6363120110010117");
		mockRequest.setSource("SRXP");

		BigDecimal grossPaid = new BigDecimal("11.0");
		String portalCode = "10.8";
		String prestationCode = "EU100008";
		BigDecimal quantity = new BigDecimal("1");
		BigDecimal vatBase = new BigDecimal("10.0");
		BigDecimal vatPaid = new BigDecimal("1.0");
		BigDecimal vatPercentage = new BigDecimal("10.0");
		Currency currency = new Currency("DKK", "Danish Krone");
		Country country = new Country("DK", true, "Denmark");

		InvoiceItem item = new InvoiceItem(grossPaid, portalCode, prestationCode, quantity, vatBase, vatPaid,
				vatPercentage);
		item.setExpenseTypeId(prestationCode);
		String date = "2015-11-06";
		Invoice invoice = new Invoice(clientId, country, date, null, null, null, currency);
		invoice.addInvoiceItem(item);

		byte[] fileContent = FileUtils.readFileToByteArray(new File(mockPicturePath));
		InvoiceContent invoiceContent = new InvoiceContent(invoice, "mockInvoice.jpg", fileContent);
		invoiceContent.setFileUrl(
				"https://s3-eu-west-1.amazonaws.com/srxp-prd-bucket/receipts/159509_ef993027ba721870f7dd819fdd3bdb26f98730cd_0_thumb.jpg?1411131001");
	
		mockRequest.addInvoice(invoiceContent);
		
	}
	
	@Test
	public void testTipsPost() throws JAXBException {
		System.out.println(client.tipsPost(mockRequest));
	}

}
