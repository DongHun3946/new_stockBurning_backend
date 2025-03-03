package com.mysite.stockburning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass //Entity 클래스에서 일반 클래스를 상속받기 위해서 작성
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate //엔티티가 처음 저장될 때 현재 날짜 및 시간이 자동으로 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate //엔티티가 수정될 때마다 현재 날짜 및 시간이 자동으로 저장
    private LocalDateTime updatedAt;
}
