package edu.unika.aifb.utils;

/*
 * Copyright (c) 2004 Roland Küstermann. All Rights Reserved.
 */

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * Created by IntelliJ IDEA.
 * User: roku
 * Date: 20.10.2004
 * Time: 15:15:36
 * To change this template use File | Settings | File Templates.
 */
public class Resources {

    private ResourceBundle bundle;

    public Resources(String bundle) {
        try {
            this.bundle = ResourceBundle.getBundle(bundle, Locale.getDefault(), Thread
                    .currentThread().getContextClassLoader());
            if (bundle == null) {
                this.bundle = ResourceBundle.getBundle(bundle, new Locale("en_EN"), Thread
                        .currentThread().getContextClassLoader());
            }
        } catch (Exception e) {
            this.bundle = ResourceBundle.getBundle(bundle, Locale.getDefault());
        }
    }

    public String getResourceString(String name) {
        try {
            return bundle.getString(name);
        } catch (MissingResourceException e) {
            return name;
        }
    }

}