package com.nilsson.repo;
import com.nilsson.entity.Member;
import java.util.List;

public interface MemberRepository {
    void addMember(Member member);
    Member getMember(Long id);
    List<Member> getAllMembers();
    void updateMember(Member member);
    void deleteMember(Member member);
}