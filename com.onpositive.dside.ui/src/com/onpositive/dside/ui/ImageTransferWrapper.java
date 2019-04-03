/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.eclipse.swt.dnd.TransferData
 */
package com.onpositive.dside.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.swt.dnd.TransferData;

public class ImageTransferWrapper {
    static Class<?> transfer;

    static {
        try {
            transfer = Class.forName("org.eclipse.swt.dnd.ImageTransfer");
        }
        catch (ClassNotFoundException v0) {}
    }

    public static boolean isAvalable() {
        if (transfer != null) {
            return true;
        }
        return false;
    }

    public static Object getInstance() {
        try {
            Method method = transfer.getMethod("getInstance", new Class[0]);
            return method.invoke(null, new Object[0]);
        }
        catch (SecurityException v0) {
        }
        catch (NoSuchMethodException v1) {
        }
        catch (IllegalArgumentException v2) {
        }
        catch (IllegalAccessException v3) {
        }
        catch (InvocationTargetException v4) {}
        return null;
    }

    public static boolean isSupportedType(TransferData d) {
        try {
            Method method = transfer.getMethod("isSupportedType", TransferData.class);
            return (Boolean)method.invoke(ImageTransferWrapper.getInstance(), new Object[]{d});
        }
        catch (SecurityException v0) {
        }
        catch (NoSuchMethodException v1) {
        }
        catch (IllegalArgumentException v2) {
        }
        catch (IllegalAccessException v3) {
        }
        catch (InvocationTargetException v4) {}
        return false;
    }
}

