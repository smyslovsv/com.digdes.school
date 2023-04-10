package com.digdes.school;

import java.util.*;

import static com.digdes.school.ComparisonOperations.*;

public class JavaSchoolStarter {
    // Список имеющихся записей ака БД
    private List<Map<String, Object>> dataBase = new ArrayList<>();
    // список условий для выборки, удаления или обновления
    private List<Map<String, Object>> listOfConditions = new ArrayList<>();
    // карта параметров со значениями для вставки и обновления
    private Map<String, Object> mapOfParam = new HashMap<>();

    //Дефолтный конструктор
    public JavaSchoolStarter() {

    }

    public JavaSchoolStarter(List<Map<String, Object>> initList) {
        dataBase.addAll(initList);
    }

    //На вход запрос, на выход результат выполнения запроса
    public List<Map<String, Object>> execute(String request) throws Exception {
        //очистка списка условий и карты параметров
        listOfConditions.clear();
        mapOfParam.clear();
        try {
            //Проверяем входную строку на наличие одной из разрешённых команд
            String[] firstCommandCode = request.trim().split(" ", 2);

            if (!firstCommandCode[0].isEmpty()) {
                switch (InstructionSet.valueOf(firstCommandCode[0].toUpperCase())) {
                    case INSERT -> {
                        System.out.println("Вставка нового элемента: " + request);
                        return insertItem(request);
                    }
                    case UPDATE -> {
                        System.out.println("Обновление значений элемента(ов): " + request);
                        return updateItems(request);
                    }
                    case SELECT -> {
                        System.out.println("Выборка значений: " + request);
                        return selectItems(request);
                    }
                    case DELETE -> {
                        System.out.println("Удаление элементов: " + request);
                        return deleteItems(request);
                    }
                    default -> throw new MyException("Ошибка. Не известная команда в запросе: " + request);
                }
            } else {
                // ERROR
                throw new MyException("Ошибка. Пустой запрос");
            }
        } catch (Exception ex) {
            System.out.println("Операция прервана");
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> insertItem(String request) throws MyException {
        String[] nextCommand = request.trim().split(" ", 2);

        if (nextCommand.length > 1) {
            String[] comCode = nextCommand[1].split(" ", 2);
            //Проверяем наличие обязательного слова VALUES
            if (InstructionSet.valueOf(comCode[0].toUpperCase()) == InstructionSet.VALUES) {
                //проверяем наличие аргументов для вставки
                if (comCode.length > 1) {
                    //получаем список аргументов в переменную mapOfParam
                    try {
                        getListArgs(comCode[1]);
                    } catch (MyException ex) {
                        throw new MyException();
                    }

                    //проверка на вставку значений NULL
                    boolean existNullValues = mapOfParam.values()
                            .stream()
                            .anyMatch(Objects::isNull);

                    if (existNullValues) {
                        throw new MyException("Попытка вставки значений NULL");
                    } else {
                        //вставляем значения в базу, дополняя недостающими ключами со значением NULL
                        List<Map<String, Object>> listForReturn = List.of(new HashMap<>(mapOfParam));
                        for (DatabaseFields key : DatabaseFields.values()) {
                            if (!mapOfParam.containsKey(key.getFieldName())) {
                                mapOfParam.put(key.getFieldName(), null);
                            }
                        }
                        dataBase.add(new HashMap<>(mapOfParam));
                        return listForReturn;
                    }
                } else {
                    // ERROR
                    throw new MyException("Неправильный формат параметров команды INSERT : пустой список аргументов");
                }
            } else {
                // ERROR
                throw new MyException("Неправильный формат параметров команды INSERT : отсутствует обязательный оператор VALUES");
            }
        } else {
            // ERROR
            throw new MyException("Ошибка. Неправильный формат запроса INSERT.");
        }
    }

    private List<Map<String, Object>> selectItems(String request) throws MyException {
        String[] firstCommandCode = request.trim().split(" ", 2);
        if (firstCommandCode.length > 1) {
            // обрабатываем столбцы и параметр WHERE
            String[] comCode = firstCommandCode[1].trim().split(" ", 2);
            try {
                if (InstructionSet.valueOf(comCode[0].toUpperCase()) == InstructionSet.WHERE) {
                    if (comCode.length > 1) {
                        try {
                            getListOfConditions(comCode[1]); // получаем условия
                        } catch (MyException ex) {
                            throw new MyException();
                        }

                        List<Map<String, Object>> selectedItems = new ArrayList<>();
                        for (Map<String, Object> curItem : dataBase) {
                            if (checkConditions(curItem)) {
                                selectedItems.add(clearNullValues(curItem));
                            }
                        }
                        return selectedItems;
                    } else {
                        throw new MyException("Ошибка. Неправильный формат запроса, отсутствуют условия оператора WHERE.");
                    }
                }
            } catch (IllegalArgumentException ex) {
                throw new MyException("Ошибка. Неправильный формат запроса, не известный оператор \"" + comCode[0] + "\" вместо WHERE.");
            }
        } else {
            List<Map<String, Object>> selectedItems = new ArrayList<>();
            for (Map<String, Object> curItem : dataBase) {
                selectedItems.add(clearNullValues(curItem));
            }
            return selectedItems;
        }
        return new ArrayList<>();
    }

    private List<Map<String, Object>> updateItems(String request) throws MyException {
        String[] nextCommand = request.trim().split(" ", 2);
        List<Map<String, Object>> listOfUpdatedItems = new ArrayList<>();
        if (nextCommand.length > 1) {

            String[] comCode = nextCommand[1].trim().split(" ", 2);
            if (InstructionSet.valueOf(comCode[0].toUpperCase()) == InstructionSet.VALUES) {
                if (comCode.length > 1) {
                    if (comCode[1].toUpperCase().contains(InstructionSet.WHERE.toString())) {
                        // Есть условия, обрабатываем записи.
                        // Делим строку на значения и условия.

                        int idx1 = comCode[1].toUpperCase().indexOf(String.valueOf(InstructionSet.WHERE));
                        int idx2 = comCode[1].toUpperCase().lastIndexOf(String.valueOf(InstructionSet.WHERE));
                        if (idx1 == idx2) {
                            try {
                                getListArgs(comCode[1].substring(0, idx1).trim()); // получили параметры
                                getListOfConditions(comCode[1].substring(idx1 + 5).trim()); //Получили условия
                            } catch (MyException ex) {
                                throw new MyException();
                            }
                        } else {
                            throw new MyException(" Не могу разделить строку на параметры и условия");
                        }
                    } else {
                        try {
                            //нет условий. обновляем весь список
                            getListArgs(comCode[1]); // получили параметры
                        } catch (MyException ex) {
                            throw new MyException();
                        }
                    }
                    for (Map<String, Object> curItem : dataBase) {
                        if (checkConditions(curItem)) {
                            Map<String, Object> updatedItem = new HashMap<>(curItem);

                            for (String key : mapOfParam.keySet()) {
                                updatedItem.put(key, mapOfParam.get(key));
                            }

                            boolean allNullValues = updatedItem.values()
                                    .stream()
                                    .allMatch(Objects::isNull);

                            if (!allNullValues) {
                                listOfUpdatedItems.add(clearNullValues(updatedItem));
                                curItem.putAll(updatedItem);
                            } else {
                                throw new MyException(" После обновления все поля имеют значение NULL \n" +
                                        " Обновление записи \"" + curItem + " прервано");
                            }
                        }
                    }
                } else {
                    // ERROR
                    throw new MyException("Неправильный формат параметров команды UPDATE : пустой список параметров и условий");
                }
            } else {
                // ERROR
                throw new MyException("Неправильный формат параметров команды UPDATE : отсутствует обязательный оператор VALUES");
            }
        } else {
            // ERROR
            throw new MyException("Ошибка. Неправильный формат запроса UPDATE.");
        }
        return listOfUpdatedItems;
    }

    private List<Map<String, Object>> deleteItems(String request) throws MyException {
        String[] firstCommandCode = request.trim().split(" ", 2);
        List<Map<String, Object>> deletedItems = new ArrayList<>();

        if (firstCommandCode.length > 1) {
            String[] comCode = firstCommandCode[1].split(" ", 2); // строка не пустая, а с чем то
            try {
                if (InstructionSet.valueOf(comCode[0].toUpperCase()) == InstructionSet.WHERE) {
                    //проверяем наличие самих условий
                    if (comCode.length > 1) {
                        try {
                            getListOfConditions(comCode[1]);
                        } catch (MyException ex) {
                            throw new MyException();
                        }

                        for (Iterator<Map<String, Object>> iterator = dataBase.iterator(); iterator.hasNext(); ) {
                            Map<String, Object> curItem = iterator.next();
                            if (checkConditions(curItem)) {
                                deletedItems.add(clearNullValues(curItem));
                                iterator.remove();
                            }
                        }
                    } else {
                        // ERROR есть слово Where а самих условий нет
                        throw new MyException("Неправильный формат параметров команды DELETE : пустой список условий");
                    }
                }
            } catch (IllegalArgumentException ex) {
                throw new MyException("Неправильный формат параметров команды DELETE : не правильный формат команды");
            }
        } else {
            //Очищаем всю базу. Удаляем нулевые значения
            for (Map<String, Object> curItem : dataBase) {
                deletedItems.add(clearNullValues(curItem));
            }
            dataBase.clear();
        }
        return new ArrayList<>(deletedItems);
    }

    private Map<String, Object> clearNullValues(Map<String, Object> map) {
        Map<String, Object> clearedMap = new HashMap<>();
        for (DatabaseFields key : DatabaseFields.values()) {
            if (map.get(key.getFieldName()) != null) {
                clearedMap.put(key.getFieldName(), map.get(key.getFieldName()));
            }
        }
        return clearedMap;
    }

    private void getListOfConditions(String listConditions) throws MyException {
        List<String> list = Arrays.stream(listConditions.split(",")).toList();
        /*   Разрешённые операции:  OR, AND, >=, <=, !=, =, <, >, ilike, like    */
        for (String s : list) {
            int idx1 = 0;
            int idx2 = 0;
            int operationCode = 0;
            if (s.toLowerCase().contains(" and ")) {
                // AND: operationCode == 1
                idx1 = s.toLowerCase().indexOf(" and ");
                idx2 = idx1 + 5;
                getListOfConditions(s.substring(0, idx1));
                listOfConditions.add(Map.of("operationCode", 1));
                getListOfConditions(s.substring(idx2));
            } else if (s.toLowerCase().contains(" or ")) {
                // OR: operationCode == 2
                idx1 = s.toLowerCase().indexOf(" or ");
                idx2 = idx1 + 4;
                getListOfConditions(s.substring(0, idx1));
                listOfConditions.add(Map.of("operationCode", 2));
                getListOfConditions(s.substring(idx2));
            } else {

                if (s.toLowerCase().contains(">=")) {
                    // >=: operationCode == 3
                    idx1 = s.toLowerCase().indexOf(">=");
                    idx2 = idx1 + 2;
                    operationCode = 3;
                } else if (s.toLowerCase().contains("<=")) {
                    // <=: operationCode == 4
                    idx1 = s.toLowerCase().indexOf("<=");
                    idx2 = idx1 + 2;
                    operationCode = 4;
                } else if (s.toLowerCase().contains("!=")) {
                    // !=: operationCode == 5
                    idx1 = s.toLowerCase().indexOf("!=");
                    idx2 = idx1 + 2;
                    operationCode = 5;
                } else if (s.toLowerCase().contains("=")) {
                    // =: operationCode == 6
                    idx1 = s.toLowerCase().indexOf("=");
                    idx2 = idx1 + 1;
                    operationCode = 6;
                } else if (s.toLowerCase().contains(">")) {
                    // >: operationCode == 7
                    idx1 = s.toLowerCase().indexOf(">");
                    idx2 = idx1 + 1;
                    operationCode = 7;
                } else if (s.toLowerCase().contains("<")) {
                    // <: operationCode == 8
                    idx1 = s.toLowerCase().indexOf("<");
                    idx2 = idx1 + 1;
                    operationCode = 8;
                } else if (s.toLowerCase().contains(" ilike ")) {
                    // ilike: operationCode == 9
                    idx1 = s.toLowerCase().indexOf(" ilike ");
                    idx2 = idx1 + 7;
                    operationCode = 9;
                } else if (s.toLowerCase().contains(" like ")) {
                    // like: operationCode == 10
                    idx1 = s.toLowerCase().indexOf(" like ");
                    idx2 = idx1 + 6;
                    operationCode = 10;
                }
                String nameKey = clearString(s.substring(0, idx1));
                String value = clearString(s.substring(idx2));

                if (!value.equalsIgnoreCase("null")) {
                    DatabaseFields fieldName;
                    try {
                        fieldName = DatabaseFields.valueOf(nameKey.toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        throw new MyException("Неправильное или не предусмотренное имя ключа");
                    }
                    try {
                        Map<String, Object> convertedValue = typeConversion(fieldName, value);
                        listOfConditions.add(Map.of("key", fieldName.getFieldName(),
                                "value", convertedValue.get(fieldName.getFieldName()),
                                "operationCode", operationCode));
                    } catch (NumberFormatException | MyException ex) {
                        throw new MyException();
                    }
                } else {
                    throw new MyException("Значение NULL в списке условий");
                }
            }
        }
    }

    private void getListArgs(String args) throws MyException {
        if (!args.isEmpty()) {
            List<String> list = Arrays.stream(args.split(",")).toList();

            for (String str : list) {
                String[] tmp = str.split("=");
                String nameKey = clearString(tmp[0]);
                String value = clearString(tmp[1]);
                DatabaseFields fieldName;
                try {
                    fieldName = DatabaseFields.valueOf(nameKey.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new MyException("Неправильное или не предусмотренное имя ключа" + nameKey);
                }
                try {
                    Map<String, Object> newParam = typeConversion(fieldName, value);
                    mapOfParam.put(fieldName.getFieldName(), newParam.get(fieldName.getFieldName()));
                } catch (NumberFormatException | MyException ex) {
                    throw new MyException();
                }
            }
        }
    }

    private boolean checkConditions(Map<String, Object> curItem) {
        boolean checkCond = true;
        boolean logicalOperationAnd = false;
        boolean logicalOperationOr = false;

        List<Map<String, Object>> listWithResult = new ArrayList<>();

        for (Map<String, Object> itemOfCondition : listOfConditions) {
            switch ((int) itemOfCondition.get("operationCode")) {
                case 1 -> {
                    // AND  -  OR
                    logicalOperationAnd = true;
                    listWithResult.add(Map.of("operationCode", itemOfCondition.get("operationCode")));
                }
                case 2 -> {
                    // OR
                    logicalOperationOr = true;
                    listWithResult.add(Map.of("operationCode", itemOfCondition.get("operationCode")));
                }
                case 3 -> {
                    //  >=
                    Object res = moreOrEquals(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 4 -> {
                    //  <
                    Object res = lessOrEquals(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 5 -> {
                    //  !=
                    Object res = notEquals(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 6 -> {
                    //  =
                    Object res = equal(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 7 -> {
                    //  >
                    Object res = more(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 8 -> {
                    //  >
                    Object res = less(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 9 -> {
                    //  ilike
                    Object res = iLike(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
                case 10 -> {
                    //  like
                    Object res = ComparisonOperations.like(curItem.get((String) itemOfCondition.get("key")), itemOfCondition.get("value"));
                    listWithResult.add(Map.of("result", res));
                }
            }
        }
        if (logicalOperationAnd) {
            listWithResult = logicalOperation(listWithResult, 1);
        } else if (logicalOperationOr) {
            listWithResult = logicalOperation(listWithResult, 2);
        }

        for (Map<String, Object> item : listWithResult) {
            checkCond = checkCond && (Boolean) item.get("result");
        }
        return checkCond;
    }

    private List<Map<String, Object>> logicalOperation(List<Map<String, Object>> listWithResult, int typeOperation) {
        List<Map<String, Object>> result = new ArrayList<>();
        int curLength = listWithResult.size();
        for (int i = 0; i < curLength; i++) {
            if (listWithResult.get(i).containsKey("operationCode")) {
                if (((int) listWithResult.get(i).get("operationCode")) == typeOperation) {
                    Map<String, Object> tmp;
                    if (((int) listWithResult.get(i).get("operationCode")) == 1) {
                        tmp = Map.of("result",
                                (Boolean) result.get(result.size() - 1).get("result")
                                        && (Boolean) listWithResult.get(i + 1).get("result"));
                    } else {
                        tmp = Map.of("result",
                                (Boolean) result.get(result.size() - 1).get("result")
                                        || (Boolean) listWithResult.get(i + 1).get("result"));
                    }
                    result.remove(result.size() - 1);
                    result.add(tmp);
                    i++;
                } else {
                    result.add(listWithResult.get(i));
                }
            } else {
                result.add(Map.of("result", listWithResult.get(i).get("result")));
            }
        }
        return result;
    }

    private Map<String, Object> typeConversion(DatabaseFields key, String value) throws MyException {
        /* id - Long, lastName - String, cost - Double, age - Long, active- Boolean  */
        Object converted = new Object();

        if (!value.equalsIgnoreCase("null")) {
            try {
                switch (key) {
                    case ID, AGE -> converted = Long.valueOf(value);
                    case LASTNAME -> converted = value;
                    case COST -> converted = Double.parseDouble(value);
                    case ACTIVE -> {
                        if (Objects.equals(value.toLowerCase(), new String("true"))) {
                            converted = Boolean.valueOf(value);
                        } else if (Objects.equals(value.toLowerCase(), new String("false"))) {
                            converted = Boolean.valueOf(value);
                        } else {
                            throw new MyException("Неправильное значение переменной типа Boolean");
                        }
                    }
                }
                return Map.of(key.getFieldName(), converted);
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка типа параметра " + key);
                throw new NumberFormatException();
            }
        } else {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(key.getFieldName(), null);
            return tmp;
        }
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

}
