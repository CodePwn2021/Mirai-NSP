# Mirai-NSP

一个用于查询《我的世界》中国版玩家信息的Mirai-Console插件。  
全名是 `Mirai-NeteaseSearchPlayer`

## 注意事项

网易已经**禁止**获取玩家的注册、上一次登录、上一次登出时间，**时间戳全部变成0，接口差不多废了**。  
我非常气愤，但是奈何说不出什么来。  
~~目前已经是请求网易官方直接添加接口供我们调用、获取不止于此的信息，能让我们做出更好的bot插件。~~  
更正：请求无效  
希望以后真的有这么一天。  

## 用法

从 [Release](https://github.com/CodePwn2021/Mirai-NSP/releases) 下载插件，将插件放入plugins文件夹，打开MiraiConsole，让插件初始化配置，随后关闭MiraiConsole。  
在 `config/com.codepwn.nsp/NSP_Config.yml` 中修改masterQQ，以及enableList中的群号，最后在填入的群号对应的群使用命令即可。  
配置文件中的变量均有说明。命令如下：
```
.nsp name [serverName] [nickName]
.nsp help
以下命令只有主人可执行:
.nsp add/remove [configName] [value]
.nsp set [configName] [value]
```

## 效果

<img src="https://s4.ax1x.com/2022/02/14/Hsjpff.jpg" width="30%" height="30%">

## 特别说明

在未取得商业授权的情况下，修改后的代码应全部公开，使用AGPLV3协议。
