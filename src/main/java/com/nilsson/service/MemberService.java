package com.nilsson.service;

import com.nilsson.entity.Member;
import com.nilsson.repo.MemberRepository;
import java.util.List;

public class MemberService {

    private final MemberRepository memberRepository;

    // Dependency Injection via Constructor (Required by assignment)
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(String firstName, String lastName, String level) {
        // Validation logic
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name required");

        Member member = new Member(firstName, lastName, level);
        memberRepository.addMember(member);
        return member;
    }

    public Member getMember(Long id) {
        Member member = memberRepository.getMember(id);
        if (member == null) {
            // Use a custom exception here later as per assignment
            throw new RuntimeException("Member not found with ID: " + id);
        }
        return member;
    }

    public List<Member> getAllMembers() {
        return memberRepository.getAllMembers();
    }
}