package com.data.invoice;

import com.data.invoice.controller.InvoiceGeneratorController;
import com.data.invoice.models.InvoiceResponse;
import com.data.invoice.models.Items;

import java.util.List;
import java.util.Map;

public class InvoiceGenerator {
    public static void main(String[] args) {
        InvoiceGeneratorController invoiceGeneratorController = new InvoiceGeneratorController();
        Map<String, List<Items>> mappedItems = invoiceGeneratorController.readInputfromFile();
        List<InvoiceResponse> invoiceResponseList = invoiceGeneratorController.calculateTaxForItems(mappedItems);
        invoiceResponseList.forEach(invoiceResponse -> {
            System.out.println("Items: ");
            invoiceResponse.getItemsList().forEach(items -> {
                System.out.println(items.getBillingName()+":"+items.getTotalAmount());
            });
            System.out.println("Total Tax: " + invoiceResponse.getTotalTax());
            System.out.println("Total Amount: " + invoiceResponse.getTotalAmount());
            System.out.println("++++++++++++++Next Input+++++++++++++++");
        });
    }
}
