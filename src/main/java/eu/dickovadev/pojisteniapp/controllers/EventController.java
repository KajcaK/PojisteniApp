package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.responses.EventCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventIndexResponse;
import eu.dickovadev.pojisteniapp.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(
            EventService eventService
    ) {
        this.eventService = eventService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public String renderIndex(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "queryId", required = false) String queryId,
            @RequestParam(name = "queryStatus", required = false) String queryStatus,
            @RequestParam(name = "searchField", required = false, defaultValue = "eventId") String searchField
    ) {
        // Define the page size
        int pageSize = 8;

        if (queryId != null) queryId = queryId.trim();

        EventIndexResponse response = eventService.getEventIndexData(queryId, queryStatus, searchField, page, pageSize);

        model.addAttribute("eventList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("eventStatuses", EventStatus.values()); //Enum values for view
        model.addAttribute("pageTitle", "Událost - index");

        return "pages/event/index";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{policyId}/create")
    public String renderCreateForm(
            @PathVariable long policyId,
            @ModelAttribute EventDTO event,
            Model model,
            Authentication authentication
    ) {
        EventCreateResponse response = eventService.getEventCreateData(policyId, authentication);

        model.addAttribute("event", event); // Pass the EventDTO object to the form
        model.addAttribute("eventTypes", response.getAvailableEventTypes()); // Dropdown options for event type
        model.addAttribute("eventStatus", response.getEventStatuses()); // Dropdown options for event status
        model.addAttribute("pageTitle", "Vytvořit událost");

        return "pages/event/create";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{policyId}/create")
    public String createEvent(
            @Valid @ModelAttribute EventDTO event,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @PathVariable long policyId,
            Model model,
            Authentication authentication
    ) {
        if (result.hasErrors()) {
            return renderCreateForm(policyId, event, model, authentication);
        }

        eventService.create(event, policyId);

        redirectAttributes.addFlashAttribute("success", "Událost vytvořena.");

        return "redirect:/policy/" + policyId + "/detail";
    }

    @PreAuthorize("isAuthenticated() or hasRole('ROLE_ADMIN')")
    @GetMapping("/{eventId}/detail")
    public String renderDetail(
            @PathVariable long eventId,
            Model model,
            Authentication authentication
    ) {
        EventDetailResponse response = eventService.getEventDetailData(eventId, authentication);

        //add attributes to model
        model.addAttribute("event", response.getEvent());
        model.addAttribute("policy", response.getPolicy());
        model.addAttribute("sameUser", response.getPolicy().isSameUser());
        model.addAttribute("pageTitle", "Událost");

        return "pages/event/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{eventId}/edit")
    public String renderEditForm(
            @PathVariable long eventId,
            Model model,
            @ModelAttribute EventDTO event
    ) {
        EventEditResponse response = eventService.getEventEditResponse(eventId);

        model.addAttribute("event", response.getEvent()); // Pass the EventDTO object to the form
        model.addAttribute("eventTypes", response.getAvailableEventTypes()); // Dropdown options for event type
        model.addAttribute("eventStatus", response.getEventStatuses()); // Dropdown options for event status
        model.addAttribute("policyId", response.getEvent().getPolicyId()); //pass policyId to form
        model.addAttribute("pageTitle", "Upravit událost");

        return "pages/event/edit";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{eventId}/edit")
    public String editEvent(
            @PathVariable long eventId,
            @Valid @ModelAttribute EventDTO event,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            return renderEditForm(eventId, model, event);
        }

        eventService.edit(event, eventId);
        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return "redirect:/event/" + eventId + "/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{eventId}/delete")
    public String deleteEvent(
            @PathVariable long eventId,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {
        // Call service and get policyId
        long policyId = eventService.remove(eventId);

        redirectAttributes.addFlashAttribute("success", "Událost smazána.");

        return "redirect:/policy/" + policyId + "/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{policyId}/report")
    public String renderReportForm(
            @PathVariable long policyId,
            @ModelAttribute EventDTO event,
            Model model,
            Authentication authentication
    ) {
        try {
            EventCreateResponse response = eventService.getEventCreateData(policyId, authentication);

            model.addAttribute("event", event); // Pass the EventDTO object to the form
            model.addAttribute("eventTypes", response.getAvailableEventTypes()); // Dropdown options for event type
            model.addAttribute("pageTitle", "Vytvořit událost");

            return "pages/event/report";

        } catch (AccessDeniedException e) {
            return "access-denied";
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{policyId}/report")
    public String reportEvent(
            @Valid @ModelAttribute EventDTO event,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @PathVariable long policyId,
            Model model,
            Authentication authentication
    ) {
        try {
            if (result.hasErrors()) {
                return renderCreateForm(policyId, event, model, authentication);
            }

            eventService.report(event, policyId, authentication);

            redirectAttributes.addFlashAttribute("success", "Událost nahlášena.");

            return "redirect:/policy/" + policyId + "/detail";

        } catch (AccessDeniedException e) {
            return "access-denied";
        }
    }
}
