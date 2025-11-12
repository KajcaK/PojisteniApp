package eu.dickovadev.pojisteniapp.models.responses;

import java.util.HashMap;
import java.util.Map;

public class StatisticsResponse {

    private Map<String, Long> userStats;
    private Map<String, Long> policyStats;
    private Map<String, Long> eventStats;
    private Map<String, Double> avgStats;

    public StatisticsResponse() {
        userStats = new HashMap<>();
        policyStats = new HashMap<>();
        eventStats = new HashMap<>();
        avgStats = new HashMap<>();
    }

    public void addUserStat(String key, Long value) {
        userStats.put(key, value);
    }

    public void addPolicyStat(String key, Long value) {
        policyStats.put(key, value);
    }

    public void addEventStat(String key, Long value) {
        eventStats.put(key, value);
    }

    public void addAvgStat(String key, Double value) {
        avgStats.put(key, value);
    }

    // Getters and Setters
    public Map<String, Long> getUserStats() {
        return userStats;
    }

    public Map<String, Long> getPolicyStats() {
        return policyStats;
    }

    public Map<String, Long> getEventStats() {
        return eventStats;
    }

    public Map<String, Double> getAvgStats() {
        return avgStats;
    }


    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "userStats=" + userStats +
                ", policyStats=" + policyStats +
                ", eventStats=" + eventStats +
                ", avgStats=" + avgStats +
                '}';
    }
}
