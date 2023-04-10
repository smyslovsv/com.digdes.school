package com.digdes.school;

public class ComparisonOperations {
    public static boolean moreOrEquals(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            if (item.getClass().equals(Long.class)) {
                return (Long) item >= (Long) condition;
            } else {
                return (Double) item >= (Double) condition;
            }
        }
    }
    public static boolean more(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            if (item.getClass().equals(Long.class)) {
                return (Long) item > (Long) condition;
            } else {
                return (Double) item > (Double) condition;
            }
        }
    }
    public static boolean equal(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            if (item.getClass() == condition.getClass()) {
                return item.equals(condition);
            } else {
                return false;
            }
        }
    }
    public static boolean lessOrEquals(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            if (item.getClass().equals(Long.class)) {
                return (Long) item <= (Long) condition;
            } else {
                return (Double) item <= (Double) condition;
            }
        }
    }
    public static boolean notEquals(Object item, Object condition) {
        if (item == null) {
            return true;
        } else {
            if (item.getClass() == condition.getClass()) {
                return !item.equals(condition);
            } else {
                return true;
            }
        }
    }
    public static boolean less(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            if (item.getClass().equals(Long.class)) {
                return (Long) item < (Long) condition;
            } else {
                return (Double) item < (Double) condition;
            }
        }
    }
    public static boolean like(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            String refactor = condition.toString().toLowerCase().replaceAll("%", ".*");
            return item.toString().toLowerCase().matches(refactor);
        }
    }

    public static boolean iLike(Object item, Object condition) {
        if (item == null) {
            return false;
        } else {
            return item.toString().matches(condition.toString().replaceAll("%", ".*"));
        }
    }
}
