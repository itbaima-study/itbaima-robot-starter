package xyz.cssxsh.mirai.tool

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Cola(
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
