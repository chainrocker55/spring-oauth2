package com.oauth.data.entity

import com.oauth.data.enum.LoginStatus
import com.oauth.data.enum.LoginType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "login_history")
class LoginHistoryEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "user_id", nullable = false)
    val userId: Int,

    @Column(name = "external_customer_id", nullable = true)
    val externalCustomerId: String? = null,

    @Column(name = "source_ip", nullable = true)
    val sourceIp: String? = null,

    @Column(name = "browser", nullable = true)
    val browser: String? = null,

    @Column(name = "browser_version", nullable = true)
    val browserVersion: String? = null,

    @Column(name = "device_type", nullable = true)
    var deviceType: String? = null,

    @Column(name = "device_os", nullable = true)
    val deviceOs: String? = null,

    @Column(name = "is_mobile", nullable = false)
    val isMobile: Boolean,

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var loginType: LoginType,

    @Column(name = "login_status", nullable = false)
    @Enumerated(EnumType.STRING)
    var loginStatus: LoginStatus,

    @Column(name = "error_message", nullable = true)
    var errorMessage: String? = null,

    @Column(name = "raw_data", nullable = true)
    var rawData: String? = null,

    @Column(name = "client_id", nullable = false)
    var clientId: String,

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

