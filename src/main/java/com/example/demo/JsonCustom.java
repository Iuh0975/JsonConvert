package com.example.demo;


import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.*;

public class JsonCustom {


    public static void main(String[] args)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        Character uniChar = '\u039A';
        List<Integer> listsName = new ArrayList<Integer>();
        listsName.add(1);
        listsName.add(2);
        listsName.add(3);
        

        List<Hobbie> hobbieList = new ArrayList<>();
        hobbieList.add(new Hobbie("Chocolate", "US", 10));
        hobbieList.add(new Hobbie("Socola", "UK", 20));

        Children user03 = new Children(1, "Thanh", "25", "Dien Bien Phu", true, uniChar, hobbieList, listsName);
        Gson gson = new Gson();
        // Call Gson libary to parse object format
        System.out.println("Gson Parser: " + gson.toJson(user03));

        // Call custom libary to parse object format
        System.out.println("Custom Parser: " + parseToJson(user03));


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
        // Init linkedHashmap
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


            // Call method to filter elements in an object
            try {
                valueFilter = filterTypeObject(value, privateField, valueFilter);
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
                String keyUn = key;
            }


            // Put key field as key and valueFilter as value into linkedHashMap
            linkedHashMap.put(key, valueFilter);
            // Implement logic to remove the unnecessary value in StringBuilder and proceed to append
            s.delete(0, s.length());
            s.append(builderMap(linkedHashMap));
        }
        return s;
    }

    private static Object filterTypeObject(Object value, Field privateField, Object valueFilter) throws NoSuchFieldException, IllegalAccessException {

        // Checking Type of specific field is not char type and is primitive type
        if (value == null) {
            System.out.println("Null pointer exception");
        } else if ((privateField.getType() != char.class)
                && (privateField.getType().isPrimitive() || (privateField.getType().equals(Boolean.class)))) {
            // Call filterType function with 3 parameter: TYPE_NUMBER, value need to passed, condition
            valueFilter = filterType(AppConstants.TYPE_NUMBER, value, "");
        }
        // Checking type of specific field is List type
        else if (privateField.getType().getSimpleName().equals("List")) {
            // Get Generic Type value of specific filed. Ex: java.util.List<java.lang.String> or java.util.List<java.lang.Children>
            String tmp = String.valueOf(privateField.getGenericType());
            // Call filterType function with 3 parameter: TYPE_NUMBER, value need to passed, condition with generic type
            valueFilter = filterType(AppConstants.TYPE_ARRAY, value, tmp);
        }
        // Default String case
        else {
            // Call filterType function with 3 parameter: TYPE_NUMBER, value need to passed, condition
            valueFilter = filterType(AppConstants.TYPE_STRING, value, "");
        }

        return valueFilter;

    }

    private static Object filterType(String type, Object value, String condition)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = "";
        StringBuilder striBuilder = new StringBuilder();
        // Switch case with 3 condition: String, Number, Array
        switch (type) {
            case "String":
                // With String case, just append value
                processStringType(value, striBuilder);
                result = striBuilder;
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
                    result = iterateListObject(value);
                }

                break;
        }
        return result;
    }


    private static void processStringType(Object value, StringBuilder striBuilder) {
        striBuilder.append("\"" + value + "\"");
    }

    // Method  used for iterating List String.
    private static Object interateArray(Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        List<String> stringList = (ArrayList<String>) value;
        StringBuilder striBuilder = new StringBuilder();
        striBuilder.append("[");
        for (String i : stringList) {
            processStringType(i, striBuilder);
            striBuilder.append(",");
        }
        striBuilder.deleteCharAt(striBuilder.length() - 1);
        striBuilder.append("]");
        return striBuilder;

    }

    // Need to be maintained. All of the codes bellow are not correct
    public static Object iterateListObject(Object object) throws NoSuchFieldException, IllegalAccessException {
        System.out.println(object);
        List<Object> objectList = (ArrayList<Object>) object;
        System.out.println(object);
        StringBuilder s = new StringBuilder();
        s.append("[");
        for (Object obj : objectList) {
            if (obj.getClass().getSimpleName().equals("Integer")) {
                s.append(obj);
            } else {
                s.append(iterateMap(obj));
            }
            s.append(",");
        }
        s.deleteCharAt(s.length() - 1);
        s.append("]");
        return s;
    }


}

