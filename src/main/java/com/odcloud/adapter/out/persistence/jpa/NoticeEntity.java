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
@Table(name = "notice")
class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "writer_email")
    private String writerEmail;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Column(name = "mod_dt")
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
