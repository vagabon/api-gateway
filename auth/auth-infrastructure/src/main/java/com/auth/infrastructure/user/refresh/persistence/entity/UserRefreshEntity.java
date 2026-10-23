package com.auth.infrastructure.user.refresh.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.auth.infrastructure.user.user.persistence.entity.UserEntity;
import com.base.infrastructure.persistence.entity.BaseEntity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_refresh")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserRefreshEntity extends BaseEntity {

    @Column(unique = true)
    private String token;

    private String ipAddress;

    private Boolean revoked;

    private Instant expiredDate;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
