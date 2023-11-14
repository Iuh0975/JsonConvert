package com.example.demo;


import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.*;

public class JsonCustom {


    public static void main(String[] args)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        Character uniChar = '\u039A';
        List<String> listsName = new ArrayList<String>();
        listsName.add("John");
        listsName.add("Wick");
        listsName.add("Lada");

        List<Hobbie> hobbieList = new ArrayList<>();
        hobbieList.add(new Hobbie("Chocolate", "US", 10));
        hobbieList.add(new Hobbie("Socola", "UK", 20));

        Children user = new Children(1, "Thanh", "25", "Dien Bien Phu", true, uniChar, listsName, hobbieList);
        Children user01 = new Children(1, "Thanh", "25", "Dien Bien Phu", true, uniChar, hobbieList);
        Gson gson = new Gson();
        // Call Gson libary to parse object format
        System.out.println("Gson Parser: " + gson.toJson(user));

        // Call custom libary to parse object format
        System.out.println("Custom Parser: " + parseToJson(user));


    }

    // Function excuted format Json
    private static Object parseToJson(Object object)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        return iterateMap(object);
    }

    private static Object builderMap(Map<String, Object> linkedHashMap) {
        Set<String> keys = linkedHashMap.keySet();
        StringBuilder stringBuilder = new StringBuilder("{");
        int count = 1;
        for (String key : keys) {
            if (count == keys.size()) {
                stringBuilder.append("\"" + key + "\"" + ":" + linkedHashMap.get(key) + "}");
            } else {
                stringBuilder.append("\"" + key + "\"" + ":" + linkedHashMap.get(key) + ",");
                count++;
            }
        }
        return stringBuilder;
    }

    // Function used for iterating object
    private static Object iterateMap(Object object)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Init linkedhashmap
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
        // Returns an array of Field objects reflecting all the fields declared by the class
        Field[] fields = object.getClass().getDeclaredFields();
        // Init StringBuilder s
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            // Get index fields based on i
            String key = fields[i].getName();
            //Returns a Field object that reflects the specified declared field of the class
            Field privateField = object.getClass().getDeclaredField(key);
            // Provide accessible for private field
            privateField.setAccessible(Boolean.TRUE);
           //Returns the value of the field represented by this Field, on the specified object
            Object value = (Object) privateField.get(object);
            // Init valueFilter variable
            Object valueFilter = "";

            // Checking Type of specific field is not char type and is primitive type
            if ((object.getClass().getDeclaredField(key).getType() != char.class)
                    && (object.getClass().getDeclaredField(key).getType().isPrimitive())) {
                // Call filterType function with 3 parameter: TYPE_NUMBER, value need to passed, condition
                valueFilter = filterType(AppConstants.TYPE_NUMBER, value, "");
            }
            // Checking type of specific field is List type
            else if (object.getClass().getDeclaredField(key).getType().getSimpleName().equals("List")) {
                // Get Generic Type value of specific filed. Ex: java.util.List<java.lang.String> or java.util.List<java.lang.Children>
                String tmp = String.valueOf(object.getClass().getDeclaredField(key).getGenericType());
                // Call filterType function with 3 parameter: TYPE_NUMBER, value need to passed, condition with generic type
                valueFilter = filterType(AppConstants.TYPE_ARRAY, value, tmp);
            }
            // Default String case
            else {
                // Call filterType function with 3 parameter: TYPE_NUMBER, value need to passed, condition
                valueFilter = filterType(AppConstants.TYPE_STRING, value, "");
            }

            // Put key field as key and valueFilter as value into linkedHashMap
            linkedHashMap.put(key, valueFilter);
            // Implement logic to remove the unnecessary value in StringBuilder and proceed to append
            s.delete(0, s.length());
            s.append(builderMap(linkedHashMap));
        }
        return s;
    }


    // Method  used for iterating List String
    private static Object interateArray(Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = "";
        StringBuilder striBuilder = new StringBuilder();
        // Convert Object to String
        String tmp = value.toString();
        // Sub String first index and last index to get value
        String tmpCutter = tmp.substring(1, tmp.length() - 1);
        // Split by quote
        String[] strings = tmpCutter.split(",");
        Object tmpResult = "";
        // Init flag with count = 1
        int count = 1;
        for (int i = 0; i < strings.length; i++) {
            if (count == strings.length) {
                tmpResult = striBuilder.append('"' + strings[i].trim() + '"');
            } else {
                tmpResult = striBuilder.append('"' + strings[i].trim() + '"' + ",");
                count++;
            }

        }
        result = "[" + tmpResult + "]";
        return result;

    }

    // Need to be maintained. All of the codes bellow are not correct
    public static void iterateListObject(Object object) throws NoSuchFieldException, IllegalAccessException {
        System.out.println(object);
        String listObject = object.toString();
        String[] s = listObject.split("[\\\\(||\\\\)]");
        List tmpList = new ArrayList();

        for (int i = 0; i < s.length; i++) {
            if (i % 2 == 0) {
                System.out.println("Skip Old Number");
            } else {
                tmpList.add(s[i]);
            }
        }
        System.out.println(tmpList);

        StringBuilder stringBuilder = new StringBuilder("[");

        for (int k = 0; k < tmpList.size(); k++) {
            System.out.println(tmpList.get(k));
            stringBuilder.append(tmpList.get(k));
        }

        stringBuilder.append("]");
    }

    private static Object filterType(String type, Object value, String condition)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = "";
        StringBuilder striBuilder = new StringBuilder();
        // Switch case with 3 condition: String, Number, Array
        switch (type) {
            case "String":
                // With String case, just append value
                result = striBuilder.append("\"" + value + "\"");
                break;
            case "Number":
                // With Number case, assign directly to result value
                result = value;
                break;
            case "Array":
                // Checking condition with List String
                if (condition.equals("java.util.List<java.lang.String>")) {
                    result = interateArray(value);
                }
                // Default Condition with List Object
                else {
                    iterateListObject(value);
                }

                break;
        }
        return result;
    }


}

