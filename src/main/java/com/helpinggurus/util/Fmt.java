package com.helpinggurus.util;

/** JSP EL function: formats rupees with Indian digit grouping (e.g. 16,00,00,000). */
public final class Fmt {
    private Fmt() {}
    /** Formats an amount with the rupee sign and Indian digit grouping, e.g. 1600000 becomes ₹16,00,000. */
    public static String inr(double amount) {
        String s = Long.toString(Math.abs(Math.round(amount)));
        if (s.length() > 3) {
            String last3 = s.substring(s.length() - 3), rest = s.substring(0, s.length() - 3);
            rest = rest.replaceAll("(\\d)(?=(\\d\\d)+$)", "$1,");
            s = rest + "," + last3;
        }
        return "\u20B9" + (amount < 0 ? "-" : "") + s;
    }
}
