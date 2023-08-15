package com.oauth.service.impl

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.oauth.client.keymanagement.KMSApiService
import com.oauth.client.keymanagement.config.KMSConfig
import com.oauth.client.keymanagement.response.AuthCryptoInfoFile
import com.oauth.client.keymanagement.response.KmsKeyPair
import com.oauth.component.KeyMgmtComponent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.security.KeyPair

@Import(value = [KeyServiceImpl::class])
@ExtendWith(SpringExtension::class)
@ActiveProfiles(profiles = ["test"])
class KeyServiceImplTest @Autowired constructor(private val keyService: KeyServiceImpl){
    @MockBean
    lateinit var keyMgmtComponent: KeyMgmtComponent
    @MockBean
    lateinit var kmsApiService: KMSApiService
    @MockBean
    lateinit var kmsConfig: KMSConfig


    private val keyUtil = KeyMgmtComponent()
    private var keyPair: KeyPair? = null;

    companion object{
        const val FILE_NAME = "FILE_NAME"
        private val AUTH_CRYPTO_FILE: AuthCryptoInfoFile = AuthCryptoInfoFile(
            publicKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA47EihvWuHE5+9T/E+g99\nnDS8bQWMEhXuIe3mE3jwXrA9HaER6uW1kl1ZZjhQ0tErrqcifsR9D6zpiO7V1LC4\nNbfpkhYi+k1AWJk2LLg/bJxYNuUsbXVCfTV2Fx81TjvEB0+aiVIcySwDdSTv1RlG\ns1hPH7AcA08z3nO785tVhCpoHXKT/xR2LUMu2PdPvfmYmXmK/e2dkGrHzNv84ijn\nS8iczfwlXB5CIN6XbbXl/H8MHOM46NwoyhCo2m8RGloal4zMpVkXClqzhFh9/2BO\nBWib15XpPWwoSOLZvVdiIdX9p92ScVVksIfPCMPHFrgXlqgDtXEEJ9JE8pJLAsxp\nKwIDAQAB\n-----END PUBLIC KEY-----",
            privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDjsSKG9a4cTn71\nP8T6D32cNLxtBYwSFe4h7eYTePBesD0doRHq5bWSXVlmOFDS0SuupyJ+xH0PrOmI\n7tXUsLg1t+mSFiL6TUBYmTYsuD9snFg25SxtdUJ9NXYXHzVOO8QHT5qJUhzJLAN1\nJO/VGUazWE8fsBwDTzPec7vzm1WEKmgdcpP/FHYtQy7Y90+9+ZiZeYr97Z2QasfM\n2/ziKOdLyJzN/CVcHkIg3pdtteX8fwwc4zjo3CjKEKjabxEaWhqXjMylWRcKWrOE\nWH3/YE4FaJvXlek9bChI4tm9V2Ih1f2n3ZJxVWSwh88Iw8cWuBeWqAO1cQQn0kTy\nkksCzGkrAgMBAAECggEBAJwTxFM2/c6xkB2VRPUUwUnebHtfHhEXr1DavqgCcuK1\ndG9nBd068SgPjUSgveaFM6hZMTAsiH3CTaNr9HADY4KPOzDEs91YLw+MlrI46mEN\nGvsmRToS8yzZBMX8wpGKLmnEi24uN0U/Qm9+jlhJJ0mw8ywJkGpEVI3cT65BgHFD\nRXNT169boEjHR9+5aRY5Ll6Ie5ZlexSE716SMhS+s1X4PorVuZVIWmx9JIkSXTy8\n74iEDcT5BYGtn0DPOB73j2INHHF+v9/jB4JalW6Ka2IIPaoEIKHEhRcYWPg2q1bO\nZ9Ifgj0i2BQQJTsbwCA39Yw/h8o3CELs01mhhSMkoOECgYEA8qaZ5NwsaM8Itk3P\nbehR/pcaw9c4I32Omaq4Hg84cyJTXAWaoA/DimrO/02d747/6X3+H/8r19zM7X57\n1qkMlZp8hDqhWdU854JL3oq7MUjTqtN22xeU/82A4ITyND/I9ObkK2vyBNdcfABO\nlgLnTTTUAZjKcbhXE7jONZzOMncCgYEA8Dfb7S+ypdXO+GyDZUo7vJy882xz3f3M\nXJz9u3QiZSMIUT7LZY1Cw0NRcL9W0MEXwz36GbRtCWgPpGRHVUI8IxQyXlpJrn2E\nPKAjDa5n7AvFPmluP9+mqK2LUlGtB1RxvwMUr1Hyfch7kygud1QvfWH1cmiEF+Is\nuOHAzupZF+0CgYEA7tod/JDaSiUZn1JCkB6yr5gQKZztzsmmGA9bg4NZ/sNYKOn/\nRfWedt8ieV7uy0QJFs8F2ns0rhzTwCfUQP5Qu+bBEOwhrargc8bnZd1L3HyilrHn\nekzhR1gDt35GaDYtWxz4luF/71IK4KjwsmuTQYVx90xuYrihRnGQ0mdS9YsCgYAp\nXhU7lVk4gcdwtI80JEKBSqSnbufNNOW6lTaEz40UyaGnDB9Oz7LNmYwu1GSI8pe7\nAbNONeoJEqiO8M3v+sYMbG/4G6hQqkDZh+JBeuVemvBOvS1FRqiWp7w0YuhfOMWV\n1Vv8jSorSxks0GCgIBlFiFcSarlEu6hj9kyT1XueSQKBgQCcyVTTqmCVNy61Vv6n\nk8qczRF2HrKfSDg1OBN1Ytzd59WEJCGJEmevJDuarV69jVIUIafbBynlqlhmh/LS\nhLzU716nXwJ/xShdrVxMWH2YOXL2nEL21O2amYN+BtWeNxLwFlzA85L5oA0iFZ61\n5e0lS4t01HL17maJgDEN9EuCgQ==\n-----END PRIVATE KEY-----",
            salt = "D05E39EB8A3F2D4E",
            iv = "954B7EA624620458C91EA751AF63C08D",
            secret = "invx1234!"
        )
        private val KEY_PAIR: KmsKeyPair = KmsKeyPair(
            publicKey = "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA47EihvWuHE5+9T/E+g99\nnDS8bQWMEhXuIe3mE3jwXrA9HaER6uW1kl1ZZjhQ0tErrqcifsR9D6zpiO7V1LC4\nNbfpkhYi+k1AWJk2LLg/bJxYNuUsbXVCfTV2Fx81TjvEB0+aiVIcySwDdSTv1RlG\ns1hPH7AcA08z3nO785tVhCpoHXKT/xR2LUMu2PdPvfmYmXmK/e2dkGrHzNv84ijn\nS8iczfwlXB5CIN6XbbXl/H8MHOM46NwoyhCo2m8RGloal4zMpVkXClqzhFh9/2BO\nBWib15XpPWwoSOLZvVdiIdX9p92ScVVksIfPCMPHFrgXlqgDtXEEJ9JE8pJLAsxp\nKwIDAQAB\n-----END PUBLIC KEY-----",
            privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDjsSKG9a4cTn71\nP8T6D32cNLxtBYwSFe4h7eYTePBesD0doRHq5bWSXVlmOFDS0SuupyJ+xH0PrOmI\n7tXUsLg1t+mSFiL6TUBYmTYsuD9snFg25SxtdUJ9NXYXHzVOO8QHT5qJUhzJLAN1\nJO/VGUazWE8fsBwDTzPec7vzm1WEKmgdcpP/FHYtQy7Y90+9+ZiZeYr97Z2QasfM\n2/ziKOdLyJzN/CVcHkIg3pdtteX8fwwc4zjo3CjKEKjabxEaWhqXjMylWRcKWrOE\nWH3/YE4FaJvXlek9bChI4tm9V2Ih1f2n3ZJxVWSwh88Iw8cWuBeWqAO1cQQn0kTy\nkksCzGkrAgMBAAECggEBAJwTxFM2/c6xkB2VRPUUwUnebHtfHhEXr1DavqgCcuK1\ndG9nBd068SgPjUSgveaFM6hZMTAsiH3CTaNr9HADY4KPOzDEs91YLw+MlrI46mEN\nGvsmRToS8yzZBMX8wpGKLmnEi24uN0U/Qm9+jlhJJ0mw8ywJkGpEVI3cT65BgHFD\nRXNT169boEjHR9+5aRY5Ll6Ie5ZlexSE716SMhS+s1X4PorVuZVIWmx9JIkSXTy8\n74iEDcT5BYGtn0DPOB73j2INHHF+v9/jB4JalW6Ka2IIPaoEIKHEhRcYWPg2q1bO\nZ9Ifgj0i2BQQJTsbwCA39Yw/h8o3CELs01mhhSMkoOECgYEA8qaZ5NwsaM8Itk3P\nbehR/pcaw9c4I32Omaq4Hg84cyJTXAWaoA/DimrO/02d747/6X3+H/8r19zM7X57\n1qkMlZp8hDqhWdU854JL3oq7MUjTqtN22xeU/82A4ITyND/I9ObkK2vyBNdcfABO\nlgLnTTTUAZjKcbhXE7jONZzOMncCgYEA8Dfb7S+ypdXO+GyDZUo7vJy882xz3f3M\nXJz9u3QiZSMIUT7LZY1Cw0NRcL9W0MEXwz36GbRtCWgPpGRHVUI8IxQyXlpJrn2E\nPKAjDa5n7AvFPmluP9+mqK2LUlGtB1RxvwMUr1Hyfch7kygud1QvfWH1cmiEF+Is\nuOHAzupZF+0CgYEA7tod/JDaSiUZn1JCkB6yr5gQKZztzsmmGA9bg4NZ/sNYKOn/\nRfWedt8ieV7uy0QJFs8F2ns0rhzTwCfUQP5Qu+bBEOwhrargc8bnZd1L3HyilrHn\nekzhR1gDt35GaDYtWxz4luF/71IK4KjwsmuTQYVx90xuYrihRnGQ0mdS9YsCgYAp\nXhU7lVk4gcdwtI80JEKBSqSnbufNNOW6lTaEz40UyaGnDB9Oz7LNmYwu1GSI8pe7\nAbNONeoJEqiO8M3v+sYMbG/4G6hQqkDZh+JBeuVemvBOvS1FRqiWp7w0YuhfOMWV\n1Vv8jSorSxks0GCgIBlFiFcSarlEu6hj9kyT1XueSQKBgQCcyVTTqmCVNy61Vv6n\nk8qczRF2HrKfSDg1OBN1Ytzd59WEJCGJEmevJDuarV69jVIUIafbBynlqlhmh/LS\nhLzU716nXwJ/xShdrVxMWH2YOXL2nEL21O2amYN+BtWeNxLwFlzA85L5oA0iFZ61\n5e0lS4t01HL17maJgDEN9EuCgQ==\n-----END PRIVATE KEY-----",
        )
    }

    @BeforeEach
    fun setUp(){
        whenever(kmsConfig.accessTokenKeyFilename).thenReturn(FILE_NAME)
        whenever(kmsConfig.refreshTokenKeyFilename).thenReturn(FILE_NAME)
        whenever(kmsConfig.passphraseKeyFilename).thenReturn(FILE_NAME)
        keyPair = keyUtil.decodeKeyPair(AUTH_CRYPTO_FILE.publicKey, AUTH_CRYPTO_FILE.privateKey)
    }

    @Test
    fun `get access token crypto file success`(){
        whenever(kmsConfig.mock).thenReturn(false)
        whenever(kmsApiService.getCryptoFile(any())).thenReturn(AUTH_CRYPTO_FILE)
        val result = keyService.getAccessTokenCryptoFile()
        assertNotNull(result)
        verify(kmsApiService, Mockito.times(1)).getCryptoFile(any())
    }

    @Test
    fun `get refresh token crypto file success`(){
        whenever(kmsConfig.mock).thenReturn(false)
        whenever(kmsApiService.getCryptoFile(any())).thenReturn(AUTH_CRYPTO_FILE)
        val result = keyService.getRefreshTokenCryptoFile()
        assertNotNull(result)
        verify(kmsApiService, Mockito.times(1)).getCryptoFile(any())
    }

    @Test
    fun `get passphrase token crypto file success`(){
        whenever(kmsConfig.mock).thenReturn(false)
        whenever(kmsApiService.getCryptoFile(any())).thenReturn(AUTH_CRYPTO_FILE)
        val result = keyService.getPassPhraseCryptoFile()
        assertNotNull(result)
        verify(kmsApiService, Mockito.times(1)).getCryptoFile(any())
    }

    @Test
    fun `get access token crypto file error`(){
        whenever(kmsConfig.mock).thenReturn(false)
        whenever(kmsApiService.getCryptoFile(any())).thenThrow(RuntimeException::class.java)
        assertThrows<RuntimeException> { keyService.getAccessTokenCryptoFile() }
        verify(kmsApiService, Mockito.times(1)).getCryptoFile(any())
    }

    @Test
    fun `get refresh token crypto file error`(){
        whenever(kmsConfig.mock).thenReturn(false)
        whenever(kmsApiService.getCryptoFile(any())).thenThrow(RuntimeException::class.java)
        assertThrows<RuntimeException> { keyService.getRefreshTokenCryptoFile() }
        verify(kmsApiService, Mockito.times(1)).getCryptoFile(any())
    }

    @Test
    fun `get passphrase token crypto file error`(){
        whenever(kmsConfig.mock).thenReturn(false)
        whenever(kmsApiService.getCryptoFile(any())).thenThrow(RuntimeException::class.java)
        assertThrows<RuntimeException> { keyService.getPassPhraseCryptoFile() }
        verify(kmsApiService, Mockito.times(1)).getCryptoFile(any())
    }

    @Test
    fun `test decrypt if has bracket`(){
        val expect = "value"
        whenever(keyMgmtComponent.decryptCipher(any(), any())).thenReturn(expect)
        val encKey = "ENC($expect)"
        val result = keyService.decryptCipherIfHasBracket(encKey, AUTH_CRYPTO_FILE)
        Assertions.assertEquals(expect, result)
        verify(keyMgmtComponent, Mockito.times(1)).decryptCipher(any(), any())
    }
    @Test
    fun `test decrypt if not has bracket`(){
        val expect = "value"
        whenever(keyMgmtComponent.decryptCipher(any(), any())).thenReturn(expect)
        val result = keyService.decryptCipherIfHasBracket(expect, AUTH_CRYPTO_FILE)
        Assertions.assertEquals(expect, result)
        verify(keyMgmtComponent, Mockito.times(1)).decryptCipher(any(), any())
    }


}