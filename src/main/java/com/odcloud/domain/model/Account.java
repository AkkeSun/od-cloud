package com.odcloud.domain.model;

import com.odcloud.adapter.out.client.google.GoogleUserInfoResponse;
import com.odcloud.application.account.port.in.command.RegisterAccountCommand;
import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private String name;
    private String picture;
    private List<Group> groups;
    private List<Voucher> vouchers;
    private LocalDateTime modDt;
    private LocalDateTime regDt;

    public static Account of(Claims claims) {
        List<Map<String, Object>> groupsInfo =
            (List<Map<String, Object>>) claims.get("groups");
        return Account.builder()
            .email(claims.getSubject())
            .id(((Number) claims.get("id")).longValue())
            .nickname(claims.get("nickname").toString())
            .picture(claims.get("picture").toString())
            .groups(groupsInfo.stream()
                .map(groupInfo -> Group.builder()
                    .id(((Number) groupInfo.get("id")).longValue())
                    .name(groupInfo.get("name").toString())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }

    public static Account of(GoogleUserInfoResponse userInfo, RegisterAccountCommand command) {
        return Account.builder()
            .email(userInfo.email())
            .nickname(userInfo.name())
            .name(command.name())
            .picture(userInfo.picture())
            .regDt(LocalDateTime.now())
            .build();
    }

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

    public List<Map<String, Object>> getVouchersInfo() {
        if (vouchers == null) {
            return List.of();
        }
        return vouchers.stream()
            .map(voucher -> {
                Map<String, Object> voucherInfo = new java.util.HashMap<>();
                voucherInfo.put("voucherType", voucher.getVoucherType().name());
                voucherInfo.put("groupId", voucher.getGroupId());
                return voucherInfo;
            })
            .collect(Collectors.toList());
    }
}
