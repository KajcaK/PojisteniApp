package eu.dickovadev.pojisteniapp.utils;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.PolicyUserDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class StringUtil {

    public String formatFullName(UserDTO user) {
        if (user.getFirstName() == null || user.getLastName() == null) {
            return "Celé jméno chybí";
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    public String formatFullName(PolicyUserDTO user) {
        if (user.getFirstName() == null || user.getLastName() == null) {
            return "Celé jméno chybí";
        }
        return user.getFirstName() + " " + user.getLastName();
    }

    public String formatFullNameWithId(UserDTO user) {
        if (user.getFirstName() == null || user.getLastName() == null) {
            return "Celé jméno chybí (" + user.getUserId() + ")";
        }
        return String.format("%s %s (%d)", user.getFirstName(), user.getLastName(), user.getUserId());
    }

    public String formatPolicyTypeWithId(PolicyDTO policy) {
        return String.format("%s (%d)", policy.getType().getFormattedPolicyType(), policy.getPolicyId());
    }

    public String formatEventTypeWithId(EventDTO event) {
        return String.format("%s (%d)", event.getEventType().getFormattedEventType(), event.getEventId());
    }

    public String formatAddress(UserDTO user) {
        if (user.getStreet() == null || user.getCity() == null) {
            return "Nevyplněno";
        }
        return user.getStreet() + ", " + user.getCity();
    }
}
