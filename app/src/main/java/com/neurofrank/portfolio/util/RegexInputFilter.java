package com.neurofrank.portfolio.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexInputFilter implements InputFilter {

    private static final String CLASS_NAME = RegexInputFilter.class.getSimpleName();
    private Pattern mPattern;

    public RegexInputFilter(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(CLASS_NAME + " requires a regex.");
        }

        mPattern = pattern;
    }

    public RegexInputFilter(String pattern) {
        this(Pattern.compile(pattern));
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(source);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}
