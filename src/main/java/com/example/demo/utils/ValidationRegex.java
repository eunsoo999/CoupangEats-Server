package com.example.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexPhone(String target) {
        String regex = "^01(?:0|1|[6-9])[0-9]{4}[0-9]{4}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexPassword(String target) {
        // 영문/숫자/특수문자 2가지 이상 조합(8~20자)
        String regexEnglishAndNum = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,20}$";
        String regexEnglishAndSpecial = "^(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,20}$";
        String regexSpecialAndNum = "^(?=.*[^a-zA-Z0-9])(?=.*[0-9]).{8,20}$";

        Pattern pattern = Pattern.compile(regexEnglishAndNum, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        boolean resultEnglishAndNum = matcher.find();

        pattern = Pattern.compile(regexEnglishAndSpecial, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(target);
        boolean resultEnglishAndSpecial = matcher.find();

        pattern = Pattern.compile(regexSpecialAndNum, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(target);
        boolean resultSpecialAndNum = matcher.find();

        if(!(resultEnglishAndNum || resultEnglishAndSpecial || resultSpecialAndNum)){
            return false;
        }
        return true;
    }

    public static boolean isRegexPasswordSequence(String target) {
        // 3개 이상 연속되거나 동일한 문자/숫자 제외
        String regexSequence = "(\\w)\\1\\1";
        Pattern pattern = Pattern.compile(regexSequence, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        boolean resultSequence = matcher.find();

        if(resultSequence) {
            return false;
        }
        return true;
    }

    public static boolean isRegexName(String target) {
        String regex = "^[가-힣]{2,30}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexLatitude(String target) {
        String regex = "^(\\+|-)?(?:90(?:(?:\\.0{1,10})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,10})?))$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexLongitude(String target) {
        String regex = "^(\\+|-)?(?:180(?:(?:\\.0{1,10})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,10})?))$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}

