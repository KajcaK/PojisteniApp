package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.entities.UserEntity;
import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.enums.PolicyType;
import eu.dickovadev.pojisteniapp.models.exceptions.InvalidPolicyDatesException;
import eu.dickovadev.pojisteniapp.models.exceptions.PolicyNotFoundException;
import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.models.mappers.PolicyMapper;
import eu.dickovadev.pojisteniapp.models.mappers.UserMapper;
import eu.dickovadev.pojisteniapp.models.services.PaginationService;
import eu.dickovadev.pojisteniapp.models.services.PolicyService;
import eu.dickovadev.pojisteniapp.models.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/policy")
public class PolicyController {

    @Autowired
    private PolicyService policyService;
    @Autowired
    private UserService userService;
    @Autowired
    private PolicyMapper policyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PaginationService paginationService;

    @GetMapping
    public String renderIndex(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        // Define the page size
        int pageSize = 8;

        List<PolicyDTO> policyList = policyService.getAll();

        //Paginate list
        List<PolicyDTO> paginatedList = paginationService.paginate(policyList, page, pageSize);

        //Get calculated metadata
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(policyList, page, pageSize);

        model.addAttribute("policyList", policyList);
        model.addAttribute("currentPage", paginationMetadata.get("currentPage"));
        model.addAttribute("totalPages", paginationMetadata.get("totalPages"));
        model.addAttribute("totalItems", paginationMetadata.get("totalItems"));

        return "pages/policy/index";
    }

    @GetMapping("/{userId}/create")
    public String renderCreateForm(
            @PathVariable long userId,
            @ModelAttribute PolicyDTO policy,
            Model model
    ) {
        UserEntity insured = userService.getEntityById(userId);

        model.addAttribute("policy", policy);  // Pass the PolicyDTO object to the form
        model.addAttribute("policyTypes", PolicyType.values()); // Dropdown options for policy type
        model.addAttribute("userId", userId);  // Ensure userId is available for the form
        model.addAttribute("users", userService.getAll());  // List of users for the policyholder dropdown
        model.addAttribute("insuredFirstName", insured.getFirstName());
        model.addAttribute("insuredLastName", insured.getLastName());

        return "pages/policy/create";
    }


    @PostMapping("/{userId}/create")
    public String createPolicy(
            @Valid @ModelAttribute PolicyDTO policy,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @PathVariable long userId,
            Model model,
            HttpServletRequest request
    ) {
        if (result.hasErrors()) {
            return renderCreateForm(userId, policy, model);
        }

        UserEntity insuredUserEntity = userService.getEntityById(userId);
        policy.setInsuredUser(userMapper.toDTO(insuredUserEntity));

        UserEntity policyHolderEntity = userService.getEntityById(policy.getPolicyHolder().getUserId());
        policy.setPolicyHolder(userMapper.toDTO(policyHolderEntity));

        policyService.create(policy);

        // Make user IDs are available for error handler for redirect
        request.getSession().setAttribute("userId", userId);

        redirectAttributes.addFlashAttribute("success", "Pojištění přidáno.");

        return "redirect:/insured/" + userId + "/detail";
    }


    @GetMapping("/{policyId}/detail")
    public String renderDetail(
            @PathVariable long policyId,
            Model model
    ) {
        PolicyDTO policy = policyService.getById(policyId);

        long userId = policyService.getPolicyHolderIdByPolicyId(policyId);

        model.addAttribute("policy", policy);
        model.addAttribute("userId", userId);

        return "pages/policy/detail";
    }

    @GetMapping("/{policyId}/edit")
    public String renderEditForm(
            @PathVariable Long policyId,
            Model model,
            PolicyDTO policy

    ) {
        PolicyDTO fetchedPolicy = policyService.getById(policyId);

        long userId = policyService.getPolicyHolderIdByPolicyId(policyId);

        //TODO: policy type service?
        model.addAttribute("policy", fetchedPolicy);
        model.addAttribute("policyTypes", PolicyType.values()); // Add dropdown options
        model.addAttribute("userId", policyService.getPolicyHolderIdByPolicyId(policyId));
        model.addAttribute("users", userService.getAll());
        model.addAttribute("userId", userId);

        return "pages/policy/edit";
    }

    @PostMapping("/{policyId}/edit")
    public String editPolicy(
            @PathVariable long policyId,
            @Valid PolicyDTO policy,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpServletRequest request
    ) {
        if (result.hasErrors()) {
            return renderEditForm(policyId, model, policy);
        }

        policy.setPolicyId(policyId);

        long userId = policyService.getPolicyHolderIdByPolicyId(policyId);
        // Make IDs are available for error handler for redirect
        request.getSession().setAttribute("userId", userId);
        request.getSession().setAttribute("policyId", policyId);

        policyService.edit(policy);

        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");
        return "redirect:/policy/" + policyId + "/detail";
    }

    @GetMapping("/{policyId}/delete")
    public String deletePolicy(
            @PathVariable long policyId,
            RedirectAttributes redirectAttributes
    ){
        long userId = policyService.getPolicyHolderIdByPolicyId(policyId);

        policyService.remove(policyId);
        redirectAttributes.addFlashAttribute("success", "Pojištění smazáno.");

        return "redirect:/insured/" + userId + "/detail";
    }

    @ExceptionHandler({PolicyNotFoundException.class})
    public String handlePolicyNotFoundException(
            RedirectAttributes redirectAttributes,
            PolicyNotFoundException ex
    ){
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/policy";
    }

    @ExceptionHandler({InvalidPolicyDatesException.class})
    public String handleInvalidPolicyDatesException(
            RedirectAttributes redirectAttributes,
            InvalidPolicyDatesException ex,
            HttpServletRequest request
    ) {
        long userId = (long) request.getSession().getAttribute("userId");
        long policyId = (long) request.getSession().getAttribute("policyId");

        String requestUri = request.getRequestURI();

        if (requestUri.contains("/policy/" + userId + "/create")) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/policy/" + userId + "/create";
        } else if (requestUri.contains("/policy/" + policyId + "/edit")) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/policy/" + policyId + "/edit";
        } else {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "/error";
        }
    }


    @ExceptionHandler({UserNotFoundException.class})
    public String handleUserNotFoundException(
            RedirectAttributes redirectAttributes,
            UserNotFoundException ex,
            HttpServletRequest request
    ){
        long userId = (long) request.getSession().getAttribute("userId");
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/policy/" + userId + "/create";
    }
}
