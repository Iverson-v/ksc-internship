package com.ksyun.train.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

public class ParamParseUtil {
    //T就是Pod。
    //泛型方法
    public static <T> T parse(Class<T> clz, String queryString) throws Exception {
        // t is one instance of T
        //第一步：通过反射创建对象
        Constructor<T> constructor = clz.getDeclaredConstructor();
        //防止private权限不够
        constructor.setAccessible(true);
        T t = constructor.newInstance();


        //第二步： queryString按照&分片并排序
        String[] querys=getQuerys(queryString);
//        for (String query:querys) {
//            System.out.println(query);
//        }

        //第三步： 创建辅助Map对象，嵌套赋值
        Map<String, Object> paramMap = parseAuxiliaryMap(querys);
        //System.out.println("-----------------------------------------------");

        //第四步：  根据Map中的值给对象t每个属性赋值。
        setValueForTarget(t, paramMap);
        return t;
    }
    private static String[] getQuerys(String queryString) {
        String[] querys = queryString.split("&");
        //排序没有用，这样序号超过两位数就没用了。。。
        Arrays.sort(querys);
        return querys;
    }
    public static String firstAlpLowerCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }
    public static String firstAlpUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
    public static<E> Object typeConversion(E e,String value,Field field){
        //去除字符串前后空格
        if(value!=null){
            value = value.trim();
        }

        //获取属性名
        String fieldname = field.getName();
        //获取类型名
        String typeName = field.getType().getName();


        if(e==Byte.class||e==byte.class){
            if(value==null){
                return null;
            }
            try {
                return Byte.parseByte(value);


            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }


        }else if(e==Short.class||e==short.class){
            if(value==null){
                return null;
            }
            try {
                Short valueAfterTypeConversion = Short.parseShort(value);

            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }

        }else if(e==Integer.class||e==int.class){
            if(value==null){
                return null;
            }
            try {
                return Integer.parseInt(value);

            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }
        }else if(e==Float.class||e==float.class){
            if(value==null){
                return null;
            }
            try {
                return Float.parseFloat(value);

            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }
        }else if(e==Double.class||e==double.class){
            if(value==null){
                return null;
            }
            try {
                return Double.parseDouble(value);

            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }
        }else if(e==Long.class||e==long.class){
            if(value==null){
                return null;
            }
            try {
                return Long.parseLong(value);

            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }
        }else if(e==Boolean.class||e==boolean.class){
            if(value==null||"null".equals(value)|| "Null".equals(value)||"NULL".equals(value)){
                return false;
            }
            else return true;

        }else if(e==Character.class||e==char.class){
            if(value==null){
                return null;
            }
            if(value.length()==1){
                return value.charAt(0);
            }
            System.err.println("超出字符长度！ "+fieldname+"的泛型类型为"+e+",不能添加其他类型！");;
        }else if(e==String.class){
            return value;


        }else if(e== BigDecimal.class){
            //如果为空就不赋值了
            if(value==null){
                return null;
            }
            try{
                return new BigDecimal(value);

            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",其中泛型规定为:"+e+"，不能将其他类型添加到"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            }
        }

        return null;

    }
    public static <E,T> void assignValueForBasicDataType (E e,String value,T t,Field field) {
        //去除字符串前后空格
        if(value!=null){
            value = value.trim();
        }

        //获取属性名
        String fieldname = field.getName();
        //获取类型名
        String typeName = field.getType().getName();


        if(e==Byte.class||e==byte.class){
            if(value==null){
                return;
            }
            try {
                byte valueAfterTypeConversion = Byte.parseByte(value);
                field.set(t,valueAfterTypeConversion);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }


        }else if(e==Short.class||e==short.class){
            if(value==null){
                return;
            }
            try {
                Short valueAfterTypeConversion = Short.parseShort(value);
                field.set(t,valueAfterTypeConversion);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }

        }else if(e==Integer.class||e==int.class){
            if(value==null){
                return;
            }
            try {
                Integer valueAfterTypeConversion = Integer.parseInt(value);
                field.set(t,valueAfterTypeConversion);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }else if(e==Float.class||e==float.class){
            if(value==null){
                return;
            }
            try {
                Float valueAfterTypeConversion = Float.parseFloat(value);
                field.set(t,valueAfterTypeConversion);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }else if(e==Double.class||e==double.class){
            if(value==null){
                return;
            }
            try {
                Double valueAfterTypeConversion = Double.parseDouble(value);
                field.set(t,valueAfterTypeConversion);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }else if(e==Long.class||e==long.class){
            if(value==null){
                return;
            }
            try {
                Long valueAfterTypeConversion = Long.parseLong(value);
                field.set(t,valueAfterTypeConversion);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }else if(e==Boolean.class||e==boolean.class){
            if(value==null||"null".equals(value)|| "Null".equals(value)||"NULL".equals(value)){
                try{
                    field.set(t,false);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }

            }

        }else if(e==Character.class||e==char.class){
            if(value==null){
                return;
            }
            if(value.length()==1){
                try {
                    field.set(t,value.charAt(0));
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }else if(e==String.class){
            if(value==null){
                return;
            }
            try {
                field.set(t,value);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }else if(e== BigDecimal.class){
            //如果为空就不赋值了
            if(value==null){
                return;
            }
            try{
                BigDecimal bigDecimal = new BigDecimal(value);
                field.set(t,bigDecimal);
            }catch (NumberFormatException exception){
                System.err.println("类型转换异常：  属性"+fieldname+"的类型为："+typeName+",不能将其他类型的值传给"+typeName+"!   请检查你的queryString中key为"+firstAlpUpperCase(fieldname)+"的值是否正确。");
                exception.printStackTrace();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private static <T> void assignForList(Map<String, Object> paramMap,Field field,String fieldName,T t) throws Exception {
        // 列表类型处理，最后要把这个list赋值给pod
        List<Object> list = new ArrayList<>();
        Map<String, Object> paramValue = (Map<String, Object>) paramMap.get(fieldName);
        ParameterizedType paramType = (ParameterizedType) field.getGenericType();

        //这个elementType是List<>泛型里面的类型。只考虑泛型只有一种的情况。不考虑嵌套情况。
        // 后面通过要把String类型的value转换成elementType类型然后再添加到List中，
        Class<?> elementType = (Class<?>) paramType.getActualTypeArguments()[0];

        //遍历每层map,套娃
        for (Map.Entry<String, Object> entry : paramValue.entrySet()) {
            //如果entry的值还是属于map就要取下一层
            if (entry.getValue() instanceof Map) {
                //获取map为container的下一个值envirment
                Map<String, Object> value = (Map<String, Object>) entry.getValue();

                //再递归给map里面的每一层进行传参
                Object fieldObj = elementType.getDeclaredConstructor().newInstance();
                setValueForTarget(fieldObj, value);
                list.add(fieldObj);
            } else {
                //这里entry不是map，要给基本数据类型赋值，
                Object value = entry.getValue();
                //Object转化为具体的泛型，通过elementType进行判断
                value=typeConversion(elementType,(String) value,field);
                //
                list.add(value);
            }
        }
        //最后把创建好的list赋值给pod。
        field.set(t, list);
    }
    private static <T> void setValueForTarget(T t, Map<String, Object> paramMap) throws Exception {
        //获取T这个类所有的属性
        Field[] fields = t.getClass().getDeclaredFields();

        //依次遍历pod中的每个属性
        for (Field field : fields) {

            //确保可以访问到这个属性
            field.setAccessible(true);

            // 使用 SkipMappingValueAnnotation 注解跳过
            if (field.isAnnotationPresent(SkipMappingValueAnnotation.class)) {
                continue;
            }

            //获得pod中某个属性的类型
            Class<?> type = field.getType();
            //获取属性的名称，通过这个名称和Map中的Key进行比较判断是否是需要赋值。并且后面打印错误信息也需要fieldName
            String fieldName = field.getName();

            //判断当前field是否需要赋值
            if (paramMap.containsKey(fieldName)) {
                //判断给定的类型是否为成员内部类
                if (type.isMemberClass()) {

                    //Memory=4.0
                    //是内部类就要创建对象
                    Object fieldObj = type.getDeclaredConstructor().newInstance();
                    //通过递归操作给这个对象内部下一层类型赋值
                    setValueForTarget(fieldObj, (Map<String, Object>) paramMap.get(fieldName));
                    //然后最外层赋值
                    field.set(t, fieldObj);
                } else if (type == List.class) {
                    assignForList(paramMap,field,fieldName,t);
                } else {
                    //给基本数据类型赋值
                    //paramValue为long型值为1        long paramValue=1,type=long fieldname=generation
                    Object paramValue = paramMap.get(fieldName);
                    //Object value = convertValue((String) paramValue, type);

                    //基本数据类型赋值  paramValue一定是String类型，先强转。
                    assignValueForBasicDataType(type,(String)paramValue,t,field);
                }
            }
        }

    }
    public static Map<String, Object> parseAuxiliaryMap(String[] params) {
        //使用LinkedHashMap
        //创建辅助map，
        Map<String, Object> auxiliaryMap = new LinkedHashMap<>();


        for (String param : params) {
            //把每一个键值对按照第一个等号划分为两个
            String[] keyValue = param.split("=",2);
            //判断长度为2的目的是有时候第一个等号右边什么都没有的情况
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];

                //第一个等号右边为空，手动设置为null
                //添加value为空的值,设置为null，后续需要判断Boolean类型，为null的话也要设置为false。
                if("".equals(value)){
                    value=null;
                }

                //传递参数首字母小写不添加
                if(Character.isUpperCase(key.charAt(0))) {
                    //情况一：添加正常的键值对
                    setForAuxiliaryMap(auxiliaryMap, key, value);
                }

            }
        }
        return auxiliaryMap;
    }
    private static void setForAuxiliaryMap(Map<String, Object> paramMap, String key, String value) {
        //key=Container.5.Environment.2.Key
        //value==ROOT_PASSWORD

        //parts=[Container,5,Environment,2,Key]
        String[] parts = key.split("\\.");
        Map<String, Object> currentMap = paramMap;

        //
        for (int i = 0; i < parts.length; i++) {
            // 将参数key首字母转换为小写与class属性对应
            //String part = Character.toLowerCase(parts[i].charAt(0)) + parts[i].substring(1);
            //part=container
            String part=firstAlpLowerCase(parts[i]);
            // 后面没有嵌套存入数据
            if (i == parts.length - 1) {
                currentMap.put(part, value);
            } else {
                if (!currentMap.containsKey(part)) {
                    // 若不存在就新增
                    Map<String, Object> nestedMap = new LinkedHashMap<>();
                    currentMap.put(part, nestedMap);
                    currentMap = nestedMap;
                } else {
                    // 若存在 就插入
                    Object obj = currentMap.get(part);
                    if (obj instanceof Map) {
                        currentMap = (Map<String, Object>) obj;
                    } else {
                        Map<String, Object> nestedMap = new LinkedHashMap<>();
                        currentMap.put(part, nestedMap);
                        currentMap = nestedMap;
                    }
                }
            }
        }
    }

}


