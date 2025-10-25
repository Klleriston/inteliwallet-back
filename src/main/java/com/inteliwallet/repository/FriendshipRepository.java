package com.inteliwallet.repository;

import com.inteliwallet.entity.Friendship;
import com.inteliwallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, String> {

    @Query("SELECT f.friend FROM Friendship f WHERE f.user.id = :userId")
    List<User> findFriendsByUserId(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
           "FROM Friendship f " +
           "WHERE (f.user.id = :userId AND f.friend.id = :friendId) " +
           "OR (f.user.id = :friendId AND f.friend.id = :userId)")
    Boolean areFriends(
        @Param("userId") String userId,
        @Param("friendId") String friendId
    );

    @Query("SELECT f FROM Friendship f " +
           "WHERE (f.user.id = :userId AND f.friend.id = :friendId) " +
           "OR (f.user.id = :friendId AND f.friend.id = :userId)")
    List<Friendship> findFriendship(
        @Param("userId") String userId,
        @Param("friendId") String friendId
    );
}