package eu.dickovadev.pojisteniapp.controllers;


import eu.dickovadev.pojisteniapp.models.dto.PolicyDTO;
import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.exceptions.DuplicateEmailException;
import eu.dickovadev.pojisteniapp.models.exceptions.EmailAlreadyRegisteredException;
import eu.dickovadev.pojisteniapp.models.exceptions.UserNotFoundException;
import eu.dickovadev.pojisteniapp.models.mappers.UserMapper;
import eu.dickovadev.pojisteniapp.models.services.PaginationService;
import eu.dickovadev.pojisteniapp.models.services.PolicyService;
import eu.dickovadev.pojisteniapp.models.services.RoleFilterService;
import eu.dickovadev.pojisteniapp.models.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/insured")
public class InsuredController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private RoleFilterService roleFilterService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public String renderIndex(
            Model model,
            @RequestParam(defaultValue = "1") int page
    ){
        // Define the page size
        int pageSize = 8;

        // Get all user profiles
        List<UserDTO> insuredList = userService.getAll();

        // Filter the list based on roles (admins will not be listed)
        List<UserDTO> filteredList = roleFilterService.filterUsersExcludingAdmins(insuredList);

        // Paginate the filtered list
        List<UserDTO> paginatedList = paginationService.paginate(filteredList, page, pageSize);

        // Get calculated metadata
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(filteredList, page, pageSize);

        // Add attributes to the model
        model.addAttribute("insuredList", paginatedList);
        model.addAttribute("currentPage", paginationMetadata.get("currentPage"));
        model.addAttribute("totalPages", paginationMetadata.get("totalPages"));
        model.addAttribute("totalItems", paginationMetadata.get("totalItems"));

        return "pages/insured/index";
    }

    @GetMapping("/create")
    public String renderCreateForm(@ModelAttribute UserDTO user){
        return "pages/insured/create";
    }

    @PostMapping("/create")
    public String createInsured(
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors())
            return renderCreateForm(user);

        userService.create(user);
        redirectAttributes.addFlashAttribute("success", "Pojištěnec přidán.");

        redirectAttributes.addAttribute("userId", user.getUserId());

        return "redirect:/insured/{userId}/detail";
    }

    @GetMapping("/{userId}/detail")
    public String renderDetail(
            @PathVariable long userId,
            Model model,
            @RequestParam(defaultValue = "1") int page
    ){
        // Define the page size
        int pageSize = 5;

        UserDTO user = userService.getById(userId);

        Set<PolicyDTO> policies = user.getPolicies();

        // Convert Set to List
        List<PolicyDTO> policyList = new ArrayList<>(policies);

        // Paginate the filtered list
        List<PolicyDTO> paginatedList = paginationService.paginate(policyList, page, pageSize);

        // Get calculated metadata
        Map<String, Integer> paginationMetadata = paginationService.getPaginationMetadata(policyList, page, pageSize);

        // Add attributes to the model
        model.addAttribute("user", user);
        model.addAttribute("policies", paginatedList);
        model.addAttribute("currentPage", paginationMetadata.get("currentPage"));
        model.addAttribute("totalPages", paginationMetadata.get("totalPages"));
        model.addAttribute("totalItems", paginationMetadata.get("totalItems"));

        return "pages/insured/detail";
    }


    @GetMapping("/{userId}/edit")
    public String renderEditForm(
            @PathVariable Long userId,
            UserDTO user
    ){
        UserDTO fetchedUser = userService.getById(userId);
        userMapper.updateUserProfileDTO(fetchedUser, user);

        return "pages/insured/edit";
    }

    @PostMapping("{userId}/edit")
    public String editInsured(
            @PathVariable long userId,
            @Valid UserDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors())
            return renderEditForm(userId, user);

        user.setUserId(userId);
        userService.edit(user);
        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return "redirect:/insured";
    }

    @GetMapping("{userId}/delete")
    public String deleteInsured(
            @PathVariable long userId,
            RedirectAttributes redirectAttributes
    ){
        userService.remove(userId);
        redirectAttributes.addFlashAttribute("success", "Uživatel smazán.");

        return "redirect:/insured";
    }

    @ExceptionHandler({DuplicateEmailException.class})
    public String handleDuplicateEmailException(
            RedirectAttributes redirectAttributes,
            DuplicateEmailException ex
    ){
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/insured";
    }

    @ExceptionHandler({EmailAlreadyRegisteredException.class})
    public String handleEmailAlreadyRegisteredException(
            RedirectAttributes redirectAttributes,
            EmailAlreadyRegisteredException ex
    ){
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/insured";
    }

    @ExceptionHandler({UserNotFoundException.class})
    public String handleUserNotFoundException(
            RedirectAttributes redirectAttributes,
            UserNotFoundException ex
    ){
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/insured";
    }
}
