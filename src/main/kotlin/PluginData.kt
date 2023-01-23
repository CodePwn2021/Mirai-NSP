package com.codepwn.nsp

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object PluginData: AutoSavePluginData("NSP_GroupCache") {

    // 缓存群组的上一次查询时间戳
    // GroupId:Long, GroupTimestamp:Long
    val GroupSearchTimeout:MutableMap<Long,Long> by value()
}
