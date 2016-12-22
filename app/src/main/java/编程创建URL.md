
## 写在前面

统一资源标识符URI(Uniform Resource Identifier)，URI是资源标识符的抽象，其具体实现包括URL(Uniform Resource Locator), URN, 磁力链接等。

1. URL是URI的一种，不仅标识了Web 资源，还指定了操作或者获取方式，同时指出了主要访问机制和网络位置。
2. URN是URI的一种，用特定命名空间的名字标识资源。使用URN可以在不知道其网络位置及访问方式的情况下讨论资源。

例子：
- URL： http://bitpoetry.io/posts/hello.html#intro， 其中http://是指访问资源的方式，bitpoetry.io/posts/hello.html是指访问资源的方式，#intro是资源所在的位置；
- URL： http://bitpoetry.io/posts/hello.html，通过URL可以知道资源的位置；
- URN： bitpoetry.io/posts/hello.html#intro，URN不包括访问方式；

本文主要根据[URL Encoding](https://www.talisman.org/~erlkonig/misc/lunatech%5Ewhat-every-webdev-must-know-about-url-encoding/)来介绍URL的基础，URL编码中存在的问题以及如何使用Java编写出正确的URL。

### HTTP URL构成

通过例子https://bob:bobby@www.lunatech.com:8080/file;p=1?q=2#third详解一个复杂URL的组成，具体说明如下表所示：

| schema | user | password | host address | port | path | path parameters | query parameters | fragment |
| :------:| :------: | :------: | :------: | :------: | :------: | :------: | :------: | :------: |
| https | bob | bobby | www.lunatech.com | 8080 | /file | p=1 | q=2 | third |

在HTTP URL中schema通常是固定为http或https，并且user:pwd@hostAddress:port部分通常用于指定资源所在的位置，而path则指定资源的具体路径以"/"开始，路径信息按照文件系统中的树形结构构成，例如：路径"/photos/govind/me.jpg"有3部分构成，其中me.jpg之资源文件，其在文件夹govind下面，而govind文件夹的父目录是photos；根据W3C中标准的定义，每一个path后面允许提供path parameters，参数在path后面用于 **;** 分隔，因此上述例子中/file;p=1表示文件file有一个参数q其值为1。

在路径后面是查询字符串，通过 **？** 与path分隔，查询字符串是由 **=** 连接的键值对，查询字符串中可以包含多个键值对，每个键值对使用 **&** 分隔；通常在[HTML Form](http://www.w3.org/TR/html401/interact/forms.html)进行表单提交时会以查询字符串的形式放在URL后面。

fragment用于指定所引用的不是整个URL所指定的资源文件，而是资源文件中的一部分，当访问URL时，URL会自动滑动到该部分资源所在的位置。

通过上述分析可以知道，"://"用于分隔schema和host部分，使用"/"分隔host和path部分，使用"?"分隔path和query部分；也就是说这个符号是被用在URI中作为保留字符使用的，那么我们想要在path或query中使用这些保留字符，就必须对path或query进行 **URL-Encoded**；例如要使用？，path="photo?.png"需要首先将"?"转换为字节序列并用十六进制表示，然后加上"%"，进行URL-Encoded的paht="photo%3F.png"，这样保证不会把path当做query来解析了。**实际上现在大部分浏览器都是显示Decoding后的URL，并且每次请求网络时都会在Encoding URl后进行**

### URL pitfalls

1. 字符编码，不同的编码格式对特殊字符解释不同，若不预先指定，host name和path可能采用不同的编码格式，容易造成乱码问题。

2. URL中不同部分的保留字符不同，例如在path中空格要被编码为"%20",而"+"号则不需要编码；在query中，空格会被编码为"+"(后向兼容)或"%20"，而"+"号会被编码为"%2B"，这就意味着"blue+light blue"在path("blue+light%20blue")中和query("blue%2Blight+blue")中是不同的，因此针对path部分和query部分需要进行不同的处理。

```
//path、uery的编码方式不同，这样的话若采用同一种方式进行就会导致其中另一种解析失败！
String str = "blue+light blue";
String url = "http://example.com/" + str + "?" + str;
```

3. URL只有在URL-decoded之前才是有意义的，因为在URL解码后一些保留字段就会出现，例如："http://example.com/blue%2Fred%3Fand+green"在解码前其path="blue%2Fred%3Fand+green",解码后path="blue/red?and+green"，因此若在解码后在分析，因为其中出现了path和query的分隔符，因此会将其解析为两部分，这样就不满足实际URL的含义了。

```
URL解码前：http://example.com/blue%2Fred%3Fand+green
URL解码后：http://example.com/blue/red?and+green
```

PS：URL重写时在进行匹配操作之前不能将URL解码。

4. 解码后的URL再次进行编码时，不能够得到以前的URL。上述代码中，解码后的URL是一个合法的URL，因此对其进行编码后得到的还是http://example.com/blue/red?and+green。

### Handling URL in Java

#### 对URLEncoder,Decoder的误解

><font size="6">Do not use</font> java.net.URLEncoder or java.net.URLDecoder <font size="6">for whole URLs.</font>

根据[Java API doc](http://download.java.net/jdk7/docs/api/java/net/URLEncoder.html)中所说，URLEncoder，URLDecoder:

>Utility class for HTML form encoding. This class contains static methods for converting a String to the application/x-www-form-urlencoded MIME format.

可以看出它不是用来对整个URL进行编码或解码的，最多只能够用来编码解码查询字符串(query part)；<font color="red" size="6">这就是导致许多人错误的使用URLencoder,URLDecoder对URL进行编码，解码。</font>

#### 构建URL时分别编码每一部分

在实际构建URL的时候，由于path和quuery部分会出现一些保留字符，因此需要对其进行编码后才能放在URL中，否则在解析时就会出现问题。如下面例子所示

```
    String pathSegment = "a/b?c";
    String url = "http://example.com/" + URLUtils.encodePathSegment(pathSegment);
    结果：http://example.com/a%2Fb%3Fc


    String value = "a&b==c";
    String url = "http://example.com/?query=" + URLUtils.encodeQuerySegment(value);
    结果：http://example.com/?query=a%26b==c

```

#### [ URI.getPath()](http://download.java.net/jdk7/docs/api/java/net/URI.html#getPath%28%29)<font size="8">  or  </font>[URI.getRawPath()](http://download.java.net/jdk7/docs/api/java/net/URI.html#getRawPath%28%29)

解码后的URL就失去了其本身的句法含义，因此采用getPath得到的是解码后的URL，对其处理没有任何意义因为它不是我们原本URL；因此对URL的处理只能通过getRawPath处理path部分。如下例子所示：

```
错误处理方式：
    URI uri = new URI("http://example.com/a%2Fb%3Fc");
    for(String pathSegment : uri.getPath().split("/"))  //getPath="http://example.com/a/b?c"
        System.err.println(pathSegment);

正确处理方式：
    URI uri = new URI("http://example.com/a%2Fb%3Fc");
    for(String pathSegment : uri.getRawPath().split("/")) //getRawPath="http://example.com/a%2Fb%3Fc"
        System.err.println(URLUtils.decodePathSegment(pathSegment));

```

#### Apache Commons HTTPClient's URI

采用[ Apache Commons HTTPClient 3](http://hc.apache.org/httpclient-3.x/)的[URI](http://hc.apache.org/httpclient-3.x/apidocs/org/apache/commons/httpclient/URI.html)类对URL进行编码与使用java.net.URLEncoder类进行编解码是一样错误的，不但解码有问题，它对URL中的每一部分都采用相同的方式编码(保留字符集相同)。

### 确保URL正确的被Encoding

1. 构建URL时针对每部分(path，query)分别进行Encoding;
2. Make sure your URL-rewrite filters deal with URLs correctly. [Url Rewrite Filter](http://tuckey.org/urlrewrite/) is a URL rewriting filter we use in [Seam](http://www.seamframework.org/) to transform pretty URLs into application-dependent URLs.
3. Using Apache mod-rewrite correctly.

Note: 后面两种在web服务端会经常使用，由于没有做相关工作，这方面具体可以参考[What every web developer must know about URL encoding](https://www.talisman.org/~erlkonig/misc/lunatech%5Ewhat-every-webdev-must-know-about-url-encoding/)中的做法。


