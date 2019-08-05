## 安装
 - `yum install docker` 安装
 - `systemctl start docker` 启动
 - `systemctl enable docker` 开机启动
 - `docker info`查看docker信息
## 常用命令
 - `docker images` 查看所有镜像
 - `docker rmi [IMAGE ID]`删除镜像
 - `docker run -d [IMAGE ID]` 指定镜像ID启动一个容器，多次执行同一个镜像会产生多个容器
 - `docker ps` 查看当前正在运行的容器 `docker ps -a`查看所有容器
 - `docker start [CONTAINER ID]`指定容器ID启动一个容器，注意与`docker run `的区别
 - `docker stop [CONTAINER ID]`停止一个容器
 - `docker rm [CONTAINER ID]`删除容器，`$(docker ps -a -q)`删除所有状态为停止的容器
##  Docker Registry
 - `docker pull registry`从官方镜像库拉取
 - `docker run -d -v /usr/locacl/registry:/var/lib/registry --restart=always --name registry registry`