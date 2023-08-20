package xyz.cssxsh.mirai.tool.sign.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.job
import net.mamoe.mirai.internal.spi.EncryptService
import net.mamoe.mirai.internal.spi.EncryptServiceContext
import net.mamoe.mirai.internal.utils.*
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiLogger
import net.mamoe.mirai.utils.Services
import xyz.cssxsh.mirai.tool.sign.service.impl.MagicSignerGuide
import xyz.cssxsh.mirai.tool.sign.service.impl.UnidbgFetchQsign
import java.net.ConnectException
import java.net.URL

/**
 * 原名: KFCFactory
 */
class SignServiceFactory : EncryptService.Factory {

    companion object {
        @JvmStatic
        internal var signServiceConfig: SignServiceConfig? = null

        @JvmStatic
        internal var workDir: String = ""

        @JvmStatic
        internal val logger: MiraiLogger = MiraiLogger.Factory.create(SignServiceFactory::class)

        @JvmStatic
        fun install() {
            Services.register(
                EncryptService.Factory::class.qualifiedName!!,
                SignServiceFactory::class.qualifiedName!!,
                ::SignServiceFactory
            )
        }

        @JvmStatic
        internal val created: MutableSet<Long> = java.util.concurrent.ConcurrentHashMap.newKeySet()

        @JvmStatic
        fun initConfiguration(path: String, config: SignServiceConfig) {
            workDir = "$path/"
            signServiceConfig = config
        }
    }

    override fun createForBot(context: EncryptServiceContext, serviceSubScope: CoroutineScope): EncryptService {
        if (created.add(context.id).not()) {
            throw UnsupportedOperationException("repeated create EncryptService")
        }
        serviceSubScope.coroutineContext.job.invokeOnCompletion {
            created.remove(context.id)
        }
        try {
            org.asynchttpclient.Dsl.config()
        } catch (cause: NoClassDefFoundError) {
            throw RuntimeException("请参照 https://search.maven.org/artifact/org.asynchttpclient/async-http-client/2.12.3/jar 添加依赖", cause)
        }
        return when (val protocol = context.extraArgs[EncryptServiceContext.KEY_BOT_PROTOCOL]) {
            BotConfiguration.MiraiProtocol.ANDROID_PHONE, BotConfiguration.MiraiProtocol.ANDROID_PAD -> {
                @Suppress("INVISIBLE_MEMBER")
                val version = MiraiProtocolInternal[protocol].ver
                val config = signServiceConfig!!
                when (val type = config.type.ifEmpty { throw IllegalArgumentException("need server type") }) {
                    "fuqiuluo/unidbg-fetch-qsign", "fuqiuluo", "unidbg-fetch-qsign" -> {
                        try {
                            val about = URL(config.base).readText()
                            logger.info("unidbg-fetch-qsign by ${config.base} about " + about.replace("\n", "").replace(" ", ""))
                            when {
                                "version" !in about -> {
                                    // 低于等于 1.1.3 的的版本 requestToken 不工作
                                    System.setProperty(UnidbgFetchQsign.REQUEST_TOKEN_INTERVAL, "0")
                                    logger.warning("请更新 unidbg-fetch-qsign")
                                }
                                version !in about -> {
                                    throw IllegalStateException("unidbg-fetch-qsign by ${config.base} 的版本与 ${protocol}(${version}) 似乎不匹配")
                                }
                            }
                        } catch (cause: ConnectException) {
                            throw RuntimeException("请检查 unidbg-fetch-qsign by ${config.base} 的可用性", cause)
                        } catch (cause: java.io.FileNotFoundException) {
                            throw RuntimeException("请检查 unidbg-fetch-qsign by ${config.base} 的可用性", cause)
                        }
                        UnidbgFetchQsign(
                            server = config.base,
                            key = config.key,
                            coroutineContext = serviceSubScope.coroutineContext
                        )
                    }
                    "kiliokuara/magic-signer-guide", "kiliokuara", "magic-signer-guide", "vivo50" -> {
                        try {
                            val about = URL(config.base).readText()
                            logger.info("magic-signer-guide by ${config.base} about \n" + about)
                            when {
                                "void" == about.trim() -> {
                                    logger.warning("请更新 magic-signer-guide 的 docker 镜像")
                                }
                                version !in about -> {
                                    throw IllegalStateException("magic-signer-guide by ${config.base} 与 ${protocol}(${version}) 似乎不匹配")
                                }
                            }
                        } catch (cause: ConnectException) {
                            throw RuntimeException("请检查 magic-signer-guide by ${config.base} 的可用性", cause)
                        } catch (cause: java.io.FileNotFoundException) {
                            throw RuntimeException("请检查 unidbg-fetch-qsign by ${config.base} 的可用性", cause)
                        }
                        MagicSignerGuide(
                            server = config.base,
                            serverIdentityKey = config.serverIdentityKey,
                            authorizationKey = config.authorizationKey,
                            coroutineContext = serviceSubScope.coroutineContext
                        )
                    }
                    else -> throw UnsupportedOperationException(type)
                }
            }

            else -> throw UnsupportedOperationException(protocol.name)
        }
    }

    override fun toString(): String {
        return "EncryptServiceFactory(config=$signServiceConfig)"
    }
}
