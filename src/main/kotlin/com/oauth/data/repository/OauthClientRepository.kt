package com.oauth.data.repository

import com.oauth.data.entity.OauthClientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthClientRepository : JpaRepository<OauthClientEntity, String> {
}