package manifoldsimplequery.extensions.java.lang.Comparable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import java.lang.Comparable;
import java.util.Arrays;

@Extension
public class MyComparableExt {
    public static <T> boolean in( @This Comparable<T> thiz, T... values) {
      //noinspection SuspiciousMethodCalls
      return Arrays.asList(values).contains(thiz);
    }

    public static <T> boolean between( @This Comparable<T> thiz, T from, T to) {
      return thiz.compareTo(from) >= 0 && thiz.compareTo(to) <= 0;
    }
}