package com.data.invoice.models;

import com.data.invoice.enums.ItemCategory;

import java.io.Serializable;

public class ItemMaster implements Serializable {

    String item;
    ItemCategory category;
    Boolean isImported;
    Double tax;

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Boolean getImportedType() {
        return isImported;
    }

    public void setImportedType(Boolean importedType) {
        this.isImported = importedType;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }
}
