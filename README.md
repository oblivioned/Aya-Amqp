# Aya-Gateway
Actually, it's a producer. use rabbitmq.

# 网关节点的功能
1.交易验证与提交，但是不直接返回交易结果，仅仅返回交易的Hash值，并且在初次验证签名后将正确的交易发送到交易池中。

2.交易查询，应当与交易池通讯可以选择使用RPC方式或HTTP方式，主要是获取交易池中的状态。

3.交易池分发，把有效的交易进行分类发送到不同地区的交易池中。比如按照Hahs值的首字母进行分发，建立26个交易池。

# 后续内容
1.应当使用线程池管理连接，增加链接数量。

2.应当验证签名，若签名不正确应当直接拒绝请求。

3.网关并不会同步返回结果，交易提交后只有一个交易号，需要通过其他的方式查询交易状态。

4.应当根据不同的地区，不同的APP选择使用的队列服务器。

# 请求的格式为

POST : http://localhost:port/path

RawBody : {"sender":"0x12sfklj1871891723891789","data":{"sig":"Transfer","parmas":["Address2","1"]},"sign":"0xSDLKJFLKSsdlkfjlksdlkfjlk23j4234sldkfjklj"}
