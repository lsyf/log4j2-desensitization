# log4j2-desensitization
log4j2脱敏插件。 增加Layout，增加一个类似RegexReplacement的插件，通过ahocorasick匹配敏感字段

# 用法
#### 1. 引入依赖
```xml
    <groupId>io.github.lsyf.log4j2</groupId>
    <artifactId>plugin-desensitization</artifactId>
    <version>1.0.0</version>
```
#### 2. 配置日志
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="WARN" packages="log4j2">
    <properties>
        <property name="pattern">[%d{yyyy-MM-dd HH:mm:ss.SSS}][%-5level][%thread] %logger - %msg%n
        </property>
    </properties>

    <appenders>

        <Console name="console" target="system_out">
            <!-- 使用指定的Layout,除了新增脱敏配置，其他配置和PatternLayout完全一致 -->
            <MyPatternLayout pattern="${pattern}">
                <!-- 脱敏配置 -->
                <Desensitized>
                    <!-- 开关 -->
                    <enable>true</enable>
                    <!-- name是关键字，为__default__代表默认配置 -->
                    <!-- content为匹配的内容，如身份证号是数字，不匹配非数字(除了x)，配置为num|x -->
                    <!-- c为 脱敏后的替代字符-->
                    <!-- ignoreSymbols为 忽视字符，如空格。匹配content时遇到ignoreSymbols中的字符则匹配中断-->
                    <!-- skipHead，skipTail  数据的前后不脱敏字符长度 -->
                    <!-- maxMissed为最大未匹配数 -->
                    <DesensitizedField name="__default__" content="en|num" c="*" ignoreSymbols=" " skipHead="6"
                                       skipTail="4" maxMissed="5"/>
                    <DesensitizedField name="phone" content="num" skipHead="6" skipTail="4"/>
                </Desensitized>
            </MyPatternLayout>
        </Console>


    </appenders>

    <loggers>
        <root level="info">
            <appender-ref ref="console"/>

        </root>

    </loggers>


</configuration>
```

#### 3. 原理

- 扫描日志中的 `name`位置
- 读取其后字符格式为 `content`的字符串( 如果超过`maxMissed`个字符未匹配到，则认为匹配失败不再脱敏)
- 字符串的前`skipHead`和后`skipTail`个字符不处理，其他的字符替换成`c`

案例：

```xml
<DesensitizedField name="idNo" content="num|x" skipHead="6" skipTail="4" c="*" maxMissed="5"/>
```

意味着： 日志中匹配到idNo时，则

#### 4. 配置明细
  - `<Desensitized> </Desensitized>` ：插件配置，不脱敏可以删除

  - `<enable></enable>` ：开关

  - `<DesensitizedField></DesensitizedField>`脱敏具体字段

      - name：脱敏关键字，字符串中如果有该关键字，则之后其n个字符将被脱敏。`name=__default__`为默认配置，详细配置覆盖默认配置
      - content为匹配的内容，如身份证号是数字，不匹配非数字(除了x)，配置为num|x。通过|分隔
          - en(大小写字母)
          - num(数字)
          - lang(语言,值大于128)
          - cn(汉字)
          - low(小写字母)，up(大写字母)
          - 1个字符，代表指定字符。
          - 其他则忽略
    - c为 脱敏后的替代字符
    - ignoreSymbols为 忽视字符，如当空格。可以有多个。匹配content时遇到ignoreSymbols(优先级更高)中的字符则匹配中断(相当于include content exclude ignoreSymbols)。主要是用来应对，关键字和匹配内容间隔很多非相关字符(如空格)的场景，不使用时设为空
    - skipHead，skipTail  不脱敏的字符串前后字符长度
    - maxMissed为最大未匹配数。

    

 