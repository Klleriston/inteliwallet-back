package com.inteliwallet.repository;

import com.inteliwallet.entity.FriendInvite;
import com.inteliwallet.entity.FriendInvite.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendInviteRepository extends JpaRepository<FriendInvite, String> {

    List<FriendInvite> findByToUserIdAndStatus(String toUserId, InviteStatus status);

    List<FriendInvite> findByFromUserIdAndStatus(String fromUserId, InviteStatus status);

    @Query("SELECT COUNT(i) > 0 " +
            "FROM FriendInvite i " +
            "WHERE ((i.fromUser.id = :userId AND i.toUser.id = :friendId) " +
            "OR (i.fromUser.id = :friendId AND i.toUser.id = :userId)) " +
            "AND i.status = 'PENDING'")
    Boolean existsPendingInvite(
            @Param("userId") String userId,
            @Param("friendId") String friendId
    );

    Optional<FriendInvite> findByFromUserIdAndToUserIdAndStatus(
        String fromUserId,
        String toUserId,
        InviteStatus status
    );
}