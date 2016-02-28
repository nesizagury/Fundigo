package com.example.FundigoApp.Customer.CustomerMenu;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mirit-binbin on 28/02/2016.
 */
@ParseClassName("creditCards")
public class CreditCard extends ParseObject
{
    public String getIdCostumer() {
        return getString ("IdCostumer");
    }

    public void setIdCustomer(String Id) {
        put ("Id", Id);
    }


    public Object getToken() {
        return get("Token");
    }

    public void setToken(Object token) {
        put ("Token", token);
    }

}
