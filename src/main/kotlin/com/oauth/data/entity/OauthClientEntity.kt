package com.oauth.data.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "oauth_client")
class OauthClientEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: String,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "secret", nullable = true)
    val secret: String? = null,

    @Column(name = "redirect_url", nullable = false)
    val redirectUrl: String,

    @Column(name = "is_enabled", nullable = false)
    val isEnabled: Boolean,

    @Column(name = "device_grant_enable", nullable = false)
    val deviceGrantEnable: Boolean,

    @Column(name = "password_grant_enable", nullable = false)
    val passwordGrantEnable: Boolean,

    @Column(name = "client_credentials_grant_enable", nullable = false)
    val clientCredentialsGrantEnable: Boolean,

    @Column(name = "implicit_flow_enable", nullable = false)
    val implicitFlowEnable: Boolean,

    @Column(name = "refresh_token_grant_enable", nullable = false)
    val refreshTokenGrantEnable: Boolean,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    val clientScope: List<ClientScopeEntity>,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "datetime")
    val createdAt: Date? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "datetime")
    var updatedAt: Date? = null,
) {
}

