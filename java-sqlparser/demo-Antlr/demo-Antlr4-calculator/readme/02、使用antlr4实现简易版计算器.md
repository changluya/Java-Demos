# 源码路径

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232305917.png)

# 实现目标

支持两种情况：

```java
表达式 换行
变量 = 表达式
```

对两种情况表达式来进行计算

举例：

```java
a=1
b=2
c=a+b*2
d=3+c
c
d
```

不仅仅还要实现表达式的计算，在这里额外多出来针对变量c、d最终值的计算处理。

期望输出的是c、d的取值：

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232305647.png)

# 详细实现步骤

## 1、引入antlr4的pom依赖与插件

```xml
<properties>
    <maven.compiler.source>8</maven.compiler.source>
    <maven.compiler.target>8</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <antlr.version>4.8</antlr.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.antlr</groupId>
        <artifactId>antlr4-runtime</artifactId>
        <version>${antlr.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.antlr</groupId>
            <artifactId>antlr4-maven-plugin</artifactId>
            <version>${antlr.version}</version>
            <executions>
                <execution>
                    <id>antlr</id>
                    <goals>
                        <goal>antlr4</goal>
                    </goals>
                    <phase>generate-sources</phase>
                </execution>
            </executions>
            <configuration>
                <sourceDirectory>${basedir}/src/main/resources</sourceDirectory>
                <outputDirectory>${basedir}/src/main/java/com/changlu/parser</outputDirectory>
                <listener>true</listener>
                <visitor>true</visitor>
                <treatWarningsAsErrors>true</treatWarningsAsErrors>
            </configuration>
        </plugin>
    </plugins>
</build>
```

对于该插件后续可执行运行命令：

```shell
mvn antlr4:antlr4
```

## 2、编写语法规则文件：Calculator.g4

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232305627.png)

```java
// 带赋值的计算器的语法定义文件
grammar Calculator; // 语法的名字

@header {
    package com.changlu.parser;
}

// EBNF语法表示法
// 程序program是由一系列语句构成的
// +表示1个或多个
program: statement+;

// 语句： 三种描述方式
//   - 表达式 换行符
//   - 赋值语句：标识符 '=' 换行符
//   - 换行符
// 注意：#在这里并不是注释的意思，在antlr中 # 可以给语法规则取名字
statement: expression NEWLINE           # printExpression
         | ID '=' expression NEWLINE    # assign
         | NEWLINE                     # blank
         ;

// 编写表达式
// INT表示整型、ID表示变量名（自定义的）
// op是我们给运算符取的名字
expression : expression op=('*'|'/') expression  # MulDiv
           | expression op=('+'|'-') expression  # AddSub
           | INT                                 # int
           | ID                                  # id
           | '(' expression ')'                  # parens
           ;

// 变量名：一个或者多个大小写字母
ID : [a-zA-Z]+;
// 整型
INT : [0-9]+;
// 换行符 \r?表示0个或者1个回车符
NEWLINE : '\r'? '\n';
// 空白字符
// [ \t]+ 含义如下：
//      [ ]：表示字符集合，匹配方括号内的任意一个字符。
//      （空格）：表示空格字符。
//       \t：表示制表符（Tab 键产生的字符）。
//      [ \t]：表示匹配一个空格字符或一个制表符。
//      +：表示前面的字符集合 [ \t] 可以出现一次或多次。
// -> skip 是 ANTLR 的一个指令，表示当解析器遇到匹配此规则的输入时，会跳过这些字符，不将它们作为 Token 传递给语法分析器。
WS : [ \t]+ -> skip;

//实际上面expression的语法规则中，可以使用MUL、DIV、ADD、SUB 代替，但是为了简洁，可以不使用
MUL: '*';
DIV: '/';
ADD: '+';
SUB: '-';
```

**实现直接Test测试语法规则：**

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232305666.png)

**对于写多行（带有换行符），也是可以解析到多个statement情况**：

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232305793.png)

针对目前测试的场景表达式求值：

```java
a=1
b=2
c=a+b*2
d=3+c
c
d
```

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306399.png)

------

## 3、自动生成词法、语法解析器

### 配置Antlr生成参数

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306041.png)

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306225.png)

支持生成监听者模式以及访问者模式，我们一般选择访问者模式生成。

### 命令生成词法、语法解析器

执行命令：

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306705.png)

```shell
mvn antlr4:antlr4
```

此时会在步骤1中指定的目录下生成相关词法、语法文件。

有用的只是这六个文件：

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306381.png)

lexer：词法分析器。用于切分为词法标记流。

Parser：语法分析器。进行语法分析，将其转换为抽象语法树。

说明：目前对于监听器Listener暂无使用场景后续可都删除掉。

------

## 4、实现自定义visitor来计算表达式值

对于如何计算表达式值，可以通过遍历语法树来进行计算。

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306650.png)

说明：这里由于是要求表达式的计算值，所以这里extends CalculatorBaseVisitor<Integer>中**泛型为Integer**，表示访问某个节点值最终返回的都是数值。

```java
package com.changlu;

import com.changlu.parser.CalculatorBaseVisitor;
import com.changlu.parser.CalculatorLexer;
import com.changlu.parser.CalculatorParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.Map;

// 计算器的结果是整型，所以访问抽象语法树的每个节点的返回值也是整型
public class CalculatorVisitor extends CalculatorBaseVisitor<Integer> {

    // map 用来保存变量和值的变量关系  a = 1  => {"a": 1}
    // key：变量名
    // value：变量名的所对应的值
    Map<String, Integer> memory = new HashMap<>();

    /**
     * 访问变量名 a = expression
     * @param ctx the parse tree
     * @return 返回表达式的值
     */
    @Override
    public Integer visitAssign(CalculatorParser.AssignContext ctx) {
        // 取出=左边的变量名
        String paramName = ctx.ID().getText();
        // 取出=右边的表达式
        // 根据表达式来求值
        Integer value = visit(ctx.expression());
        memory.put(paramName, value);
        return value;
    }

    // expression NEWLINE
    @Override
    public Integer visitPrintExpression(CalculatorParser.PrintExpressionContext ctx) {
        Integer value = visit(ctx.expression());
        // 每针对带有 expression NEWLINE 情况会进行打印
        System.out.println(ctx.expression().getText() + " = " + value);
        return value;
    }

    // 访问到INT表达式情况
    @Override
    public Integer visitInt(CalculatorParser.IntContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    // 访问到 '(' expression ')'情况
    @Override
    public Integer visitParens(CalculatorParser.ParensContext ctx) {
        // 直接返回'(' expression ')' 中的表达式的值
        return visit(ctx.expression());
    }

    // 访问到 expression op=('*'|'/') expression 情况
    @Override
    public Integer visitMulDiv(CalculatorParser.MulDivContext ctx) {
        Integer leftVal = visit(ctx.expression(0));
        Integer rightVal = visit(ctx.expression(1));
        if (ctx.op.getType() == CalculatorParser.MUL) {
            return leftVal * rightVal;
        }else {
            return leftVal / rightVal;
        }
    }

    // 访问到 expression op=('+'|'-') expression 情况
    @Override
    public Integer visitAddSub(CalculatorParser.AddSubContext ctx) {
        Integer leftVal = visit(ctx.expression(0));
        Integer rightVal = visit(ctx.expression(1));
        if (ctx.op.getType() == CalculatorParser.ADD) {
            return leftVal + rightVal;
        }else {
            return leftVal - rightVal;
        }
    }

    // 访问到 ID 情况 （最终得到求表达式的值）
    @Override
    public Integer visitId(CalculatorParser.IdContext ctx) {
        String id = ctx.ID().getText();
        if (memory.containsKey(id)) return memory.get(id);
        return 0;
    }

}
```

对于某个节点，可根据语法文件.g4来进行编写访问过程操作：

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306071.png)

## 5、实现测试方法（自定义visitor中）

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306640.png)

```java
public static void main(String[] args) {
    String expr = "a = 1\n" +
            "b = 2\n" +
            "c = a + b * 2\n" +
            "d = 3 + c\n" +
            "c\n" +
            "d\n";
    // 1、将字符串转换成流
    CodePointCharStream stream = CharStreams.fromString(expr);
    // 2、实例化词法分析器 进行词法分析
    // 词法分析器初始化
    CalculatorLexer lexer = new CalculatorLexer(stream);
    // 词法分析
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    // 3、实例化语法分析器
    CalculatorParser parser = new CalculatorParser(tokens);
    // 语法分析并转为抽象语法树
    CalculatorParser.ProgramContext tree = parser.program();

    // 进行表达式求值
    CalculatorVisitor visitor = new CalculatorVisitor();
    visitor.visit(tree);
}
```

运行测试方法：

![img](https://pictured-bed.oss-cn-beijing.aliyuncs.com/img/2024/202503232306328.png)



# 参考资料

[1]. 尚硅谷技术中台实战教程，大数据九章云台项目【视频 antlr】：https://www.bilibili.com/video/BV1vR4y1z79G

