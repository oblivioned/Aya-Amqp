# Aya-Gateway
Actually, it's a producer. use rabbitmq.

# 后续内容
1.应当使用线程池管理连接，增加链接数量。

2.应当验证签名，若签名不正确应当直接拒绝请求。

3.网关并不会同步返回结果，交易提交后只有一个交易号，需要通过其他的方式查询交易状态。

4.应当根据不同的地区，不同的APP选择使用的队列服务器。

# 请求的格式为

POST : http://localhost:port/path
RawBody : {"sender":"0x12sfklj1871891723891789","data":{"sig":"Transfer","parmas":["Address2","1"]},"sign":"0xSDLKJFLKSsdlkfjlksdlkfjlk23j4234sldkfjklj"}
