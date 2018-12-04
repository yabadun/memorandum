# 编码
- ascii不包含中文编码
- GB2312中文编码，一个16进制对应一个中文汉字，127以下为ascii(第一个字节0开头)，以上为中文
- unicode字符集

# Basic
- 静态内部类只能访问外部类的静态变量

# java中获取jar包路径的方法
- `String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();`
