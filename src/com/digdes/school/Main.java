package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter(List.of(
                Map.of("lastName", "Федоров", "id", 3, "age", 40, "active", true),
                Map.of("lastName", "Петров", "id", 2, "age", 34, "active", true, "cost", 10.1)));
        try {
            //Вставка строки в коллекцию
            //List<Map<String,Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=true");
            //Вставка строки в коллекцию
            //List<Map<String,Object>> result0 = starter.execute("INSERT VALUES 'lastName' = 'Петров' , 'id'=2, 'age'=34, 'active'=true, 'cost'=10.1");
            //Изменение значения которое выше записывали
            List<Map<String,Object>> result2 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=77.7 where 'age' > 35 OR 'age' <=40");
            //Получение всех данных из коллекции (т.е. в данном примере вернется 1 запись)
            List<Map<String,Object>> result3 = starter.execute("SELECT");

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}