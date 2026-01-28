package com.nilsson.repo;
import com.nilsson.entity.Member;
import java.util.List;

public interface MemberRepository {
    void save(Member member);
    Member getMember(Long id);
    List<Member> getAllMembers();
    void update(Member member);
    void delete(Member member);
}