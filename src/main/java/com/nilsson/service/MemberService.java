package com.nilsson.service;

import com.nilsson.entity.Member;
import com.nilsson.entity.MembershipLevel;
import com.nilsson.entity.Rental;
import com.nilsson.exception.InvalidMemberDataException;
import com.nilsson.exception.MemberActiveException;
import com.nilsson.exception.ResourceNotFoundException;
import com.nilsson.repo.MemberRepository;
import com.nilsson.repo.RentalRepository;
import com.nilsson.util.LanguageManager;

import java.util.List;

public class MemberService {

    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;

    public MemberService(MemberRepository memberRepository, RentalRepository rentalRepository) {
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
    }

    public void addMember(Member member) {
        validateMember(member.getFirstName(), member.getLastName());
        memberRepository.addMember(member);
    }

    public Member addMember(String firstName, String lastName, MembershipLevel membershipLevel) {
        validateMember(firstName, lastName);

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
            throw new ResourceNotFoundException(LanguageManager.getInstance().getString("error.ResourceNotFoundException") + id);
        }
        return member;
    }

    private void validateMember(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidMemberDataException(LanguageManager.getInstance().getString("error.InvalidMemberFirstName"));
        }
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidMemberDataException(LanguageManager.getInstance().getString("error.InvalidMemberLastName"));
        }
    }

    public List<Member> getAllMembers() {
        return memberRepository.getAllMembers();
    }

    public void updateMember(Member member) {
        memberRepository.updateMember(member);
    }

    public void deleteMember(Member member) {
        List<Rental> memberRentals = rentalRepository.getRentalsByMemberId(member.getId());

        boolean hasActiveRentals = memberRentals.stream()
                .anyMatch(r -> r.getEndTime() == null);
        if (hasActiveRentals) {
            throw new MemberActiveException(
                    LanguageManager.getInstance().getString("error.memberHasActiveRentals")
            );
        }

        memberRepository.deleteMember(member);
    }
}