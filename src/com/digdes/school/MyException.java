package com.digdes.school;

class MyException extends Exception {
    public MyException(String ex) {
        System.out.println(ex);
    }
    public MyException() {
    }
}
