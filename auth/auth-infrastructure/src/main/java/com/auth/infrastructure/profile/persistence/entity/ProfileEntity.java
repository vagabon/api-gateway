package com.auth.infrastructure.profile.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.base.infrastructure.persistence.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProfileEntity extends BaseEntity {

    private String name;
    private String roles;

    private Instant endPlan;

}