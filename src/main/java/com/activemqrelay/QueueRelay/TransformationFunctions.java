package com.activemqrelay.QueueRelay;

public class TransformationFunctions {

    public static String toUpperCase(String input) {
        return input.toUpperCase();
    }

    public static String addStars(String input) {
        return "**" + input + "**";
    }
}
