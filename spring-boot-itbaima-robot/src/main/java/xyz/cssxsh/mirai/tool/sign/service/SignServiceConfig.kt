package xyz.cssxsh.mirai.tool.sign.service

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class SignServiceConfig(
    @SerialName("base_url")
    val base: String,
    @SerialName("type")
    val type: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("server_identity_key")
    @JsonNames("serverIdentityKey")
    val serverIdentityKey: String = "",
    @SerialName("authorization_key")
    @JsonNames("authorizationKey")
    val authorizationKey: String = ""
)
