# Mirai-NSP
一个用于查询《我的世界》中国版玩家信息的Mirai-Console插件。
## 用法
从 [Release](https://github.com/codepwn2021/Mirai-NSP/releases) 下载插件，将插件放入plugins文件夹，打开MiraiConsole，让插件初始化配置，随后关闭MiraiConsole。  
在 `config/com.codepwn.nsp/NSP_Config.yml` 中修改masterQQ，以及enableList中的群号，最后在填入的群号对应的群使用命令即可。  
命令如下：
```
.nsp name [serverName] [nickName]
.nsp help
以下命令只有主人可执行:
.nsp add/remove [configName] [value]
.nsp set [configName] [value]
```
