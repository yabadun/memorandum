## 安装
 - `yum install docker` 安装
 - `systemctl start docker` 启动
 - `systemctl enable docker` 开机启动
 - `docker info`查看docker信息
 
## 添加用户
- `sudo groupadd docker`添加用户群组
- `sudo gpasswd -a ${USER} docker`添加用户到群组
- `sudo systemctl restart docker`重启服务
- `newgrp docker`登入docker群组

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
 - `docker run -d -p 5000:5000 -v /usr/locacl/registry:/var/lib/registry --restart=always --privileged=true --name registry registry`启动容器

## mysql
 `docker run -v /var/lib/mysql:/var/lib/mysql --name zdd_mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=123456 --restart=always --privileged=true mysql`

## redis
 - `docker run --name [redisname] -p [6380:6379]  -d [redis] --requirepass "123456"`
 - `docker exec -it [CONTAINER id] redis-cli `
## postgres
  - `docker run --name postgresql -v ~/postgresql/:/var/postgresql/data -e POSTGRES_PASSWORD=123456 -p 5432:5432 --restart=always -d  postgres`
## sonarqube
 `docker run -d --name sonarqube --link postgresql -p 9000:9000 -e sonar.jdbc.username=sonar -e sonar.jdbc.password=sonar -e sonar.jdbc.url=jdbc:postgresql://postgresql:5432/sonarqube sonarqube`
