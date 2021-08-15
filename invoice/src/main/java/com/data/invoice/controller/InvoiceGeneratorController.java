package com.data.invoice.controller;

import com.data.invoice.InvoiceGenerator;
import com.data.invoice.enums.ItemCategory;
import com.data.invoice.models.InvoiceResponse;
import com.data.invoice.models.ItemMaster;
import com.data.invoice.models.Items;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class InvoiceGeneratorController {

    public Map<String,List<Items>> readInputfromFile(){
        Scanner inputScanner = new Scanner(InvoiceGenerator.class.getResourceAsStream("/input"));
        Scanner mapperScanner = new Scanner(InvoiceGenerator.class.getResourceAsStream("/itemMapper"));
        List<ItemMaster> itemMasterList = new ArrayList<>();

        while(mapperScanner.hasNextLine()){
            String[] strings = mapperScanner.nextLine().split(",");
            ItemMaster master = new ItemMaster();
            master.setItem(strings[0]);
            master.setImportedType(Boolean.valueOf(strings[1]));
            master.setCategory(ItemCategory.valueOf(strings[2]));
            master.setTax(Double.valueOf(strings[3]));
            itemMasterList.add(master);
        }

        Map<String,List<Items>> mapperItems = new HashMap<>();
        List<Items> itemsList = null;
        String inputVal = "";
        while(inputScanner.hasNextLine()){

            String line = inputScanner.nextLine();

            if(line.isEmpty()){
                mapperItems.putIfAbsent(inputVal,itemsList);
            }
            if(line.contains("Input ")){
                inputVal = line;
                itemsList = new ArrayList<>();
            }else{
                String[] wordcount = line.split(" ");
                if(wordcount.length>1) {
                    Items items = new Items();
                    items.setQuantity(Integer.valueOf(wordcount[0]));
                    items.setAmount(Double.valueOf(wordcount[wordcount.length - 1]));
                    items.setImported(line.contains("imported") ? true : false);

                    String[] itemBilling = line.split(" at");
                    String billing = itemBilling[0];

                     items.setBillingName(billing);

                    String item = line.replaceFirst(wordcount[0],"");
                    item = item.replace(wordcount[wordcount.length-1],"" );
                    if(line.contains("imported")){
                        item = item.replaceFirst(" imported","");
                    }
                    item = item.substring(0,item.lastIndexOf("at")).trim().replaceAll(" ","_");

                    for (int i = 1; i < wordcount.length-1; i++) {
                            //String word = wordcount[i].toLowerCase().trim().replace(" ","");
                        String finalItem = item;
                        Predicate<ItemMaster> itemMasterPredicate = (itemMaster -> itemMaster.getItem().equals(finalItem) && itemMaster.getImportedType().equals(items.getImported()));
//                            itemMasterPredicate.and(itemMaster -> itemMaster.getImportedType().equals(items.getImported()));
                            Optional<ItemMaster> optionalItemMaster = itemMasterList.stream().filter(itemMasterPredicate).findFirst();
                            if(optionalItemMaster.stream().allMatch(itemMasterPredicate)){
                                items.setItem(optionalItemMaster.get().getItem());
                                items.setCategory(optionalItemMaster.get().getCategory());
                                items.setTax(optionalItemMaster.get().getTax());
                            }
                        }


                    itemsList.add(items);
                }

            }
        }

        if(itemsList.size()>0){
            mapperItems.putIfAbsent(inputVal,itemsList);
        }

        return mapperItems;
    }

    public List<InvoiceResponse> calculateTaxForItems(Map<String, List<Items>> mappedItems) {

        List<InvoiceResponse> invoiceResponseList = new ArrayList<>();
        mappedItems.forEach((s, items) -> {

            InvoiceResponse invoiceResponse = new InvoiceResponse();
            invoiceResponse.setItemsList(items);
            Map<String, Double> taxMap = getAmountForItems(items);
            invoiceResponse.setTotalTax(taxMap.get("tax"));
            invoiceResponse.setTotalAmount(taxMap.get("total"));
            invoiceResponseList.add(invoiceResponse);
        });

        //Double totalAmount = getTotalAmountForItems(mappedItems,totalTax);
        return invoiceResponseList;
    }

    private Map<String,Double> getAmountForItems(List<Items> itemsList) {

        Map<String,Double> taxMap = new HashMap<>();
        AtomicReference<Double> totalTax = new AtomicReference<>(0.0);
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
        itemsList.forEach(items -> {

            totalTax.set(totalTax.get() + (items.getAmount() * (items.getTax()/100)));
            BigDecimal bdTax = BigDecimal.valueOf(totalTax.get());
            bdTax = bdTax.setScale(2, RoundingMode.HALF_UP);
            totalTax.set(Math.round(bdTax.doubleValue()*20)/20.0);

            items.setTotalAmount(items.getAmount() + ((items.getAmount() *items.getTax()/100)));

            BigDecimal bd = BigDecimal.valueOf(items.getTotalAmount());
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            items.setTotalAmount(Math.round(bd.doubleValue()*20)/20.0);

            totalAmount.set(totalAmount.get() + items.getTotalAmount());

        });
        //totalAmount.set(totalAmount.get());
        //totalAmount.set(Math.round(totalAmount.get()*20.0)/20.0);
        //totalTax.set(Math.round(totalTax.get()*20.0)/20.0);
        taxMap.putIfAbsent("tax",totalTax.get());
        taxMap.putIfAbsent("total",totalAmount.get());
        return taxMap;

    }
}
