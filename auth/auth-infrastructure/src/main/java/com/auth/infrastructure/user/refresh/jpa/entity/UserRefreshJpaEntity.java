package com.auth.infrastructure.user.refresh.jpa.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.auth.infrastructure.user.user.persistence.entity.UserEntity;
import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

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
public class UserRefreshJpaEntity extends BaseCrudJpaEntity {

    @Column(unique = true)
    private String token;

    private String ipAddress;

    private Boolean revoked;

    private Instant expiredDate;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
