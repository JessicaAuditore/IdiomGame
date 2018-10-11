package org.soul.database;

import org.soul.database.annotation.Column;
import org.soul.database.annotation.Id;
import org.soul.database.annotation.Ignore;
import org.soul.entity.Idiom;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBAdapter {

    public static List<Object> convertResultSet(ResultSet rs, Class clazz) throws Exception {
        List<Object> objects = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        List<String> cm = new ArrayList<>();
        List<String> sm = new ArrayList<>();
        List<Class> ty = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Ignore.class))
                continue;
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                cm.add(column.name());
            } else
                cm.add(field.getName());
            sm.add(field.getName());
            ty.add(field.getType());
        }
        while (rs.next()) {
            Object obj = clazz.newInstance();
            for (String n : cm) {
                int index = cm.indexOf(n);
                String source = sm.get(index);
                Object v = rs.getObject(n);
                String getName = "set" + source.substring(0, 1).toUpperCase() + source.substring(1);
                Method method = clazz.getMethod(getName, ty.get(index));
                method.invoke(obj, v);
            }
            objects.add(obj);
        }
        return objects;
    }

    public static String convertSelectSql(Object obj) throws Exception {
        String className = obj.getClass().getSimpleName().toLowerCase();
        Field[] fields = obj.getClass().getDeclaredFields();
        String sql_temp = "select * from %s where %s;";
        String whereStr = "";
        for (Field field : fields) {
            String getName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method method = obj.getClass().getMethod(getName);
            Object value = method.invoke(obj);
            if (value == null) {
                continue;
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String na = column.name();
                whereStr = whereStr + na + " = '" + value + "' and ";
            } else
                whereStr = whereStr + field.getName() + " = '" + value + "' and ";
        }
        whereStr = whereStr.substring(0, whereStr.length() - 5);
        String sql = String.format(sql_temp, className, whereStr);
        return sql;
    }

    public static String convertSelect2Sql(Object obj1, Object obj2) throws Exception {
        String className = obj1.getClass().getSimpleName().toLowerCase();
        Field[] fields1 = obj1.getClass().getDeclaredFields();
        Field[] fields2 = obj2.getClass().getDeclaredFields();
        String sql_temp = "select * from %s where %s;";
        String whereStr = "";
        for (Field field : fields1) {
            String getName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method method = obj1.getClass().getMethod(getName);
            Object value = method.invoke(obj1);
            if (value == null) {
                continue;
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String na = column.name();
                whereStr = whereStr + na + " >= '" + value + "' and ";
            } else
                whereStr = whereStr + field.getName() + " >= '" + value + "' and ";
        }

        for (Field field : fields2) {
            String getName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method method = obj2.getClass().getMethod(getName);
            Object value = method.invoke(obj2);
            if (value == null) {
                continue;
            }

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String na = column.name();
                whereStr = whereStr + na + " <= '" + value + "' and ";
            } else
                whereStr = whereStr + field.getName() + " <= '" + value + "' and ";
        }

        whereStr = whereStr.substring(0, whereStr.length() - 5);
        String sql = String.format(sql_temp, className, whereStr);
        return sql;
    }

    public static String conver2Insert(Object user) throws Exception {
        String className = user.getClass().getSimpleName().toLowerCase();
        Field[] fields = user.getClass().getDeclaredFields();
        List<String> values = new ArrayList<>();
        String n = "";
        String v = "";
        for (Field field : fields) {
            if (field.isAnnotationPresent(Ignore.class))
                continue;
            String getName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method method = user.getClass().getMethod(getName);
            String value = (String) method.invoke(user);
            if (value == null)
                continue;

            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String na = column.name();
                int maxSize = column.maxSize();
                if (value.length() > maxSize)
                    System.out.println("fwedgewtr");
                n = n + "," + na;
            } else
                n = n + "," + field.getName();
            v = v + "," + "'" + value + "'";
            values.add(value);
        }
        n = n.substring(1);
        v = v.substring(1);
        String sql_tmp = "INSERT INTO `%s` (%s) VALUES (%s);";
        String sql = String.format(sql_tmp, className, n, v);
        return sql;
    }

    public static String conver2Update(Object user) throws Exception {
        String className = user.getClass().getSimpleName().toLowerCase();
        Field[] fields = user.getClass().getDeclaredFields();
        String sql_temp = "UPDATE %s SET %s WHERE %s";
        String doStr = "";
        String whereStr = "";
        for (Field field : fields) {
            String getName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method method = user.getClass().getMethod(getName);
            String value = (String) method.invoke(user);
            if (value == null)
                continue;
            if (field.isAnnotationPresent(Id.class))
                whereStr = field.getName() + "='" + value + "'";
            else
                doStr = doStr + field.getName() + "='" + value + "','";
        }
        doStr = doStr.substring(0, whereStr.length() - 1);
        String sql = String.format(sql_temp, className, doStr, whereStr);
        return sql;
    }
}
