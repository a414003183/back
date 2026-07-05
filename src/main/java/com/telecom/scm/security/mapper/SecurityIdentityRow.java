package com.telecom.scm.security.mapper;

public class SecurityIdentityRow {

    private Long userId;
    private String identityType;
    private String displayName;
    private String status;
    private Boolean defaultIdentity;
    private Long memberId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void setIdentityType(String identityType) {
        this.identityType = identityType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDefaultIdentity() {
        return defaultIdentity;
    }

    public void setDefaultIdentity(Boolean defaultIdentity) {
        this.defaultIdentity = defaultIdentity;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
