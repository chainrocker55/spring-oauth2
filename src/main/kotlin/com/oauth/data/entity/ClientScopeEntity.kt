package com.oauth.data.entity

import com.oauth.data.entity.ScopeEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
@Table(name = "client_scope")
class ClientScopeEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "client_id", nullable = false)
    val clientId: String,

    @OneToOne
    @JoinColumn(name = "scope_id", referencedColumnName = "id")
    val scopes: ScopeEntity,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "datetime")
    val createdAt: Date? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", columnDefinition = "datetime")
    var updatedAt: Date? = null,
    )

