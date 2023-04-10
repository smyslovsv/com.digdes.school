package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        JavaSchoolStarter starter = new JavaSchoolStarter();

        try {
            List<Map<String,Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Федоров', 'id'=8, 'age'=40, 'active'=true");
            result1 = starter.execute("INSERT VALUES 'lastName' = 'Петров', 'id'=3, 'age'=34, 'active'=true, 'cost'=10");
//            result1 = starter.execute("INSERT VALUES 'lastName' = 'Ivanov', 'id'=7, 'active'=true, 'cost'=8.9");
//            result1 = starter.execute("INSERT VALUES 'lastName' = 'Сидоров', 'id'=4, 'age'=12, 'cost'=2");
//            result1 = starter.execute("INSERT VALUES 'lastName' = 'Смыслов', 'id'=5, 'age'=24, 'active'=false");
            System.out.println("");

//            result1 = starter.execute("Update VALUES 'lastName' = null where 'id' = 's' OR 'age'>25");
//            result1.forEach(System.out::println);

            List<Map<String,Object>> result3 = starter.execute("Select where 'lastName' like '%оров'");
            result3.forEach(System.out::println);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}