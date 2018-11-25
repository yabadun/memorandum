# 安装

## erlang安装

- [erlang](https://github.com/rabbitmq/erlang-rpm/releases)零依赖的专用于rabbitmq的rpm安装包
- `erl`测试安装成功

## rabbitmq安装

- [rpm](https://www.rabbitmq.com/install-rpm.html)安装
- `yum -y install socat`可能会缺少socat依赖
- `rpm -qa |grep rabbit`查找是否安装
- `rpm -e --nodeps rabbitmq-server-3.6.8-1.el7.noarch`删除已安装的rabbitmq
- `rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc`导入签名
- `rabbitmq-plugins enable rabbitmq_management`启动管理插件
- `rabbitmqctl add_user [username] [password]`添加用户
- `rabbitmqctl set_user_tags [username] [administrator]`分配用户标签
- `rabbitmqctl set_permissions -p "/" [username] ".*" ".*" "/*"`分配用户权限
- `firewall-cmd --zone=public --add-port=15672/tcp --permanent`
- `firewall-cmd --reload`防火墙开启端口

> [erlang与rabbitmq版本关系](https://www.rabbitmq.com/which-erlang.html)
