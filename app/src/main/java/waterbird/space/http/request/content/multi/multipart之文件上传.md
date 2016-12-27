
## http multipart request

Http-Multipart-Request是HTTP客户端创建的请求用于向HTTP服务器发送文件和数据的http请求。multipart标准可以参考[The Multipart content type](https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html)。

Notes: 下面中的每一行都是以"\r\n"结束

### enctype：multipart/form-data格式

```
    <form action="http://localhost:8000" method="post" enctype="multipart/form-data">
      <p><input type="text" name="text1" value="text default">
      <p><input type="text" name="text2" value="a&#x03C9;b">
      <p><input type="file" name="file1">
      <p><input type="file" name="file2">
      <p><input type="file" name="file3">
      <p><button type="submit">Submit</button>
    </form>

    POST / HTTP/1.1
    [[ Less interesting headers ... ]]
    Content-Type: multipart/form-data; boundary=---------------------------735323031399963166993862150
    Content-Length: 834

    -----------------------------735323031399963166993862150
    Content-Disposition: form-data; name="text1"

    text default
    -----------------------------735323031399963166993862150
    Content-Disposition: form-data; name="text2"

    -----------------------------735323031399963166993862150
    Content-Disposition: form-data; name="file1"; filename="a.txt"
    Content-Type: text/plain

    Content of a.txt.

    -----------------------------735323031399963166993862150
    Content-Disposition: form-data; name="file2"; filename="a.html"
    Content-Type: text/html

    <!DOCTYPE html><title>Content of a.html.</title>

    -----------------------------735323031399963166993862150
    Content-Disposition: form-data; name="file3"; filename="binary"
    Content-Type: application/octet-stream

    -----------------------------735323031399963166993862150--
```

### enctype：application/x-www-form-urlencoded格式

```
    POST / HTTP/1.1
    [[ Less interesting headers ... ]]
    Content-Type: application/x-www-form-urlencoded
    Content-Length: 51

    text1=text+default&text2=a%CF%89b&file1=a.txt&file2=a.html&file3=binary
```

上述两种方式的不同地方
- multipart/form-data: adds a few bytes of boundary overhead to the message, and must spend some time calculating it, but sends each byte in one byte.
- application/x-www-form-urlencoded: has a single byte boundary per field (&), but adds a linear overhead factor of 3x for every non-printable character.

