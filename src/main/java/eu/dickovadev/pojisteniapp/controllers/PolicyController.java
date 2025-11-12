package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.responses.PolicyCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyIndexResponse;
import eu.dickovadev.pojisteniapp.services.PolicyService;
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
@RequestMapping("/policy")
public class PolicyController {

    private static final Logger log = LoggerFactory.getLogger(PolicyController.class);

    private static final String VIEW_INDEX          = "pages/policy/index";
    private static final String VIEW_CREATE         = "pages/policy/create";
    private static final String VIEW_DETAIL         = "pages/policy/detail";
    private static final String VIEW_EDIT           = "pages/policy/edit";
    private static final String REDIRECT_INSURED_DETAIL = "redirect:/insured/%d/detail";
    private static final String REDIRECT_POLICY_DETAIL  = "redirect:/policy/%d/detail";

    private static final int INDEX_PAGE_SIZE  = 8;
    private static final int DETAIL_PAGE_SIZE = 3;

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public String renderIndex(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "searchField", required = false, defaultValue = "userId") String searchField
    ) {
        String q = query == null ? null : query.trim().toLowerCase();
        log.info("GET /policy index page={} size={} searchField={} query='{}'", page, INDEX_PAGE_SIZE, searchField, q);

        PolicyIndexResponse response = policyService.getPaginatedPolicies(q, searchField, page, INDEX_PAGE_SIZE);

        model.addAttribute("policyList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Index pojištění");

        return VIEW_INDEX;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/create")
    public String renderCreateForm(
            @PathVariable long userId,
            @ModelAttribute PolicyDTO policy,
            Model model
    ) {
        log.info("GET /policy/{}/create", userId);
        PolicyCreateResponse response = policyService.getPolicyCreateData(userId);

        model.addAttribute("policy", policy);
        model.addAttribute("policyTypes", response.getPolicyTypes());
        model.addAttribute("userId", userId);
        model.addAttribute("users", response.getUsers());
        model.addAttribute("insuredFirstName", response.getInsuredFirstName());
        model.addAttribute("insuredLastName", response.getInsuredLastName());
        model.addAttribute("pageTitle", "Přidat pojištění");

        return VIEW_CREATE;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{userId}/create")
    public String createPolicy(
            @PathVariable long userId,
            @Valid @ModelAttribute PolicyDTO policy,
            BindingResult result,
            RedirectAttributes flash,
            Model model
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on policy create for userId={} errors={}", userId, result.getErrorCount());
            return renderCreateForm(userId, policy, model); // stop on errors
        }

        policyService.create(policy, userId);
        log.info("Policy created for userId={}", userId);

        flash.addFlashAttribute("success", "Pojištění přidáno.");
        return String.format(REDIRECT_INSURED_DETAIL, userId);
    }

    @PreAuthorize("isAuthenticated() or hasRole('ROLE_ADMIN')")
    @GetMapping("/{policyId}/detail")
    public String renderDetail(
            @PathVariable long policyId,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            Authentication authentication
    ) {
        log.info("GET /policy/{}/detail page={} size={} by={}", policyId, page, DETAIL_PAGE_SIZE,
                authentication != null ? authentication.getName() : "anonymous");

        PolicyDetailResponse response =
                policyService.getPolicyWithPaginatedEvents(policyId, page, DETAIL_PAGE_SIZE, authentication);

        model.addAttribute("policy", response.getPolicy());
        model.addAttribute("userId", response.getUserId());
        model.addAttribute("eventList", response.getPaginatedEvents());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("sameUser", response.getPolicy().isSameUser());
        model.addAttribute("pageTitle", "Detail pojištění");

        return VIEW_DETAIL;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{policyId}/edit")
    public String renderEditForm(
            @PathVariable long policyId,
            Model model,
            @ModelAttribute PolicyDTO policy

    ) {
        log.info("GET /policy/{}/edit", policyId);
        PolicyEditResponse response = policyService.getPolicyEditData(policyId);

        model.addAttribute("policy", response.getPolicy());
        model.addAttribute("policyTypes", response.getPolicyTypes());
        model.addAttribute("users", response.getUsers());
        model.addAttribute("userId", response.getUserId());
        model.addAttribute("insuredUser", response.getInsuredUser());
        model.addAttribute("pageTitle", "Upravit pojištění");

        return VIEW_EDIT;
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{policyId}/edit")
    public String editPolicy(
            @PathVariable long policyId,
            @Valid @ModelAttribute PolicyDTO policy,
            BindingResult result,
            RedirectAttributes flash,
            Model model
    ) {
        if (result.hasErrors()) {
            log.debug("Validation errors on policy edit policyId={} errors={}", policyId, result.getErrorCount());
            return renderEditForm(policyId, model, policy); // stop on errors
        }

        long userId = policyService.edit(policyId, policy);
        log.info("Policy {} edited (userId={})", policyId, userId);

        flash.addFlashAttribute("success", "Změny uloženy.");
        return String.format(REDIRECT_POLICY_DETAIL, policyId);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{policyId}/delete")
    public String deletePolicy(
            @PathVariable long policyId,
            RedirectAttributes flash
    ) {
        long userId = policyService.remove(policyId);
        log.warn("Policy {} deleted by admin; redirecting to insured detail {}", policyId, userId);

        flash.addFlashAttribute("success", "Pojištění smazáno.");
        return String.format(REDIRECT_INSURED_DETAIL, userId);
    }
}
