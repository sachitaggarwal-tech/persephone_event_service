package com.example.s4hana.proposal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.base.destination.DestinationAccessor;
import com.example.base.utils.APIHandler;

/**
 * Facade to work with S4Hana proposal extensions
 * 
 * @author I030998
 *
 */
public class ProposalAPIFacade {

	//TODO: read this url also from destinations
	//private static String baseUrl = "https://persephone-dev-workflow-events-java.cfapps.sap.hana.ondemand.com/odata/v4/CustomerService/";
	private Logger LOG = LoggerFactory.getLogger(ProposalAPIFacade.class);
	private static final String PROPOSAL_OAUTH_DEST = "PROPOSAL_APP_OAUTH";
	private static final String PROPOSAL_DEST = "PROPOSAL_APP";
	private DestinationAccessor destAcc;
	private APIHandler apiHandler;
	
	public ProposalAPIFacade(DestinationAccessor accessor , APIHandler apiHandler) {
		this.destAcc = accessor;
		this.apiHandler = apiHandler;
	}
	
	public List<Proposal> getNewProposals() {
		LOG.info("Getting proposals");

		//get jwt token for client credentials for proposal api
		String token = destAcc.getAuthToken(PROPOSAL_OAUTH_DEST);
		if (token != null) {
			//get propsals
			Map<String, String> headersMap = new HashMap<>();
			headersMap.put("Authorization", "Bearer " + token);
			String response = apiHandler.executeGetCall(getProposalAppUrl() + "/ProposedCustomers", headersMap);
			if (response != null) {
				try {
					List<Proposal> proposalsList = new ArrayList<>();
					JSONObject obj2 = new JSONObject(response);
					if (obj2 != null && obj2.has("value")) {
						JSONArray array = obj2.getJSONArray("value");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							proposalsList.add(new Proposal(obj.getInt("ProposalId"), obj.getString("FirstName"),
									obj.getString("LastName"), obj.getString("City"),obj.getString("Country"),
									obj.getString("ApprovalStatus")));
						}
						return proposalsList;
					} else {
						LOG.error("Failed to parse response -" + response);
					}
				} catch (JSONException e) {
					LOG.error("Failed to read proposals - " + e.getMessage());
					return null;
				}
			}
			LOG.error("Failed to read proposals null response");
			return null;
		} else {
			LOG.error("Failed to get auth token");
			return null;
		}
	}

	public void rejectProposal(String proposalId, String customerId) {
		LOG.info("Closing proposal - " + proposalId);
		String token = destAcc.getAuthToken(PROPOSAL_OAUTH_DEST);
		if (token != null) {
			Map<String, String> headersMap = new HashMap<>();
			headersMap.put("Content-Type", "application/json");
			headersMap.put("Authorization", "Bearer " + token);
			String body = "{\"ProposalId\":" + proposalId
					+ ",\"Comments\": \"Rejected since duplicate found in S4Hana with Id -" + customerId + " \""
					+ ",\"ProcessedBy\":\"s4hana.events@persephone.com\""
							+ "}";
			int code = apiHandler.executePostCall(getProposalAppUrl() + "/Close", body, headersMap);
			if (code >= 200 && code <= 300) {
				LOG.info("Rejection completed");
			} else {
				LOG.error("Rejection failed");
			}
		} else {
			LOG.error("Failed to get auth token");
		}
	}

	private String getProposalAppUrl() {
		Map<String, String> destProperties = destAcc.getDestinationProperties(PROPOSAL_DEST);
		String url = destProperties.get("URL")+"/odata/v4/CustomerService/";
		LOG.debug("Proposal APP Url : - "+url);
		return url;
	}
}
