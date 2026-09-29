package cn.duckflew.education.messaging;

import cn.duckflew.education.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(name = "from_user_id")
    private Long fromUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "related_user_id")
    private Long relatedUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "anchor_type")
    private NotificationAnchor anchorType;

    @Column(name = "anchor_id")
    private Long anchorId;

    @Column(name = "anchor_ref_id")
    private Long anchorRefId;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    public static Notification of(Long toUserId, Long fromUserId, NotificationType type, Long resourceId, Long relatedUserId) {
        return of(toUserId, fromUserId, type, resourceId, relatedUserId, null, null, null);
    }

    public static Notification of(Long toUserId, Long fromUserId, NotificationType type, Long resourceId,
                                  Long relatedUserId, NotificationAnchor anchorType, Long anchorId, Long anchorRefId) {
        Notification n = new Notification();
        n.setToUserId(toUserId);
        n.setFromUserId(fromUserId);
        n.setType(type);
        n.setResourceId(resourceId);
        n.setRelatedUserId(relatedUserId);
        n.setAnchorType(anchorType);
        n.setAnchorId(anchorId);
        n.setAnchorRefId(anchorRefId);
        return n;
    }
}
