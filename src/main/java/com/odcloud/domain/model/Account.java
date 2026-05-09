package com.odcloud.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Long id;
    private String email;
    private String nickname;
    private String picture;
    private List<Group> groups;
    private List<Voucher> vouchers;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public List<Long> getGroupIds() {
        return groups.stream()
            .map(Group::getId)
            .collect(Collectors.toList());
    }

    public List<java.util.Map<String, Object>> getGroupsInfo() {
        return groups.stream()
            .map(group -> {
                java.util.Map<String, Object> groupInfo = new java.util.HashMap<>();
                groupInfo.put("id", group.getId());
                groupInfo.put("name", group.getName());
                return groupInfo;
            })
            .collect(Collectors.toList());
    }

    public void updateGroups(List<Group> groups) {
        this.groups = groups;
    }


    public void updatePicture(String picture) {
        this.picture = picture;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateModDt() {
        this.modDt = LocalDateTime.now();
    }

    public void updateVouchers(List<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

    public List<String> getVouchersInfo() {
        if (vouchers == null) {
            return List.of();
        }
        return vouchers.stream()
            .map(voucher -> voucher.getVoucherType().toString())
            .collect(Collectors.toList());
    }
}
