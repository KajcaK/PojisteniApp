package eu.dickovadev.pojisteniapp.controllers;

import eu.dickovadev.pojisteniapp.models.dto.UserDTO;
import eu.dickovadev.pojisteniapp.models.responses.UserDetailResponse;
import eu.dickovadev.pojisteniapp.models.responses.UserIndexResponse;
import eu.dickovadev.pojisteniapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/insured")
public class InsuredController {

    private final UserService userService;

    @Autowired
    public InsuredController(UserService userService) {
        this.userService = userService;
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

        UserIndexResponse response = userService.getPaginatedUsers(query, searchField, page, pageSize);

        // Add attributes to the model
        model.addAttribute("insuredList", response.getPaginatedList());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Zákazníci");

        return "pages/insured/index";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/create")
    public String renderCreateForm(
            @ModelAttribute UserDTO user,
            Model model
    ) {
        model.addAttribute("pageTitle", "Vytvořit profil");
        return "pages/insured/create";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public String createInsured(
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            Model model
    ) {
        if (result.hasErrors())
            return renderCreateForm(user, model);

        long userId = userService.create(user);

        // Store userId in session after creation
        request.getSession().setAttribute("userId", userId);

        redirectAttributes.addFlashAttribute("success", "Pojištěnec přidán.");
        redirectAttributes.addAttribute("userId", userId);

        return "redirect:/insured/{userId}/detail";
    }

    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}/detail")
    public String renderDetail(
            @PathVariable long userId,
            Model model,
            @RequestParam(defaultValue = "1") int page
    ) {
        // Define the page size
        int pageSize = 4;

        // Call the service method, passing the logged-in user's ID
        UserDetailResponse response = userService.getUserWithPaginatedPolicies(userId, page, pageSize);

        // Add attributes to the model
        model.addAttribute("user", response.getUser());
        model.addAttribute("policies", response.getPaginatedPolicies());
        model.addAttribute("currentPage", response.getPaginationMetadata().get("currentPage"));
        model.addAttribute("totalPages", response.getPaginationMetadata().get("totalPages"));
        model.addAttribute("totalItems", response.getPaginationMetadata().get("totalItems"));
        model.addAttribute("pageTitle", "Detail");

        return "pages/insured/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/edit")
    public String renderEditForm(
            @PathVariable Long userId,
            @ModelAttribute UserDTO user,
            Model model
    ) {
        userService.getUserEditData(userId, user);

        model.addAttribute("pageTitle", "Upravit osobu");
        return "pages/insured/edit";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{userId}/edit")
    public String editInsured(
            @PathVariable long userId,
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors())
            return renderEditForm(userId, user, model);

        userService.editByAdmin(user, userId);

        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return "redirect:/insured/" + userId + "/detail";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{userId}/delete")
    public String deleteInsured(
            @PathVariable long userId,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request
    ) {
        userService.remove(userId);

        redirectAttributes.addFlashAttribute("success", "Uživatel smazán.");
        return "redirect:/insured";
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @GetMapping("/{userId}/customer-edit")
    public String renderCustomerEditForm(
            @PathVariable Long userId,
            @ModelAttribute UserDTO user,
            Model model
    ) {
        userService.getUserEditData(userId, user);

        model.addAttribute("pageTitle", "Upravit osobu");
        return "pages/insured/customer-edit";
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @PostMapping("/{userId}/customer-edit")
    public String editInsuredByCustomer(
            @PathVariable long userId,
            @Valid @ModelAttribute UserDTO user,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors())
            return renderEditForm(userId, user, model);

        userService.editByCustomer(user, userId);

        redirectAttributes.addFlashAttribute("success", "Změny uloženy.");

        return "redirect:/insured/" + userId + "/detail";
    }
}
