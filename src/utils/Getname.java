package utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

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
            tableList.addAll(tableNameList);
        } catch (JSQLParserException e) {
            System.out.println("解析sql出错\n");
        }
    }

    public static String getdropname(String sql){
        String pattern = "drop\\s+table\\s+([\\w]+)";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(sql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            return tableName;
        } else {
            return null;
        }
    }

    public static void main(String args[]){
        String sql = "DROP TABLE mytable";
        String pattern = "DROP\\s+TABLE\\s+([\\w]+)";

        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(sql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            System.out.println("Table name: " + tableName);
        } else {
            System.out.println("No table name found.");
        }
    }
}
