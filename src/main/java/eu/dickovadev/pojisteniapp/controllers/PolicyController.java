package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import eu.dickovadev.pojisteniapp.models.exceptions.AccessDeniedException;
import eu.dickovadev.pojisteniapp.models.responses.PolicyCreateResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyEditResponse;
import eu.dickovadev.pojisteniapp.models.responses.PolicyIndexResponse;
import eu.dickovadev.pojisteniapp.services.PolicyService;
import eu.dickovadev.pojisteniapp.services.UserService;
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
@RequestMapping("/policy")
public class PolicyController {

    private final PolicyService policyService;

    @Autowired
    public PolicyController(
            PolicyService policyService
    ) {
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
        // Define the page size
        int pageSize = 8;

        PolicyIndexResponse response = policyService.getPaginatedPolicies(query, searchField, page, pageSize);

        model.addAttribute("policyList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Index pojištění");

        return "pages/policy/index";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/create")
    public String renderCreateForm(
            @PathVariable long userId,
            @ModelAttribute PolicyDTO policy,
            Model model
    ) {

        PolicyCreateResponse response = policyService.getPolicyCreateData(userId);

        model.addAttribute("policy", policy);  // Pass the PolicyDTO object to the form
        model.addAttribute("policyTypes", response.getPolicyTypes()); // Dropdown options for policy type
        model.addAttribute("userId", userId);  // Ensure userId is available for the form
        model.addAttribute("users", response.getUsers());  // List of users for the policyholder dropdown
        model.addAttribute("insuredFirstName", response.getInsuredFirstName());
        model.addAttribute("insuredLastName", response.getInsuredLastName());
        model.addAttribute("pageTitle", "Přidat pojištění");

        return "pages/policy/create";
    }


    @Secured("ROLE_ADMIN")
    @PostMapping("/{userId}/create")
    public String createPolicy(
            @Valid @ModelAttribute PolicyDTO policy,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @PathVariable long userId,
            Model model
    ) {
        if (result.hasErrors()) {
            return renderCreateForm(userId, policy, model);
        }

        policyService.create(policy, userId);

        redirectAttributes.addAttribute("userId", userId);

        redirectAttributes.addFlashAttribute("success", "Pojištění přidáno.");

        return "redirect:/insured/" + userId + "/detail";
    }

    @PreAuthorize("isAuthenticated() or hasRole('ROLE_ADMIN')")
    @GetMapping("/{policyId}/detail")
    public String renderDetail(
            @PathVariable long policyId,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            Authentication authentication
    ) {
        // Define the page size
        int pageSize = 3;

        PolicyDetailResponse response = policyService.getPolicyWithPaginatedEvents(policyId, page, pageSize, authentication);

        //add attributes to model
        model.addAttribute("policy", response.getPolicy());
        model.addAttribute("userId", response.getUserId());
        model.addAttribute("eventList", response.getPaginatedEvents());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("sameUser", response.getPolicy().isSameUser());
        model.addAttribute("pageTitle", "Detail pojištění");

        return "pages/policy/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{policyId}/edit")
    public String renderEditForm(
            @PathVariable long policyId,
            Model model,
            PolicyDTO policy

    ) {
        PolicyEditResponse response = policyService.getPolicyEditData(policyId);

        model.addAttribute("policy", response.getPolicy());
        model.addAttribute("policyTypes", response.getPolicyTypes());
        model.addAttribute("users", response.getUsers());
        model.addAttribute("userId", response.getUserId());
        model.addAttribute("insuredUser", response.getInsuredUser());
        model.addAttribute("pageTitle", "Upravit pojištění");


        return "pages/policy/edit";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{policyId}/edit")
    public String editPolicy(
            @PathVariable long policyId,
            @Valid @ModelAttribute PolicyDTO policy,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        if (result.hasErrors()) {
            return renderEditForm(policyId, model, policy);
        }

        // Call service and get userId
        long userId = policyService.edit(policyId, policy);

        redirectAttributes.addAttribute("userId", userId);
        redirectAttributes.addAttribute("policyId", policyId);

        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");
        return "redirect:/policy/" + policyId + "/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{policyId}/delete")
    public String deletePolicy(
            @PathVariable long policyId,
            RedirectAttributes redirectAttributes
    ) {
        // Call service and get userId
        long userId = policyService.remove(policyId);

        redirectAttributes.addFlashAttribute("success", "Pojištění smazáno.");

        return "redirect:/insured/" + userId + "/detail";
    }
}
