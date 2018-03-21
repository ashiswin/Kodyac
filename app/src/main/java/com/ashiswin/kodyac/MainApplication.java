package com.ashiswin.kodyac;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by ashis on 3/21/2018.
 */

public class MainApplication extends Application {
    public static final String SERVER_URL = "http://www.kodyac.tech/scripts/";
    int linkId;
    int companyId;

    HashMap<String, Boolean> methods;
    String[] methodNames;

    String name;
    String address;
    String nric;
    String contact;
    String nationality;
    String dob;
    String sex;
    String race;
}
