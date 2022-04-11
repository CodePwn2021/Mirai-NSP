package com.codepwn.nsp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.error
import net.mamoe.mirai.utils.info
import org.json.JSONObject

object PluginMain : KotlinPlugin(JvmPluginDescription(
    id = "com.codepwn.nsp",
    name = "NeteaseSearchPlayer",
    version = "1.1.4",
) {
    author("CodePwn")
    info("""这个插件可以按呢称查询我的世界中国版的玩家信息""")
}) {
    override fun onEnable() {
        // 初始化信息
        val pluginTag = "[NSP] "
        PluginConfig.reload()
        PluginData.reload()
        logger.info { pluginTag + "欢迎使用本插件！" }
        if (PluginConfig.masterQQ == 0L) {
            logger.error { pluginTag + "未设置主人QQ，请关闭MiraiConsole后打开配置目录的NSP_Config.yml，修改群组白名单的群号，随后重启MiraiConsole。" }
        } else {
            logger.info { pluginTag + "当前设置的主人QQ：" + PluginConfig.masterQQ }
        }

        // 启动监听协程
        this.globalEventChannel().subscribeAlways<GroupMessageEvent> {

            // 忽略enableList以外的群消息
            if (group.id !in PluginConfig.enableList) return@subscribeAlways

            // 忽略机器人自己发的群消息
            if (sender.id == bot.id) return@subscribeAlways

            // 群内只对命令有反应
            if (message.contentToString().startsWith(".nsp ")) {
                when (message.contentToString().replace(".nsp ", "").split(" ")[0]) {
                    "name" -> {

                        val timeout = Utils.checkTimeout(group.id)
                        if (sender.id != PluginConfig.masterQQ && timeout.split("_")[1] != "yes") {
                            group.sendMessage("[NSP] " + PluginConfig.searchTooFastTips.replace("%NSP_sec%",
                                timeout.split("_")[0]))
                            return@subscribeAlways
                        }

                        val searchResult = Utils.searchInNetease(message.contentToString().replace(".nsp name ", ""))
                        if (
                            searchResult.startsWith("{")
                        ) {
                            val obj = JSONObject(searchResult)
                            val entitys = obj.getJSONObject("entity")
                            val playerName = entitys.getString("name")
                            val playerUid = entitys.getString("entity_id")
                            val playerSignature = entitys.getString("signature").ifEmpty { "这个玩家很懒，没有设置签名哦～" }
                            val playerAvatar = entitys.getString("avatar_image_url")
                            val playerRegister = entitys.getInt("register_time").toString()
                            val playerLogin = entitys.getInt("login_time").toString()
                            val playerLogout = entitys.getInt("logout_time").toString()
                            val needSendResult = "玩家 $playerName 的信息：\n玩家UID：$playerUid\n玩家注册时间：\n" + Utils.ts2d(
                                playerRegister) + "\n玩家上一次登录时间：\n" + Utils.ts2d(playerLogin) + "\n玩家上一次登出时间：\n" + Utils.ts2d(
                                playerLogout) + "\n玩家头像链接：\n" + playerAvatar.toString() + "\n玩家签名内容：\n" + playerSignature.toString() + "\n\n" + PluginConfig.searchTips
                            try {
                                val avatarImage = Utils.urlRes2InputStream(playerAvatar)
                                group.sendMessage(group.uploadImage(avatarImage) + needSendResult)
                                withContext(Dispatchers.IO) {
                                    avatarImage.close()
                                }
                            } catch (e: Exception) {
                                group.sendMessage("[该玩家头像无法加载，可能是头像被删除]\n$needSendResult")
                            }
                        } else {
                            group.sendMessage(searchResult)
                        }
                    }

                    "help" -> group.sendMessage("""
                            [NSP] 帮助
                            .nsp name <serverName> <nickName>
                            .nsp help
                            以下命令只有主人才可以执行，其他人无效
                            .nsp set <config> <value>
                            .nsp add/remove <config> <value>
                            """.trimIndent())

                    "set" -> {
                        if (sender.id != PluginConfig.masterQQ) {
                            group.sendMessage("[NSP] 只有主人才能修改配置！")
                            return@subscribeAlways
                        }

                        try {
                            when (message.contentToString().replace(".nsp set ", "").split(" ")[0]) {
                                "searchTimeoutSec" -> {
                                    val value = message.contentToString().replace(".nsp set ", "").split(" ")[1].toInt()
                                    PluginConfig.searchTimeoutSec = value
                                    group.sendMessage("[NSP] 查询延时已设置为 $value 秒")
                                    PluginConfig.save()
                                }

                                "searchTooFastTips" -> {
                                    val value = message.contentToString().replace(".nsp set searchTooFastTips ", "")
                                    PluginConfig.searchTooFastTips = value
                                    group.sendMessage("[NSP] 查询过快提示已设置为 $value")
                                    PluginConfig.save()
                                }

                                "searchTips" -> {
                                    val value = message.contentToString().replace(".nsp set searchTips ", "")
                                    PluginConfig.searchTips = value
                                    group.sendMessage("[NSP] 回复小尾巴已设置为 $value")
                                    PluginConfig.save()
                                }

                                else -> group.sendMessage("[NSP] 设置选项错误，请检查输入")
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            group.sendMessage("[NSP] 设置选项错误，请检查输入")
                        }
                    }

                    "add" -> {
                        if (sender.id != PluginConfig.masterQQ) {
                            group.sendMessage("[NSP] 只有主人才能修改配置！")
                            return@subscribeAlways
                        }

                        try {
                            when (message.contentToString().replace(".nsp add ", "").split(" ")[0]) {
                                "enableList" -> {

                                    val value =
                                        message.contentToString().replace(".nsp add ", "").split(" ")[1].toLong()
                                    PluginConfig.enableList.add(value)
                                    group.sendMessage("[NSP] 启用群组名单添加 $value 成功")
                                    PluginConfig.save()
                                }

                                "bannedNickname" -> {
                                    val value = message.contentToString().replace(".nsp add ", "").split(" ")[1]
                                    PluginConfig.bannedNickname.add(value)
                                    group.sendMessage("[NSP] 禁止查询玩家名单添加 $value 成功")
                                    PluginConfig.save()
                                }

                                else -> group.sendMessage("[NSP] 设置选项错误，请检查输入")
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            group.sendMessage("[NSP] 设置选项错误，请检查输入")
                        }
                    }

                    "remove" -> {
                        if (sender.id != PluginConfig.masterQQ) {
                            group.sendMessage("[NSP] 只有主人才能修改配置！")
                            return@subscribeAlways
                        }

                        try {
                            when (message.contentToString().replace(".nsp remove ", "").split(" ")[0]) {
                                "enableList" -> {
                                    val value =
                                        message.contentToString().replace(".nsp remove ", "").split(" ")[1].toLong()
                                    PluginConfig.enableList.remove(value)
                                    group.sendMessage("[NSP] 启用群组名单移除 $value 成功")
                                    PluginConfig.save()
                                }

                                "bannedNickname" -> {
                                    val value = message.contentToString().replace(".nsp remove ", "").split(" ")[1]
                                    PluginConfig.bannedNickname.remove(value)
                                    group.sendMessage("[NSP] 禁止查询玩家名单移除 $value 成功")
                                    PluginConfig.save()
                                }

                                else -> group.sendMessage("[NSP] 设置选项错误，请检查输入")
                            }
                        } catch (e: IndexOutOfBoundsException) {
                            group.sendMessage("[NSP] 设置选项错误，请检查输入")
                        }
                    }
                    else -> group.sendMessage("[NSP] 命令输入不正确，你可以尝试使用“.nsp help”查看帮助")
                }
            }

            /*
            if(message.contentToString().startsWith("指定")) {
                group.sendMessage("指定")
                return@subscribeAlways
            }
            group.sendMessage("喵~")
            */
            return@subscribeAlways
        }
    }
}

