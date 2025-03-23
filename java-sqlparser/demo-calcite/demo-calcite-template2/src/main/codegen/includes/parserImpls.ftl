<#--
// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to you under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
-->

<#--
  Add implementations of additional parser statements, literals or
  data types.

  Example of SqlShowTables() implementation:
  SqlNode SqlShowTables()
  {
    ...local variables...
  }
  {
    <SHOW> <TABLES>
    ...
    {
      return SqlShowTables(...)
    }
  }
-->
// 创建函数
SqlNode SqlCreateFunction() :
{
    // 声明变量
    SqlParserPos createPos;
    SqlParserPos propertyPos;
    SqlNode functionName = null;
    String className = null;
    String methodName = null;
    String comment = null;
    SqlNodeList properties = null;
}
{
    // create 关键字  ｜ Parser.jj中搜索 < CREATE，定义在常量TOKEN里
    <CREATE>
    {
        // 获取当前token的行列位置 ｜ Parser.jj中搜索 SqlParserPos getPos()
        createPos = getPos();
    }
    // FUNCTION 关键字 | Parser.jj中搜索 < FUNCTION，定义在常量TOKEN里
    <FUNCTION>
    // 函数名（字符串解析成一个组件） ｜ Parser.jj中搜索 SqlIdentifier CompoundIdentifier()
    functionName = CompoundIdentifier()
    // as关键字
    <AS>
    // 类名（解析成一个字符串） | 这里StringLiteralValue() 为自己定义的java函数
    { className = StringLiteralValue(); }
    // if语句
    [
        // method 关键字
        <METHOD>
        {
            // 方法名称
            methodName = StringLiteralValue();
        }
    ]
    // if语句
    [
        // property关键字 ｜ 当前自己定义 config.fmpp中
        <PROPERTY>
        {
            // 获取关键字位置
            propertyPos = getPos();
            SqlNode property;
            properties = new SqlNodeList(propertyPos);
        }
        // 匹配左括号 "("  ｜ parser.jj 搜索 < LPAREN: "(">
        <LPAREN>
        [
            // 尝试解析属性值并赋值给 property 变量
            property = PropertyValue()
            {
                // 将解析得到的属性添加到 properties 列表中
                properties.add(property);
            }
            // 零个或多个以逗号分隔的属性值 ｜ ()*表示可以不出现，也可以出现多次
            (
                // 匹配逗号 ","
                <COMMA>
                {
                    // 解析下一个属性值并赋值给 property 变量
                    property = PropertyValue();
                    // 将新解析的属性添加到 properties 列表中
                    properties.add(property);
                }
            )*
        ]
        // 匹配右括号 ")" | parser.jj 搜索 < RPAREN: ")">
        <RPAREN>
    ]
    // if语句
    [
        // comment关键字 ｜ 当前自己定义 config.fmpp中
        <COMMENT>
        {
            // 备注
            comment = StringLiteralValue();
        }
    ]
    {
        // 返回SqlCreateFunction类实现实例
        return new SqlCreateFunction(createPos, functionName, className, methodName, comment, properties);
    }
}

// 自定义java函数
JAVACODE String StringLiteralValue() {
    // 字符串 ｜ Parser.jj中搜索SqlNode StringLiteral()，匹配字符串node
    SqlNode sqlNode = StringLiteral();
    return ((NlsString) SqlLiteral.value(sqlNode)).getValue();
}

/**
 * 解析SQL中的key=value形式的属性值
 */
SqlNode PropertyValue() :
{
    SqlNode key;
    SqlNode value;
    SqlParserPos pos;
}
{
    key = StringLiteral()
    { pos = getPos(); }
    <EQ> value = StringLiteral()
    {
        return new SqlProperty(getPos(), key, value);
    }
}
