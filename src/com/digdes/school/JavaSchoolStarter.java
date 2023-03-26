package com.digdes.school;

import java.util.*;

public class JavaSchoolStarter {
    List<Map<String,Object>> dataBase = new LinkedList<>();
    //Дефолтный конструктор
    public JavaSchoolStarter(){

    }
    public JavaSchoolStarter(List<Map<String,Object>> initList){
        dataBase.addAll(initList);
    }

    //На вход запрос, на выход результат выполнения запроса
    public List<Map<String,Object>> execute(String request) throws Exception {
        //Проверяем входную строку на наличие одной из разрешённых команд и их параметров
        //TODO: обработать исключения

        String[] firstCommandCode = request.trim().split(" ", 2);

        if (!firstCommandCode[0].isEmpty()) {
            switch (getCommandCode(firstCommandCode[0])) {
                case 1 -> {// INSERT
                    System.out.println("Вставка нового элемента");
                    if (!firstCommandCode[1].isEmpty()) {
                        return insertItem(firstCommandCode[1]);
                    } else {
                        // ERROR
                        System.out.println("Неправильный формат параметров команды INPUT : пустой список");
                    }
                }
                case 2 -> {// UPDATE
                    System.out.println("Обновление значений элемента(ов)");
                    if (firstCommandCode.length > 1) {
                        return updateItem(firstCommandCode[1]);
                    } else {
                        // ERROR
                        System.out.println("Неправильный формат параметров команды UPDATE : пустой список");
                    }
                }
                case 3 -> {
                    // SELECT
                    System.out.println("Выборка значений");
                    if (firstCommandCode.length > 1) {
                        // Не пустой список параметров, обрабатываем столбцы и параметр WHERE
                        return selectItems(firstCommandCode[1]);
                    } else {
                        // Пустой список параметров, возвращается весь список
                        return dataBase;
                    }
                }
                case 4 -> {// DELETE
                    System.out.println("Удаление элементов");
                }
                default ->
                        System.out.println("Неправильный формат команд");
            }
        } else {
            // ERROR
            System.out.println("Пустой запрос");
        }

        return new ArrayList<>();
    }

    private List<Map<String, Object>> selectItems(String s) {

        return new ArrayList<>();
    }

    private List<Map<String, Object>> updateItem(String s) {
        String[] comCode = s.split(" ", 2);
        if (comCode[0].equalsIgnoreCase("values")) {
            if (comCode.length > 1) {

                Map<String,Object> listOfParam;

                if (comCode[1].toLowerCase().contains(" where ")) {
                    // Есть условия, обрабатываем записи.
                    // Делим строку на значения и условия.
                    int idx1 = comCode[1].toLowerCase().indexOf(" where ");
                    listOfParam = getListArgs(comCode[1].substring(0, idx1)); // получили параметры
                    List<Map<String, Object>> listOfConditions = getListOfConditions(comCode[1].substring(idx1 + 7)); //Получили условия

                    System.out.println("\nОбновлённая БД: " + dataBase);
                } else {
                    //нет условий. обновляем весь список
                    listOfParam = getListArgs(comCode[1]); // получили параметры

                    for (Map<String, Object> item : dataBase) {
                        Set<String> keys = listOfParam.keySet();
                        for (String key : keys) {
                            if (item.containsKey(key)) {
                                item.put(key, listOfParam.get(key));
                            } else {
                                item.put(key, listOfParam.get(key));
                            }
                        }
                    }
                    System.out.println("\nОбновлённая БД: " + dataBase);
                    return dataBase;
                }

                //System.out.println("\n" + dataBase);
                return List.of(listOfParam);
            } else {
                // ERROR
                System.out.println("Неправильный формат параметров команды UPDATE : пустой список аргументов");
            }
        } else {
            // ERROR
            System.out.println("Неправильный формат параметров команды UPDATE : отсутствует VALUES");
        }
        return new ArrayList<>();
    }

    private List<Map<String, Object>> getListOfConditions(String listConditions) {
        List<String> list = Arrays.stream(listConditions.split(",")).toList();
        List<Map<String, Object>> listOfConditions = new ArrayList<>();
        /*
            Разрешённые операции:  OR, AND, >=, <=, !=, =, <, >
         */
        for (String s : list) {
            if (s.toLowerCase().contains(" or ")) {
                // OR: operationCode == 1
                int idx1 = s.toLowerCase().indexOf(" or ");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 4));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 1));
                continue;
            } else if (s.toLowerCase().contains(" and ")) {
                // AND: operationCode == 2
                int idx1 = s.toLowerCase().indexOf(" and ");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 5));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 2));
                continue;
            } else if (s.toLowerCase().contains(">=")) {
                // >=: operationCode == 3
                int idx1 = s.toLowerCase().indexOf(">=");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 2));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 3));
                continue;
            } else if (s.toLowerCase().contains("<=")) {
                // <=: operationCode == 4
                int idx1 = s.toLowerCase().indexOf(">=");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 2));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 4));
                continue;
            } else if (s.toLowerCase().contains("!=")) {
                // !=: operationCode == 5
                int idx1 = s.toLowerCase().indexOf("!=");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 2));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 5));
                continue;
            } else if (s.toLowerCase().contains("=")) {
                // =: operationCode == 6
                int idx1 = s.toLowerCase().indexOf("=");
                //сохранение параметров логической операции
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 1));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 6));
                continue;
            } else if (s.toLowerCase().contains(">")) {
                // >: operationCode == 7
                int idx1 = s.toLowerCase().indexOf(">");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 1));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 7));
                continue;
            } else if (s.toLowerCase().contains("<")) {
                // <: operationCode == 8
                int idx1 = s.toLowerCase().indexOf("<");
                Object keyValue = clearString(s.substring(0, idx1));
                Object valueValue = clearString(s.substring(idx1 + 1));
                listOfConditions.add(Map.of("key", keyValue,"value", valueValue,"operationCode", 8));
            } else {
                return null;
            }
        }
        System.out.println("  условия : " + listOfConditions);

        return listOfConditions;
    }

    private List<Map<String, Object>> insertItem(String newItem) {
        String[] comCode = newItem.split(" ", 2);
        if (comCode[0].equalsIgnoreCase("values")) {
            if (comCode.length > 1) {
                Map<String,Object> listOfParam = getListArgs(comCode[1]);
                dataBase.add(listOfParam);

                System.out.println("\n" + dataBase);
                return List.of(listOfParam);
            } else {
                // ERROR
                System.out.println("Неправильный формат параметров команды INSERT : пустой список аргументов");
            }

        } else {
            // ERROR
            System.out.println("Неправильный формат параметров команды INSERT : отсутствует VALUES");
        }
        return new ArrayList<>();
    }
    private Map<String, Object> getListArgs(String args) {
        List<String> list = Arrays.stream(args.split(",")).toList();
        Map<String, Object> listString = new HashMap<>();

        for (String str : list) {
            String[] tmp = str.split("=");
            //System.out.println("параметры : !" + tmp[0] + "! = !" + tmp[1] + "!");
            // Нужно проверить соответствие ключа типу значения объекта.
            // Для объектов типа строка - убрать кавычки, для остальных стираем пробелы
            listString.put(clearString(tmp[0]), clearString(tmp[1]));
        }
        System.out.println("  параметры : " + listString);

        return listString;
    }

    private String clearString(String str) {
        if (str.contains("'")) {
            int p1 = str.indexOf("'") + 1;
            int p2 = str.indexOf("'", p1);
            return str.substring(p1, p2);
        } else {
            return str.replaceAll(" ", "");
        }
    }

    private int getCommandCode (String command) {
        if (command.equalsIgnoreCase("INSERT")) {
            return 1;
        }
        if (command.equalsIgnoreCase("UPDATE")) {
            return 2;
        }
        if (command.equalsIgnoreCase("SELECT")) {
            return 3;
        }
        if (command.equalsIgnoreCase("DELETE")) {
            return 4;
        }
        if (command.equalsIgnoreCase("VALUES")) {
            return 5;
        }
        if (command.equalsIgnoreCase("WHERE")) {
            return 6;
        }
        return 0;
    }
}
