package com.example.s4hana.proposal;

import com.example.s4hana.BaseEntity;

/**
 * Proposal class to hold information about proposals in S4Hana Extension APP
 * @author I030998
 *
 */
public class Proposal extends BaseEntity{

	private int id;
	private String state;
	public Proposal(int proposalId, String firstName, String lastName, String city, String country, String state) {
		super(firstName, lastName, city, country);
		this.id = proposalId;
		this.state = state;
	}

	public int getProposalId() {
		return id;
	}
	
	public String getState() {
		return state;
	}
}
