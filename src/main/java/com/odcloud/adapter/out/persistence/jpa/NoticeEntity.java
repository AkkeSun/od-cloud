package com.odcloud.adapter.out.persistence.jpa;

import com.odcloud.domain.model.Notice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTICE")
class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GROUP_ID")
    private String groupId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "WRITER_EMAIL")
    private String writerEmail;

    @Column(name = "REG_DT")
    private LocalDateTime regDt;

    @Column(name = "MOD_DT")
    private LocalDateTime modDt;

    static NoticeEntity of(Notice notice) {
        return NoticeEntity.builder()
            .id(notice.getId())
            .groupId(notice.getGroupId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .writerEmail(notice.getWriterEmail())
            .regDt(notice.getRegDt())
            .modDt(notice.getModDt())
            .build();
    }

    public Notice toDomain() {
        return Notice.builder()
            .id(id)
            .groupId(groupId)
            .title(title)
            .content(content)
            .writerEmail(writerEmail)
            .regDt(regDt)
            .modDt(modDt)
            .build();
    }
}
