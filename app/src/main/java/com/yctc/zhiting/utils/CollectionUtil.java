package com.yctc.zhiting.utils;

import java.util.Collection;

public class CollectionUtil {

    public static boolean isEmpty(Collection collection) {
        if (collection == null || collection.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }
}
