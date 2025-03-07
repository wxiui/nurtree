package com.csstj.nurtree.common;

import java.util.UUID;

public class AppCodeGenerator {
    public static String generateAppCode() {
        return UUID.randomUUID().toString();
    }
}