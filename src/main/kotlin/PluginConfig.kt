package com.codepwn.nsp

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("NSP_Config") {

    @ValueDescription("""
        主人QQ（Long）
        只有主人才可以通过私聊机器人的方式
        添加或者移除群组白名单
        """)
    val masterQQ by value(0L)

    @ValueDescription("""
        群组白名单（Long）
        在白名单的群才可以使用插件命令
        """)
    val enableList: MutableList<Long> by value(mutableListOf(123456L))

    @ValueDescription("""
        禁止查询的呢称名单（String）
        在名单内的玩家不可被查询
        如果某玩家头像是敏感图片，用这个禁止名单会很有效果
        """)
    val bannedNickname: MutableList<String> by value(mutableListOf("我是一个不可被查询的呢称"))

    @ValueDescription("""
        查询延时 单位秒（Int）
        设置需要过xx秒才可以进行下一次查询
        """)
    var searchTimeoutSec by value(20)

    @ValueDescription("""
        查询太快提示（String）
        变量是 %NSP_sec%
        合理放置变量可清晰明了地提示用户
        """)
    var searchTooFastTips by value("查询过快，请在%NSP_sec%秒后查询")

    @ValueDescription("""
        回复小尾巴（String）
        想怎么写就怎么来
        """)
    var searchTips by value("- Search by NSP -")
}