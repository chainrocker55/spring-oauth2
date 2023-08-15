package com.oauth.data.repository

import com.oauth.data.entity.LoginHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoginHistoryRepository : JpaRepository<LoginHistoryEntity,Long> {
}