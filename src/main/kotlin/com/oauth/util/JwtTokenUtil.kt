package com.oauth.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

class JwtTokenUtil {
    fun decodeJwt(jwt: String, publicKey: PublicKey): Claims {
        return Jwts.parser()
            .setSigningKey(publicKey)
            .parseClaimsJws(jwt).body
    }
    fun encodeJwt(
        claims: MutableMap<String, Any>,
        id: String,
        expireTime: Date,
        subject: String = "auth",
        privateKey: PrivateKey,
        isUser: String,
        audience: String
    ): String {

        return Jwts.builder()
            .setId(id)
            .setSubject(subject)
            .setAudience(audience)
            .setExpiration(expireTime)
            .setIssuer(isUser)
            .setIssuedAt(Date())
            .signWith(SignatureAlgorithm.RS256, privateKey)
            .addClaims(claims)
            .compact()
    }
}