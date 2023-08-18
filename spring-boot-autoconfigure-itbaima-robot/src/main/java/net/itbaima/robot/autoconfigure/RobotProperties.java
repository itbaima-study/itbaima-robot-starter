package net.itbaima.robot.autoconfigure;

import net.itbaima.robot.autoconfigure.util.LoginType;
import net.itbaima.robot.autoconfigure.util.SignerType;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(RobotProperties.PREFIX)
public class RobotProperties {
    public static final String PREFIX = "itbaima.robot";

    private LoginType loginType = LoginType.QR_CODE;
    private Long username;
    private String password;
    private SignerConfig signer = new SignerConfig();
    private DataConfig data = new DataConfig();
    private BotConfiguration.HeartbeatStrategy strategy = BotConfiguration.HeartbeatStrategy.STAT_HB;
    private BotConfiguration.MiraiProtocol protocol = BotConfiguration.MiraiProtocol.ANDROID_WATCH;

    public String getPassword() {
        return password;
    }

    public Long getUsername() {
        return username;
    }

    public BotConfiguration.HeartbeatStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(BotConfiguration.HeartbeatStrategy strategy) {
        this.strategy = strategy;
    }

    public BotConfiguration.MiraiProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(BotConfiguration.MiraiProtocol protocol) {
        this.protocol = protocol;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(Long username) {
        this.username = username;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public SignerConfig getSigner() {
        return signer;
    }

    public void setSigner(SignerConfig signer) {
        this.signer = signer;
    }

    public DataConfig getData() {
        return data;
    }

    public void setData(DataConfig data) {
        this.data = data;
    }

    public static class DataConfig {
        private String workDir = "robot-data";
        private String cacheDir = "cache";
        private boolean saveDeviceId = true;

        public boolean isContactCache() {
            return contactCache;
        }

        public void setContactCache(boolean contactCache) {
            this.contactCache = contactCache;
        }

        private boolean contactCache = true;

        public String getWorkDir() {
            return workDir;
        }

        public void setWorkDir(String workDir) {
            this.workDir = workDir;
        }

        public String getCacheDir() {
            return cacheDir;
        }

        public void setCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
        }

        public boolean isSaveDeviceId() {
            return saveDeviceId;
        }

        public void setSaveDeviceId(boolean saveDeviceId) {
            this.saveDeviceId = saveDeviceId;
        }
    }

    public static class SignerConfig {
        private String version;
        private SignerType type;
        private String url;
        private String key = "114514";
        private String serverIdentityKey = "vivo50";
        private String authorizationKey = "kfc";

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public SignerType getType() {
            return type;
        }

        public void setType(SignerType type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getServerIdentityKey() {
            return serverIdentityKey;
        }

        public void setServerIdentityKey(String serverIdentityKey) {
            this.serverIdentityKey = serverIdentityKey;
        }

        public String getAuthorizationKey() {
            return authorizationKey;
        }

        public void setAuthorizationKey(String authorizationKey) {
            this.authorizationKey = authorizationKey;
        }
    }
}
