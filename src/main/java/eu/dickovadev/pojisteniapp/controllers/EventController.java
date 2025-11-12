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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private static final String VIEW_INDEX             = "pages/event/index";
    private static final String VIEW_CREATE            = "pages/event/create";
    private static final String VIEW_DETAIL            = "pages/event/detail";
    private static final String VIEW_EDIT              = "pages/event/edit";
    private static final String VIEW_REPORT            = "pages/event/report";
    private static final String VIEW_ACCESS_DENIED     = "errors/access-denied";

    private static final String REDIRECT_EVENT_DETAIL  = "redirect:/event/%d/detail";
    private static final String REDIRECT_POLICY_DETAIL = "redirect:/policy/%d/detail"; //TODO: move to policy

    private static final int DEFAULT_PAGE_SIZE = 8;

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

        if (queryId != null) queryId = queryId.trim();

        log.info("GET /event index page={} size={} searchField={} queryId={} queryStatus={}",
                page, DEFAULT_PAGE_SIZE, searchField, queryId, queryStatus);

        EventIndexResponse response =
                eventService.getEventIndexData(queryId, queryStatus, searchField, page, DEFAULT_PAGE_SIZE);

        model.addAttribute("eventList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("eventStatuses", EventStatus.values());
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
        log.info("GET /event/{}/create by {}", policyId, authentication != null ? authentication.getName() : "anonymous");
        EventCreateResponse response = eventService.getEventCreateData(policyId, authentication);

        model.addAttribute("event", event);
        model.addAttribute("eventTypes", response.getAvailableEventTypes());
        model.addAttribute("eventStatus", response.getEventStatuses());
        model.addAttribute("pageTitle", "Vytvořit událost");

        return VIEW_CREATE;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{policyId}/create")
    public String createEvent(
            @PathVariable long policyId,
            @Valid @ModelAttribute EventDTO event,
            BindingResult result,
            RedirectAttributes flash,
            Model model,
            Authentication authentication
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on admin create policyId={} errors={}", policyId, result.getErrorCount());
            return renderCreateForm(policyId, event, model, authentication); // stop on errors
        }

        eventService.create(event, policyId);
        log.info("Event created for policyId={} by {}", policyId, authentication != null ? authentication.getName() : "unknown");
        flash.addFlashAttribute("success", "Událost vytvořena.");
        return String.format(REDIRECT_POLICY_DETAIL, policyId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{eventId}/detail")
    public String renderDetail(
            @PathVariable long eventId,
            Model model,
            Authentication authentication
    ) {
        log.info("GET /event/{}/detail by {}", eventId, authentication != null ? authentication.getName() : "anonymous");
        EventDetailResponse response = eventService.getEventDetailData(eventId, authentication);

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
        log.info("GET /event/{}/edit", eventId);
        EventEditResponse response = eventService.getEventEditResponse(eventId);

        model.addAttribute("event", response.getEvent());
        model.addAttribute("eventTypes", response.getAvailableEventTypes());
        model.addAttribute("eventStatus", response.getEventStatuses());
        model.addAttribute("policyId", response.getEvent().getPolicyId());
        model.addAttribute("pageTitle", "Upravit událost");

        return VIEW_EDIT;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{eventId}/edit")
    public String editEvent(
            @PathVariable long eventId,
            @Valid @ModelAttribute EventDTO event,
            BindingResult result,
            RedirectAttributes flash,
            Model model
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on edit eventId={} errors={}", eventId, result.getErrorCount());
            return renderEditForm(eventId, model, event); // stop on errors
        }

        eventService.edit(event, eventId);
        log.info("Event {} edited", eventId);
        flash.addFlashAttribute("success", "Změny uloženy.");
        return String.format(REDIRECT_EVENT_DETAIL, eventId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{eventId}/delete")
    public String deleteEvent(
            @PathVariable long eventId,
            RedirectAttributes flash
    ) {
        long policyId = eventService.remove(eventId);
        log.warn("Event {} deleted by admin", eventId);
        flash.addFlashAttribute("success", "Událost smazána.");
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
        log.info("GET /event/{}/report by {}", policyId, authentication != null ? authentication.getName() : "anonymous");
        try {
            EventCreateResponse response = eventService.getEventCreateData(policyId, authentication);
            model.addAttribute("event", event);
            model.addAttribute("eventTypes", response.getAvailableEventTypes());
            model.addAttribute("pageTitle", "Nahlásit událost");
            return VIEW_REPORT;

        } catch (AccessDeniedException ex) {
            log.warn("Access denied on report form policyId={} user={}", policyId, authentication != null ? authentication.getName() : "anonymous");
            return VIEW_ACCESS_DENIED;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{policyId}/report")
    public String reportEvent(
            @PathVariable long policyId,
            @Valid @ModelAttribute EventDTO event,
            BindingResult result,
            RedirectAttributes flash,
            Model model,
            Authentication authentication
    ) {
        try {
            if (result.hasErrors()) {
                log.debug("Validation errors on user report policyId={} errors={}", policyId, result.getErrorCount());
                return renderReportForm(policyId, event, model, authentication);
            }

            eventService.report(event, policyId, authentication);
            log.info("User {} reported event for policyId={}", authentication != null ? authentication.getName() : "anonymous", policyId);
            flash.addFlashAttribute("success", "Událost nahlášena.");
            return String.format(REDIRECT_POLICY_DETAIL, policyId);

        } catch (AccessDeniedException ex) {
            log.warn("Access denied on report submit policyId={} user={}", policyId, authentication != null ? authentication.getName() : "anonymous");
            return VIEW_ACCESS_DENIED;
        }
    }
}

