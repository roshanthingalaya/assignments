package test.com.data.invoice.controller; 

import com.data.invoice.controller.InvoiceGeneratorController;
import com.data.invoice.models.InvoiceResponse;
import com.data.invoice.models.Items;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InvoiceGeneratorControllerTest {

    Map<String, List<Items>> mappedItems;

@Before
public void before() throws Exception {
    InvoiceGeneratorController invoiceGeneratorController = new InvoiceGeneratorController();
    Map<String, List<Items>> mappedItems = invoiceGeneratorController.readInputfromFile();
    this.mappedItems = mappedItems;
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: readInputfromFile() 
* 
*/ 
@Test
public void testReadInputfromFile() throws Exception { 
//TODO: Test goes here...
    InvoiceGeneratorController invoiceGeneratorController = new InvoiceGeneratorController();
    Map<String, List<Items>> mappedItems = invoiceGeneratorController.readInputfromFile();
    this.mappedItems = mappedItems;
    assertNotNull(mappedItems);
} 

/** 
* 
* Method: calculateTaxForItems(Map<String, List<Items>> mappedItems) 
* 
*/ 
@Test
public void testCalculateTaxForItems() throws Exception { 
//TODO: Test goes here...

    InvoiceGeneratorController invoiceGeneratorController = new InvoiceGeneratorController();
    List<InvoiceResponse> invoiceResponseList = invoiceGeneratorController.calculateTaxForItems(this.mappedItems);
    for (int i = 0; i < invoiceResponseList.size(); i++) {
        if(i==0){
            assertEquals("1.5", String.valueOf(invoiceResponseList.get(i).getTotalTax()));
            assertEquals("29.85",String.valueOf(invoiceResponseList.get(i).getTotalAmount()));
        }else if(i==1){
            assertEquals("7.65", String.valueOf(invoiceResponseList.get(i).getTotalTax()));
            assertEquals("65.15",String.valueOf(invoiceResponseList.get(i).getTotalAmount()));
        }else if(i==3){
            assertEquals("6.70", String.valueOf(invoiceResponseList.get(i).getTotalTax()));
            assertEquals("74.68",String.valueOf(invoiceResponseList.get(i).getTotalAmount()));
        }
    }


} 


} 


