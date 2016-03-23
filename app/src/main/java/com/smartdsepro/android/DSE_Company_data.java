package com.smartdsepro.android;
/*This class is used for
 * Stroring and individual item information
 */

public class DSE_Company_data {

    private String laste_trade;
    private String change_ammount;
    private String change_percentage;
    private String company_Name;
    private String numberofstocks;
    private String purchaceunit;
    private String gain_loss_ammount;
    private String gain_loss_percentage;
    private String total_parchase_lasttrade;
    private String total_purchase_unitprice;
    private String value;
    private String volume;
    private int dse_item_color;
    private String high;
    private String low;
    private String closeprice;
    private String yesterdaycloseprice;
    private String news;
    private String concernvalue;


    public String getCompany_Name() {
        return company_Name;
    }

    public String getLaste_trade() {
        return laste_trade;
    }

    public String getChange_ammount() {
        return change_ammount;
    }

    public String getChange_percentage() {
        return change_percentage;
    }

    // Constructor for latest news
    public DSE_Company_data(String company_Name, String news) {
        this.company_Name = company_Name;
        this.news = news;
    }

    // Constructor for top 10 gainers & loosers
    public DSE_Company_data(String company_Name, String closeprice,
                            String high, String low, String yesterdaycloseprice,
                            String change_percentage) {
        this.company_Name = company_Name;
        this.closeprice = closeprice;
        this.high = high;
        this.low = low;
        this.yesterdaycloseprice = yesterdaycloseprice;
        this.change_percentage = change_percentage.concat("%");

    }

    // Constructor for DSE30 items
    public DSE_Company_data(String company_Name, String laste_trade,
                            String change_percentage, String volume, int dse_item_color,
                            boolean fau) {
        this.company_Name = company_Name;
        this.laste_trade = laste_trade;
        this.change_ammount = change_percentage.concat("%");
        this.volume = volume;
        this.change_percentage = change_percentage;
        this.dse_item_color = dse_item_color;

    }

    // Constructor for top 20 shares
    public DSE_Company_data(String company_Name, String last_trade,
                            String concernvalue) {
        this.company_Name = company_Name;
        this.laste_trade = last_trade;

        this.concernvalue = concernvalue;

    }

    public DSE_Company_data(String company_Name, String laste_trade,
                            String change_ammount, String change_percentage, String volume, int dse_item_color) {

        this.company_Name = company_Name;
        this.laste_trade = laste_trade;
        this.change_ammount = change_ammount;
        this.change_percentage = change_percentage;
        this.dse_item_color = dse_item_color;
        this.volume = volume;
    }

    // Constructor for portfolio

    public DSE_Company_data(String company_Name, String last_trade,
                            String numberofstocks, String purchaceunit, int dse_item_color,
                            float fau) {
        this.company_Name = company_Name;
        this.laste_trade = last_trade;
        this.numberofstocks = numberofstocks;
        this.purchaceunit = purchaceunit;
        this.dse_item_color = dse_item_color;
        float numberofstocks_f = Float.parseFloat(numberofstocks);
        float purchaseunit_f = Float.parseFloat(purchaceunit);
        float lasttrade_f = Float.parseFloat(last_trade);
        float total_purchase_lattrade_f = numberofstocks_f * lasttrade_f;
        float total_purchase_purchaseunit_f = numberofstocks_f * purchaseunit_f;
        float gain_loss_value_f = total_purchase_lattrade_f
                - total_purchase_purchaseunit_f;
        float gain_loss_percentage_f = (gain_loss_value_f / total_purchase_purchaseunit_f) * 100;
        setGain_loss_ammount(Float.toString(gain_loss_value_f));
        setGain_loss_percentage(Float.toString(gain_loss_percentage_f));
        setTotal_parchase_lasttrade(Float.toString(total_purchase_lattrade_f));
        setTotal_purchase_unitprice(Float
                .toString(total_purchase_purchaseunit_f));

    }

    public int getDse_item_color() {
        return dse_item_color;
    }

    public void setDse_item_color(int dse_item_color) {
        this.dse_item_color = dse_item_color;
    }

    public String getNumberofstocks() {
        return numberofstocks;
    }

    public void setNumberofstocks(String numberofstocks) {
        this.numberofstocks = numberofstocks;
    }

    public String getPurchaceunit() {

        return mod_precision(purchaceunit);
    }
    public String mod_precision(String passed_string) {
        String fin = String.format("%.2f", Double.parseDouble(passed_string));
        return fin;
    }

    public void setPurchaceunit(String purchaceunit) {
        this.purchaceunit = purchaceunit;
    }

    public String getGain_loss_ammount() {

        return mod_precision(gain_loss_ammount);
    }

    public void setGain_loss_ammount(String gain_loss_ammount) {
        this.gain_loss_ammount = gain_loss_ammount;
    }

    public String getGain_loss_percentage() {
        return mod_precision(gain_loss_percentage) + "%";
    }

    public void setGain_loss_percentage(String gain_loss_percentage) {
        this.gain_loss_percentage = gain_loss_percentage;
    }

    public String getTotal_parchase_lasttrade() {
        return total_parchase_lasttrade;
    }

    public void setTotal_parchase_lasttrade(String total_parchase_lasttrade) {
        this.total_parchase_lasttrade = total_parchase_lasttrade;
    }

    public String getTotal_purchase_unitprice() {
        return total_purchase_unitprice;
    }

    public void setTotal_purchase_unitprice(String total_purchase_unitprice) {
        this.total_purchase_unitprice = total_purchase_unitprice;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getCloseprice() {
        return closeprice;
    }

    public void setCloseprice(String closeprice) {
        this.closeprice = closeprice;
    }

    public String getYesterdaycloseprice() {
        return yesterdaycloseprice;
    }

    public void setYesterdaycloseprice(String yesterdaycloseprice) {
        this.yesterdaycloseprice = yesterdaycloseprice;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getConcernvalue() {
        return concernvalue;
    }

    public void setConcernvalue(String concernvalue) {
        this.concernvalue = concernvalue;
    }

}
