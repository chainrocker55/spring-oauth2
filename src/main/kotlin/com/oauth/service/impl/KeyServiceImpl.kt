package com.oauth.service.impl

import com.oauth.service.KeyService
import com.oauth.client.keymanagement.KMSApiService
import com.oauth.client.keymanagement.config.KMSConfig
import com.oauth.client.keymanagement.response.AuthCryptoInfoFile
import com.oauth.component.KeyMgmtComponent
import com.oauth.constant.CacheName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.security.KeyPair
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class KeyServiceImpl(
    private val keyMgmtComponent: KeyMgmtComponent,
    private val kmsApiService: KMSApiService,
    private val kmsConfig: KMSConfig,
) : KeyService {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
        private val ENC_PW_PATTERN = Pattern.compile("ENC\\(([^\\)]+)\\)")
    }

    @Cacheable(CacheName.PASSPHRASE_TOKEN_CRYPTO)
    override fun getPassPhraseCryptoFile(): AuthCryptoInfoFile {
        if (kmsConfig.mock) {
            return mockAuthCryptoInfoFile()
        }
        try {
            return kmsApiService.getCryptoFile(kmsConfig.passphraseKeyFilename)
        } catch (e: Exception) {
            logger.info("get crypto passphrase key error", e)
            throw e
        }
    }

    @Cacheable(CacheName.ACCESS_TOKEN_CRYPTO)
    override fun getAccessTokenCryptoFile(): AuthCryptoInfoFile {
        if (kmsConfig.mock) {
            return mockAuthCryptoInfoFile()
        }
        try {
            return kmsApiService.getCryptoFile(kmsConfig.accessTokenKeyFilename)
        } catch (e: Exception) {
            logger.info("get crypto access token key error", e)
            throw e
        }
    }

    @Cacheable(CacheName.REFRESH_TOKEN_CRYPTO)
    override fun getRefreshTokenCryptoFile(): AuthCryptoInfoFile {
        if (kmsConfig.mock) {
            return mockAuthCryptoInfoFile()
        }
        try {
            return kmsApiService.getCryptoFile(kmsConfig.refreshTokenKeyFilename)
        } catch (e: Exception) {
            logger.info("get crypto refresh token key error", e)
            throw e
        }
    }


    @Cacheable(CacheName.ACCESS_TOKEN_KEY_PAIR)
    override fun getAccessTokenKeypair(): KeyPair {
        val crypto = getAccessTokenCryptoFile()
        return keyMgmtComponent.decodeKeyPair(crypto.publicKey, crypto.privateKey)
    }

    @Cacheable(CacheName.REFRESH_TOKEN_KEY_PAIR)
    override fun getRefreshTokenKeypair(): KeyPair {
        val crypto = getRefreshTokenCryptoFile()
        return keyMgmtComponent.decodeKeyPair(crypto.publicKey, crypto.privateKey)
    }


    override fun decryptCipherIfHasBracket(value: String, cryptoInfo: AuthCryptoInfoFile): String {
        val matcher: Matcher = ENC_PW_PATTERN.matcher(value)
        var cipher = value
        if (matcher.matches()) {
            logger.debug("Decrypting encrypted for value : $value")
            cipher = matcher.group(1)
        }
        return keyMgmtComponent.decryptCipher(cipher, cryptoInfo)
    }


    private fun mockAuthCryptoInfoFile(): AuthCryptoInfoFile {
        return AuthCryptoInfoFile(
            publicKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw95qShPJ0MHmV2dO/pTi\n+Ri0d5wcJzM1RF1nIGIV67iBkUXLp8NS/XbmOZF3gse5Xf3Kj/6M2RajYLB/Ct7j\nqjHKMZTQmUK9tn5FRTUOMmOlvFvMP+eh4MmowkILcxxijdoloAF8lfIxtojHSb4c\n/xQVDEG2uz1VrSABCRMUBzht6CPuX0P28Ct2+OxLQHmrJOfFMWoJCSIJHJcY7rZN\nJuqraVE9vFVVBMYNdw7MGCDEVejzWatxapS+Ni6z5FplWvxRaS8CLRpepksZ9+fu\nOhRn50Q1Lba7mMaBycK87SszpuFY6x6uABbBuOk8zxFVYGUXZ9DNHs1fhsT9JjN6\njwIDAQAB\n-----END PUBLIC KEY-----",
            privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDD3mpKE8nQweZX\nZ07+lOL5GLR3nBwnMzVEXWcgYhXruIGRRcunw1L9duY5kXeCx7ld/cqP/ozZFqNg\nsH8K3uOqMcoxlNCZQr22fkVFNQ4yY6W8W8w/56HgyajCQgtzHGKN2iWgAXyV8jG2\niMdJvhz/FBUMQba7PVWtIAEJExQHOG3oI+5fQ/bwK3b47EtAeask58UxagkJIgkc\nlxjutk0m6qtpUT28VVUExg13DswYIMRV6PNZq3FqlL42LrPkWmVa/FFpLwItGl6m\nSxn35+46FGfnRDUttruYxoHJwrztKzOm4VjrHq4AFsG46TzPEVVgZRdn0M0ezV+G\nxP0mM3qPAgMBAAECggEBAIRJqNIFUW4MN61amfC50B5bQC/0oNMc8+ILj4ZAmPuO\nrvvV/vl8o4WLE21YQTZz/E9uBKyCSNCqSIgaTIOkDHG7lQstrkGE4SrrV4+piMAh\nqyNvgKC8qglvSeqBvsftKOcTqgvKVUHUxRy6fpbgrybcgma3z1ng0AfQsMJXP1Sr\n6KifpWP6Ul+X7jniOSXgDXuiSMVy6VwhfsC6owoBuEAR3X9g/ZfLQ/ud/o9V9sAc\noG8pMgarRpK5Qwauv2ZRL/aXpWPF/+f9f++mSuCPBPQ1XSMiDQ/J39ba50psqtEg\nH1SOiqZT2diCX2x4eHzLOAoJWHqBZpP/Rx9FZv49k9kCgYEA+t38zo7jUGWRrcLv\n/PdaDOGSi5a06+wXNTj/TV7kyYXCsu0nrZ8PDxQ8erAoJpHrWhs+LvPKfP99dMnf\nHhqi4NFLVKRGg3IIecMVGOOCM3jowK1yhbre/uYnlzlkfbUQLrwVizsm/KPeqNA3\nrt0bJZt3n7EpPPPH4o+aGSaUQ5UCgYEAx+BaWAbuI4gST8vty2bleTcLSPSgiuLa\nrgc5OQ8m3IfXMYWF8F9LlXcGGuRwzJkPoKGMT0HqG9tVn7/lngd7gV6lbREvnLgF\nA7Cwlbasr4h12t7h3GlM1ZGe96xpwr7XRevJLS/nZ7ig36P6peNF4r9WokOuzcLe\n+XFoLBfi/JMCgYARKwHzDNpHlTvb/Vw5HqBeA03pGZ42CwknjkgCfnz/wZSqdApk\nGl+Ic6UT7FxDD3dgyj5+LhbsaPun82y+faop4ojaOQfOVQ55br1R37f4V8zt6rDM\nsBY0sYGCGt5ir/J0kFCVH64yWfCR8vXslJsX/sS3/ALLdg921McB857v5QKBgDrA\nd83oO5e2J6vNBdkEJe+7flWzkn1AsYb2GJvtGD86rsafuP8vA8irc9/zxjRdyXou\nAlBJz8bgcgrDHXuWN3ePmpC0vIoUjWTISx+20ovGUN5FJZ7kVPFyHIgw5eWYsy6a\nYEgoDJ0waBlU2hho9eHw3EZWHsvcvC4WZ8iOPOqBAoGAH2NfcRglTL482qCna7Mp\nD3j+DQBnsFYLWKu5taywjauiTW6GM9t5xDF1DiHhwwlb3ne/g00vH299aetUV6S/\nDDkuMzxWnF2mC4I1/L/8vKtD2W5KZWMSlgCWjIeqoKVJucR1pzBkiW7qM34IQVPs\n7i0o3ON+An2OIB7fYBpXV4M=\n-----END PRIVATE KEY-----",
            salt = "53494289426EC11D",
            iv = "3C01D6A15E954663244C3DD3955926F1",
            secret = "Invx1234!"
        )
    }

}