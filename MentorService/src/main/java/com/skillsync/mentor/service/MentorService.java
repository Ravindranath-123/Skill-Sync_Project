package com.skillsync.mentor.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.skillsync.mentor.dto.*;
import com.skillsync.mentor.entity.*;
import com.skillsync.mentor.feign.SkillClient;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.repository.MentorSkillRepository;
import com.skillsync.mentor.specification.MentorSpecification;
import com.skillsync.mentor.specification.MentorSearchSpecification;

import lombok.extern.slf4j.Slf4j;

/*
 * ================================================================
 * AUTHOR: Ravindranath Potturu
 * CLASS: MentorService
 * DESCRIPTION:
 * Handles business logic for Mentor profile generation, updates,
 * skill association, and advanced search specifications.
 * ================================================================
 */
@Service
@Slf4j
public class MentorService {

	@Autowired
	private MentorRepository mentorRepository;

	@Autowired
	private MentorSkillRepository mentorSkillRepository;

	@Autowired
	private SkillClient skillClient;

	/* ================================================================
	 * METHOD: createProfile
	 * DESCRIPTION:
	 * Creates a new Mentor profile tied to a user ID. Validates input
	 * constraints and sets initial defaults like status and rating.
	 * ================================================================ */
	// ⭐ CREATE PROFILE (PENDING)
	public MentorProfileResponseDto createProfile(Long userId, CreateMentorProfileRequestDto request) {
		log.info("Creating mentor profile for user id: {}", userId);

		if (mentorRepository.existsByUserId(userId)) {
			log.error("Mentor profile already exists for user id: {}", userId);
			throw new RuntimeException("Mentor profile already exists");
		}

		if (request.getHourlyRate() < 0) {
			log.error("Hourly rate cannot be negative for user id: {}", userId);
			throw new RuntimeException("Hourly rate cannot be negative");
		}

		if (request.getExperienceYears() < 0) {
			log.error("Experience cannot be negative for user id: {}", userId);
			throw new RuntimeException("Experience cannot be negative");
		}

		Mentor mentor = Mentor.builder().userId(userId).bio(request.getBio())
				.experienceYears(request.getExperienceYears()).hourlyRate(request.getHourlyRate()).averageRating(0.0)
				.totalSessions(0).available(request.getAvailable()).createdAt(LocalDateTime.now())
				.status(MentorStatus.APPROVED).build();

		mentorRepository.save(mentor);

		return map(mentor, "Profile created successfully.");
	}

	/* ================================================================
	 * METHOD: updateProfile
	 * DESCRIPTION:
	 * Updates existing mentor profiles ensuring they are already approved.
	 * ================================================================ */
	// ⭐ UPDATE PROFILE
	public MentorProfileResponseDto updateProfile(Long userId, UpdateMentorProfileRequestDto request) {
		log.info("Updating mentor profile for user id: {}", userId);

		Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> {
					log.error("Mentor profile not found for user id: {}", userId);
					return new RuntimeException("Mentor profile not found");
				});

		if (mentor.getStatus() != MentorStatus.APPROVED) {
			log.error("Mentor not approved yet for user id: {}", userId);
			throw new RuntimeException("Mentor not approved yet");
		}

		if (request.getBio() != null)
			mentor.setBio(request.getBio());

		if (request.getExperienceYears() != null && request.getExperienceYears() >= 0)
			mentor.setExperienceYears(request.getExperienceYears());

		if (request.getHourlyRate() != null && request.getHourlyRate() >= 0)
			mentor.setHourlyRate(request.getHourlyRate());

		if (request.getAvailable() != null)
			mentor.setAvailable(request.getAvailable());

		mentorRepository.save(mentor);

		return map(mentor, "Profile updated");
	}

	/* ================================================================
	 * METHOD: addSkillToMentor
	 * DESCRIPTION:
	 * Validates and maps a certified skill to an approved mentor profile 
	 * through the mentor-skill repository.
	 * ================================================================ */
	// ⭐ ADD SKILL
	public String addSkillToMentor(Long userId, Long skillId) {
		log.info("Adding skill id: {} to mentor user id: {}", skillId, userId);

		Mentor mentor = mentorRepository.findByUserId(userId)
                .orElseThrow(() -> {
					log.error("Mentor profile not found for user id: {}", userId);
					return new RuntimeException("Mentor profile not found");
				});
                
        Long mentorId = mentor.getMentorId();

		if (mentor.getStatus() != MentorStatus.APPROVED)
			throw new RuntimeException("Mentor not approved yet");

		SkillDto skill = skillClient.getSkillById(skillId);

		if (!skill.getActive())
			throw new RuntimeException("Skill is inactive");

		boolean exists = mentorSkillRepository.findByMentorId(mentorId).stream()
				.anyMatch(m -> m.getSkillId().equals(skillId));

		if (exists)
			throw new RuntimeException("Skill already added");

		MentorSkill mapping = MentorSkill.builder().mentorId(mentorId).skillId(skillId).build();

		mentorSkillRepository.save(mapping);

		return "Skill added";
	}

	/* ================================================================
	 * METHOD: searchByPrice
	 * DESCRIPTION: Fetches paginated mentors filtered by an hourly rate range.
	 * ================================================================ */
	// ⭐ SEARCH BY PRICE
	public Page<Mentor> searchByPrice(Double min, Double max, int page, int size, String sortBy) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

		return mentorRepository.findByHourlyRateBetweenAndStatus(min, max, MentorStatus.APPROVED, pageable);
	}

	/* ================================================================
	 * METHOD: searchByRating
	 * DESCRIPTION: Queries paginated mentors filtered by minimum average rating.
	 * ================================================================ */
	// ⭐ SEARCH BY RATING
	public Page<Mentor> searchByRating(Double rating, int page, int size, String sortBy) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

		return mentorRepository.findByAverageRatingGreaterThanEqualAndStatus(rating, MentorStatus.APPROVED, pageable);
	}

	/* ================================================================
	 * METHOD: searchMentorBySkill
	 * DESCRIPTION: Returns a paginated list of mentors who possess a specific skill.
	 * ================================================================ */
	// ⭐ SEARCH BY SKILL
	public Page<Mentor> searchMentorBySkill(Long skillId, int page, int size) {

		List<MentorSkill> mappings = mentorSkillRepository.findBySkillId(skillId);

		List<Long> mentorIds = mappings.stream().map(MentorSkill::getMentorId).toList();

		Pageable pageable = PageRequest.of(page, size);

		return mentorRepository.findByMentorIdInAndStatus(mentorIds, MentorStatus.APPROVED, pageable);
	}

	/* ================================================================
	 * METHOD: searchMentors
	 * DESCRIPTION: Combined dynamic search using Spring Data Specifications.
	 * ================================================================ */
	// ⭐ COMBINED SEARCH
	public Page<Mentor> searchMentors(Double minPrice, Double maxPrice, Double rating, Boolean available, int page,
			int size, String sortBy) {

		Specification<Mentor> spec = Specification.where(MentorSpecification.hasMinPrice(minPrice))
				.and(MentorSpecification.hasMaxPrice(maxPrice)).and(MentorSpecification.hasRating(rating))
				.and(MentorSpecification.isAvailable(available))
				.and((root, q, cb) -> cb.equal(root.get("status"), MentorStatus.APPROVED));

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

		return mentorRepository.findAll(spec, pageable);
	}

	/* ================================================================
	 * METHOD: searchMentorsAdvanced
	 * DESCRIPTION: Fully advanced faceted search including multiple criteria concurrently.
	 * ================================================================ */
	// ⭐ ADVANCED SEARCH
	public Page<Mentor> searchMentorsAdvanced(Long skillId, Double minPrice, Double maxPrice, Double rating,
			Boolean available, int page, int size, String sortBy) {

		Specification<Mentor> spec = Specification.where(MentorSearchSpecification.hasSkill(skillId))
				.and(MentorSearchSpecification.minPrice(minPrice)).and(MentorSearchSpecification.maxPrice(maxPrice))
				.and(MentorSearchSpecification.minRating(rating)).and(MentorSearchSpecification.availability(available))
				.and((root, q, cb) -> cb.equal(root.get("status"), MentorStatus.APPROVED));

		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

		return mentorRepository.findAll(spec, pageable);
	}

	/* ================================================================
	 * METHOD: updateRating
	 * DESCRIPTION: Recalculates and updates the cumulative average rating 
	 * for a mentor when a new session review is published.
	 * ================================================================ */
	// ⭐ UPDATE RATING
	public void updateRating(Long mentorId, Double rating) {
		log.info("Updating rating for mentor id: {}", mentorId);

		if (rating < 0 || rating > 5) {
			log.error("Invalid rating: {} for mentor id: {}", rating, mentorId);
			throw new RuntimeException("Invalid rating");
		}

		Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(() -> new RuntimeException("Mentor not found"));

		double avg = mentor.getAverageRating();
		int total = mentor.getTotalSessions();

		double newAvg = ((avg * total) + rating) / (total + 1);

		mentor.setAverageRating(newAvg);
		mentor.setTotalSessions(total + 1);

		mentorRepository.save(mentor);
	}



	public Boolean mentorExists(Long mentorId) {
		return mentorRepository.existsById(mentorId);
	}

    public Long getUserIdByMentorId(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .map(Mentor::getUserId)
                .orElseThrow(() -> new RuntimeException("Mentor not found"));
    }
    
    public Long getMentorIdByUserId(Long userId) {
        return mentorRepository.findByUserId(userId)
                .map(Mentor::getMentorId)
                .orElseThrow(() -> new RuntimeException("Mentor not found matching the userId"+userId));
    }

	private MentorProfileResponseDto map(Mentor mentor, String msg) {

		return MentorProfileResponseDto.builder().mentorId(mentor.getMentorId()).userId(mentor.getUserId())
				.bio(mentor.getBio()).experienceYears(mentor.getExperienceYears()).hourlyRate(mentor.getHourlyRate())
				.averageRating(mentor.getAverageRating()).totalSessions(mentor.getTotalSessions())
				.available(mentor.getAvailable()).message(msg).build();
	}
}