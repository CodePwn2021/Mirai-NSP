package com.codepwn.nsp

import com.codepwn.nsp.PluginMain.save
import io.ktor.client.*
import io.ktor.client.request.*
import org.json.JSONObject
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun checkTimeout(GroupId: Long):String {
        return try {
            val cacheSearch = PluginData.GroupSearchTimeout[GroupId]
            if (System.currentTimeMillis().div(1000) > cacheSearch!!) {
                PluginData.GroupSearchTimeout[GroupId] = System.currentTimeMillis() / 1000 + PluginConfig.searchTimeoutSec
                PluginData.save()
                PluginData.GroupSearchTimeout[GroupId].toString() + "_" + "yes"
            } else {
                PluginData.GroupSearchTimeout[GroupId]?.minus(System.currentTimeMillis() / 1000).toString() + "_" + "no"
            }
        } catch (e:NullPointerException) {
            PluginData.GroupSearchTimeout[GroupId] = System.currentTimeMillis() / 1000 + PluginConfig.searchTimeoutSec
            PluginData.save()
            PluginData.GroupSearchTimeout[GroupId].toString() + "_" + "yes"
        }
    }

    suspend fun searchInNetease(Input: String): String {
        return try {
            if (Input.split(" ")[1] !in PluginConfig.bannedNickname) {
                requestServer(Input.split(" ")[0], Input.split(" ")[1])
            } else {
                "[NSP] 你不可以查询这个玩家"
            }
        } catch (e: IndexOutOfBoundsException) {
            """
            [NSP] 请检查命令格式，通常为：
            .nsp name obt Steve
            """.trimIndent()
        }
    }
    
    private suspend fun requestServer(serverName: String, nickName: String): String {
        if (getServerAddress(serverName) == "NOTFOUND") return "[NSP] 输入的服务器不正确，一般为obt"
        val neteaseUrl = "https://" + getServerAddress(serverName) + "/user/query/search-by-name"

        val reqClient = HttpClient(io.ktor.client.engine.okhttp.OkHttp)

        var finalReturnString: String
        try {
            val postData = reqClient.post<String>(neteaseUrl) {
                body = "{\"name\":\"$nickName\"}"
                header("Content-Type", "application/json;charset=utf-8")
                header("User-Agent", "okhttp/3.12.12")
            }
            val obj = JSONObject(postData)

            if (obj.getString("message").equals("请先登录")) return "[NSP] 接口被网易和谐了！插件报废了！"
            if (obj.getInt("code") == 0) {
                return postData
            } else {
                finalReturnString = if(obj.getString("message").equals("参数错误")) "[NSP] 请求异常：参数错误（可能是携带敏感词）" else "[NSP] 请求异常：" + obj.getString("message")
            }
            //return postData
        } catch (e: Exception) {
            finalReturnString = "[NSP] 请求异常，堆栈信息如下：\n${e.printStackTrace()}"
        }
        return finalReturnString
    }

    private fun getServerAddress(serverName: String): String {
        when (serverName) {
            "obt" -> return "g79mclobt.nie.netease.com"
            "expr1" -> return "g79mclexpr1.nie.netease.com"
            "android" -> return "g79mcladr.nie.netease.com"
            "ios" -> return "g79mclios.nie.netease.com"
            "qa" -> return "g79mcltest.nie.netease.com"
            "stress" -> return "g79mclstresshome.nie.netease.com:10443"
            "stress_new" -> return "g79mclstressnew.nie.netease.com:8080"
            "preobt" -> return "g79mclobt.nie.netease.com"
            "exp" -> return "g79mcltest.nie.netease.com"
            "myl" -> return "g79mclmoyinglong.nie.netease.com"
            "gaiya" -> return "g79mclgaiya.nie.netease.com"
            "taitan" -> return "g79mcltaitan.nie.netease.com"
            "sim_gray" -> return "g79mclsim.nie.netease.com"
            "gray" -> return "g79mclobtgray.nie.netease.com:9443"
            "home_graya" -> return "g79mclobt.nie.netease.com"
            "home_grayb" -> return "g79mclobt.nie.netease.com"
            "sim" -> return "g79mclsim.nie.netease.com"
            "sim_new" -> return "g79mclsim-webv2.nie.netease.com"
            else -> return "NOTFOUND"
        }
    }

    fun ts2d(timestampString: String): String {
        val formats = "yyyy-MM-dd HH:mm:ss"
        val timestamp = timestampString.toLong() * 1000
        return SimpleDateFormat(formats, Locale.CHINA).format(Date(timestamp))
    }

    // 将URL资源转换成InputStream
    fun urlRes2InputStream(Input: String): InputStream {
        val url = URL(Input)
        val connect = url.openConnection()
        return connect.getInputStream()
    }
}
