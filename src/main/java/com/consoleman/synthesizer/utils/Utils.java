package com.consoleman.synthesizer.utils;

public class Utils {
    public static void handleProcedure (Procedure procedure, boolean printStackTrace) {
        try {
            procedure.invoke();
        }
        catch (Exception e) {
            if(printStackTrace) {
                e.printStackTrace();
            }
        }
    }
}
