package com.nilsson.service;

import com.nilsson.entity.Member;
import com.nilsson.entity.MembershipLevel;
import com.nilsson.exception.ResourceNotFoundException;
import com.nilsson.repo.MemberRepository;
import java.util.List;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void addMember(Member member) {
        memberRepository.addMember(member);
    }

    public Member addMember(String firstName, String lastName, MembershipLevel membershipLevel) {
        // Validation logic
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name required");

        Member member = new Member(firstName, lastName, membershipLevel);
        memberRepository.addMember(member);
        return member;
    }

    public void saveNewMember(Member member) {
        memberRepository.addMember(member);
    }

    public Member getMember(Long id) {
        Member member = memberRepository.getMember(id);
        if (member == null) {
            throw new ResourceNotFoundException("Member not found with ID: " + id);
        }
        return member;
    }

    public List<Member> getAllMembers() {
        return memberRepository.getAllMembers();
    }

    public void updateMember(Member member) {
        memberRepository.updateMember(member);
    }

    public void deleteMember(Member member) {
        memberRepository.deleteMember(member);
    }
}