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
        System.out.println("Gson Parser: " + gson.toJson(user));
        System.out.println("Custom Parser: " + parseToJson(user));


    }

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

    private static Object iterateMap(Object object)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            String key = fields[i].getName();
            Field privateField = object.getClass().getDeclaredField(key);
            privateField.setAccessible(Boolean.TRUE);
            Object value = (Object) privateField.get(object);
            Object valueFilter = "";

            if ((object.getClass().getDeclaredField(key).getType() != char.class)
                    && (object.getClass().getDeclaredField(key).getType().isPrimitive())) {
                valueFilter = filterType(AppConstants.TYPE_NUMBER, value, "");
            } else if (object.getClass().getDeclaredField(key).getType().getSimpleName().equals("List")) {
                String tmp = String.valueOf(object.getClass().getDeclaredField(key).getGenericType());
                valueFilter = filterType(AppConstants.TYPE_ARRAY, value, tmp);
            } else {
                valueFilter = filterType(AppConstants.TYPE_STRING, value, "");
            }

            linkedHashMap.put(key, valueFilter);
            s.delete(0, s.length());
            s.append(builderMap(linkedHashMap));
        }
        return s;
    }


    private static Object interateArray(Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = "";
        StringBuilder striBuilder = new StringBuilder();
        String tmp = value.toString();
        String tmpCutter = tmp.substring(1, tmp.length() - 1);
        String[] strings = tmpCutter.split(",");
        Object tmpResult = "";
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
        switch (type) {
            case "String":
                result = striBuilder.append("\"" + value + "\"");
                break;
            case "Number":
                result = value;
                break;
            case "Array":
                if (condition.equals("java.util.List<java.lang.String>")) {
                    result = interateArray(value);
                } else {
                    iterateListObject(value);
                }

                break;
        }
        return result;
    }


}

