package net.bigtangle.wallet.activity.settings.model;

public class ContactInfoItem {

    private String contactName;

    private String address;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static ContactInfoItem build(String contactName, String address) {
        ContactInfoItem contactInfoItem = new ContactInfoItem();
        contactInfoItem.setContactName(contactName);
        contactInfoItem.setAddress(address);
        return contactInfoItem;
    }
}
