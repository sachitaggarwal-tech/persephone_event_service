package com.example.base.demo.events;

import org.junit.Test;

import com.example.demo.events.BusinessPartnetEventUtil;
import com.example.s4hana.Customer;
import com.example.s4hana.proposal.Proposal;

public class TestBusinessPartnerEventsUtil {

	@Test
	public void testProposalCustomerComparison() {
		Proposal proposal = new Proposal(1, "abc", "xyz", "Bengaluru","IN", "inProcess");
		Customer customer = new Customer("1", "abc", "xyz", "Bengaluru","IN");
		BusinessPartnetEventUtil.compareProposalToCustomer(proposal, customer);
	}
	
	@Test
	public void testBusinessPartnerEvent() {
		//for code coverage only
		BusinessPartnetEventUtil.handleBusinessPartnerEvent(null);
		BusinessPartnetEventUtil.handleBusinessPartnerEvent("111");
	}
}
