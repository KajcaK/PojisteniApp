package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.dto.EventDTO;
import eu.dickovadev.pojisteniapp.models.enums.EventStatus;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.responses.EventCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.EventIndexResponse;
import eu.dickovadev.pojisteniapp.services.EventService;
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

    private static final String VIEW_INDEX = "pages/event/index";
    private static final String VIEW_CREATE = "pages/event/create";
    private static final String VIEW_DETAIL = "pages/event/detail";
    private static final String VIEW_EDIT = "pages/event/edit";
    private static final String VIEW_REPORT = "pages/event/report";
    private static final String REDIRECT_DETAIL = "redirect:/event/%d/detail";
    private static final String REDIRECT_POLICY_DETAIL = "redirect:/policy/%d/detail"; //TODO: move to policy

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

        return VIEW_INDEX;
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

        return VIEW_CREATE;
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

        return String.format(REDIRECT_POLICY_DETAIL, policyId);
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

        return VIEW_DETAIL;
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

        return VIEW_EDIT;
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

        return String.format(REDIRECT_DETAIL, eventId);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{eventId}/delete")
    public String deleteEvent(
            @PathVariable long eventId,
            RedirectAttributes redirectAttributes
    ) {
        // Call service and get policyId
        long policyId = eventService.remove(eventId);

        redirectAttributes.addFlashAttribute("success", "Událost smazána.");

        return String.format(REDIRECT_POLICY_DETAIL, policyId);
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

            return VIEW_REPORT;

        } catch (AccessDeniedException e) {
            return "access-denied"; //TODO: constants for errors
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

            return String.format(REDIRECT_POLICY_DETAIL, policyId);

        } catch (AccessDeniedException e) {
            return "access-denied";
        }
    }
}
