package eu.dickovadev.pojisteniapp.services;

import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.enums.EventType;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import eu.dickovadev.pojisteniapp.models.enums.Role;
import eu.dickovadev.pojisteniapp.models.responses.StatisticsResponse;
import eu.dickovadev.pojisteniapp.repositories.EventRepository;
import eu.dickovadev.pojisteniapp.repositories.PolicyRepository;
import eu.dickovadev.pojisteniapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final EventRepository eventRepository;

    @Autowired
    public StatisticsService(UserRepository userRepository, PolicyRepository policyRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.eventRepository = eventRepository;
    }


    public StatisticsResponse getStatistics() {

        StatisticsResponse response = new StatisticsResponse();

        gatherUserStats(response);
        gatherPolicyStats(response);
        gatherEventStats(response);
        gatherAvgStats(response);

        return response;
    }

    private void gatherUserStats(StatisticsResponse response) {

        response.addUserStat("totalUserCount", userRepository.countTotalUsers());
        response.addUserStat("usersWithMissingNames", userRepository.countUsersWithNullNames());
        response.addUserStat("activeUserCount", userRepository.countActiveUsers());

        for (Role role : Role.values()) {
            response.addUserStat(role.name(), userRepository.countTotalByRole(role));
        }
    }

    private void gatherPolicyStats(StatisticsResponse response) {
        response.addPolicyStat("totalPolicyCount", policyRepository.countTotalPolicies());
        response.addPolicyStat("totalActivePolicies", policyRepository.countTotalActivePolicies());

        for (PolicyType type : PolicyType.values()) {
            response.addPolicyStat(type.name(), policyRepository.countTotalPoliciesByType(type));
        }
    }

    private void gatherEventStats(StatisticsResponse response) {
        response.addEventStat("totalEventCount", eventRepository.countTotalEvents());
        response.addEventStat("totalOriginalClaimAmountEvents", eventRepository.countTotalOriginalClaimAmount());
        response.addEventStat("totalAmountPaid", eventRepository.totalAmountPaid());

        for (EventStatus status : EventStatus.values()) {
            response.addEventStat(status.name(), eventRepository.countTotalByStatus(status));
        }

        for (EventType type : EventType.values()) {
            response.addEventStat(type.name(), eventRepository.countTotalByType(type));
        }
    }

    private void gatherAvgStats(StatisticsResponse response) {
        response.addAvgStat("averagePolicyCoverage", policyRepository.averagePolicyCoverage());
        response.addAvgStat("averageEventClaimAmount", eventRepository.averageEventClaimAmount());
        response.addAvgStat("averagePolicyDuration", policyRepository.averagePolicyDuration());

        for (PolicyType type : PolicyType.values()) {
            response.addAvgStat(type.name() + "averageCoverageByType", policyRepository.averagePolicyCoverageByType(type));
        }
    }
}