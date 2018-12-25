# 编码
- ascii不包含中文编码
- GB2312中文编码，一个16进制对应一个中文汉字，127以下为ascii(第一个字节0开头)，以上为中文
- unicode字符集

# Basic
- 静态内部类只能访问外部类的静态变量

# java中获取jar包路径的方法
- `String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();`

# java正则表达式
- `matcher.group(0)`代表匹配到的整个字符串

# JDK8中时区转化
 ``` java
    String s="2018-12-25T17:40:17+08:00";
    DateTimeFormatter iso = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
    System.out.println(ZonedDateTime.parse(s, iso));
  ```
