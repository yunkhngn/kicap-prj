/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author yunkhngn
 */
public class UserRoleAudit {
    private Long id;
    private Long actorId;    
    private Long targetId;   
    private String oldRole;   
    private String newRole;   
    private LocalDateTime changedAt;

    public UserRoleAudit() {}

    public UserRoleAudit(Long id, Long actorId, Long targetId, String oldRole, String newRole, LocalDateTime changedAt) {
        this.id = id;
        this.actorId = actorId;
        this.targetId = targetId;
        this.oldRole = oldRole;
        this.newRole = newRole;
        this.changedAt = changedAt;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getOldRole() { return oldRole; }
    public void setOldRole(String oldRole) { this.oldRole = oldRole; }

    public String getNewRole() { return newRole; }
    public void setNewRole(String newRole) { this.newRole = newRole; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    @Override
    public String toString() {
        return "UserRoleAudit{" + "id=" + id + ", actorId=" + actorId + ", targetId=" + targetId + ", oldRole=" + oldRole + ", newRole=" + newRole + ", changedAt=" + changedAt + '}';
    }
    
    
}
