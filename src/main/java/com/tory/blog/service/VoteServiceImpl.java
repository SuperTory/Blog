package com.tory.blog.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tory.blog.entity.Vote;
import com.tory.blog.repository.VoteRepository;

/**
 * Vote 服务.
 */
@Service
public class VoteServiceImpl implements VoteService {

	@Autowired
	private VoteRepository voteRepository;

	@Override
	@Transactional
	public void removeVote(Long id) {
		voteRepository.deleteById(id);
	}

	@Override
	public Vote getVoteById(Long id) {
		return voteRepository.getOne(id);
	}

}
