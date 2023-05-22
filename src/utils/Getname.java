package utils;

import java.util.*;
import java.util.regex.Pattern;

public class Getname {

    //正则表达式去除语句中的注释
    public synchronized static String dSql(String sql) {
        Pattern p = Pattern.compile("(?ms)('(?:''|[^'])*')|--.*?$|/\\*.*?\\*/|#.*?$|");
        return p.matcher(sql).replaceAll("$1");
    }

    //解析sql，获取表名
    public synchronized static void parseSql(String sql, Set<String> tableList) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> tableNameList = tablesNamesFinder.getTableList(statement);
            tablelist.addall(tablenamelist);
        } catch (JSQLParserException e) {
            System.out.println("解析sql出错\n");
        }
    }

}
